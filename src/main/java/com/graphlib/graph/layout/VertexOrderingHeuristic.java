package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

public class VertexOrderingHeuristic {

	private static final Logger LOGGER = Logger.getLogger(VertexOrderingHeuristic.class);

	private static final int MAX_ITERATIONS = 24;

	private final GraphLayout graph;

	private final Map<LayoutEdge, Set<LayoutNode>> edgeVirtualVerticesMap;

	private Map<LayoutNode, Double> vertexMedianValues;
	
	private int idCounter = 1;

	public VertexOrderingHeuristic(GraphLayout graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}

		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}
		this.graph = graph;
		this.vertexMedianValues = new HashMap<>();
		this.edgeVirtualVerticesMap = new HashMap<>();
		for(LayoutNode v : graph.getAllVertices()) {
			if(v.getId() > idCounter) {
				idCounter = v.getId() + 1;
			}
		}
	}

	public void orderVertices() {
		createVirtualNodesAndEdges();
		/*
		 * Reverse breadth first iteration, starting from vertices of lowest rank.
		 */
		List<LayoutNode> vList = new ArrayList<>();
		for (LayoutNode v : graph.getAllVertices()) {
			if (graph.getOutDegreeFor(v) == 0) {
				vList.add(v);
			}
		}
		Collections.sort(vList, new Comparator<LayoutNode>() {

			@Override
			public int compare(LayoutNode o1, LayoutNode o2) {
				return Integer.compare(o1.getRank(), o2.getRank());
			}

		});
		
		if(vList.isEmpty()) {
			throw new IllegalStateException("Graph has no node without outgoing edges.");
		}
		
		Map<Integer, List<LayoutNode>> vertexOrder = new HashMap<>();
		
		Queue<LayoutNode> bfsQueue = new LinkedList<>();
		bfsQueue.addAll(vList);
		Set<LayoutNode> visited = new HashSet<>();
		
		while (!bfsQueue.isEmpty()) {
			LayoutNode v = bfsQueue.remove();
			if(visited.contains(v)) {
				continue;
			}
			visited.add(v);
			if (!vertexOrder.containsKey(v.getRank())) {
				vertexOrder.put(new Integer(v.getRank()), new ArrayList<LayoutNode>());
			}
			vertexOrder.get(v.getRank()).add(v);
			for(LayoutEdge e : graph.getIncomingEdgesFor(v)) {
				if(!visited.contains(e.getSourceVertex())) {
					bfsQueue.add(e.getSourceVertex());
				}
			}
		}

		graph.setOrder(vertexOrder);
		
		LOGGER.info("Initial vertex order: " + vertexOrder);

		for (int i = 0; i < MAX_ITERATIONS; i++) {
			Map<Integer, List<LayoutNode>> oldVertexOrder = graph.getOrder();
			int oldOrderCrossings = graph.getEdgeCrossings();
			if(oldOrderCrossings == 0) {
				break;
			}
			wmedian(i);
			transpose();
			int newOrderCrossings = graph.getEdgeCrossings();
			if (newOrderCrossings == 0) {
				break;
			} else if (oldOrderCrossings < newOrderCrossings) {
				graph.setOrder(oldVertexOrder);
				vertexOrder = oldVertexOrder;
			}

			LOGGER.info("Total edge crossings: "
					+ ((oldOrderCrossings < newOrderCrossings) ? oldOrderCrossings : newOrderCrossings));
		}

		LOGGER.info("Final vertex order: " + vertexOrder);
	}

	private void transpose() {
		boolean improved = true;
		int maxRank = graph.getMaxRank();

		while (improved) {
			improved = false;

			for (int rank = 0; rank <= maxRank; rank++) {
				for (int i = 0; i <= graph.getRankOrder(rank).size() - 2; i++) {
					List<LayoutNode> vertices = new ArrayList<>(graph.getRankOrder(rank));
					LayoutNode v = vertices.get(i);
					LayoutNode w = vertices.get(i + 1);
					int oldOrderEdgeCrossings = graph.getAdjacentRankCrossings(rank);
					Map<Integer, List<LayoutNode>> oldVertexOrder = graph.getOrder();
					/*
					 * Swap v and w and compare edge crossings with the old
					 * order.
					 */
					vertices.set(i, w);
					vertices.set(i + 1, v);

					graph.setRankOrder(rank, vertices);
					int newOrderEdgeCrossings = graph.getAdjacentRankCrossings(rank);
					if (oldOrderEdgeCrossings < newOrderEdgeCrossings) {
						graph.setOrder(oldVertexOrder);
					} else {
						improved = true;
						LOGGER.debug("Swapped " + v + " and " + w);
					}
				}
			}
		}
	}

	private void wmedian(int iteration) {
		int maxRank = graph.getMaxRank();
		if (maxRank == GraphLayoutParameters.MIN_RANK) {
			return;
		}

		if (iteration % 2 == 0) {
			for (int rank = 1; rank <= maxRank; rank++) {
				for (LayoutNode v : graph.getRankOrder(rank)) {
					vertexMedianValues.put(v, medianValue(v, rank - 1));
				}

				/*
				 * Sort vertices with the current rank based on median values.
				 */
				List<LayoutNode> vertices = new ArrayList<>(graph.getRankOrder(rank));
				List<Double> medianValues = new ArrayList<>(new HashSet<>(vertexMedianValues.values()));
				Collections.sort(medianValues);
				final Map<LayoutNode, Integer> vertexPositions = new HashMap<>();
				int position = 0;
				for (Double m : medianValues) {
					for (int i = 0; i < vertices.size(); i++) {
						if (vertexMedianValues.get(vertices.get(i)).equals(m)) {
							if (m.equals(-1.0)) {
								vertexPositions.put(vertices.get(i), i);
							} else {
								while (vertexPositions.values().contains(position)) {
									position++;
								}
								vertexPositions.put(vertices.get(i), position);
							}
						}
					}
				}

				Collections.sort(vertices, new Comparator<LayoutNode>() {

					@Override
					public int compare(LayoutNode o1, LayoutNode o2) {
						return Integer.compare(vertexPositions.get(o1), vertexPositions.get(o2));
					}

				});

				graph.setRankOrder(rank, vertices);
			}
		}
	}

	private double medianValue(LayoutNode v, int rank) {
		List<Integer> positions = adjacentPosition(v, rank);
		Collections.sort(positions);
		int m = positions.size() / 2;
		if (positions.isEmpty()) {
			return -1.0;
		} else if (positions.size() == 1) {
			return positions.get(0);
		} else if (positions.size() == 2) {
			return (positions.get(0) + positions.get(1)) / 2.0;
		} else if (positions.size() % 2 == 1) {
			return positions.get(m);
		} else {
			int left = positions.get(m - 1) - positions.get(0);
			int right = positions.get(positions.size() - 1) - positions.get(m);
			return (positions.get(m - 1) * right + positions.get(m) * left) / (left + right);
		}
	}

	/**
	 * Returns the list of positions of the vertices with the given rank,
	 * adjacent to the given vertex.
	 * 
	 * @param v
	 *            Vertex whose adjacent vertices are considered
	 * @param rank
	 *            Rank of the vertices to be considered
	 * @return List of positions
	 */
	private List<Integer> adjacentPosition(LayoutNode v, int rank) {
		List<Integer> positions = new ArrayList<>();
		List<LayoutNode> order = new ArrayList<>(graph.getRankOrder(rank));

		for (LayoutNode orderVertex : order) {
			for (LayoutEdge e : graph.getAllEdges(orderVertex)) {
				if (e.getSourceVertex().equals(v) || e.getTargetVertex().equals(v)) {
					positions.add(order.indexOf(orderVertex));
				}
			}
		}

		return positions;
	}

	/**
	 * Replaces edges between nodes that are more than one rank apart by chains
	 * of unit length edges between virtual nodes.
	 */
	private void createVirtualNodesAndEdges() {
		int idCounter = 1;
		for(LayoutNode v : graph.getAllVertices()) {
			if(v.getId() >= idCounter) {
				idCounter = v.getId() + 1;
			}
		}
		LayoutNodeFactory vertexFactory = new LayoutNodeFactory(idCounter);
		Set<LayoutEdge> edges = graph.getAllEdges();
		for (LayoutEdge e : edges) {
			
			int labelRank = -1;
			if(!e.getLabel().isEmpty()) {
				labelRank = (e.getSourceVertex().getRank() + e.getTargetVertex().getRank()) / 2;
			}
			
			LayoutNode previousNode = e.getTargetVertex();
			for(int rank = e.getTargetVertex().getRank() + 1; rank <= e.getSourceVertex().getRank(); rank++) {
				LayoutNode virtualNode = null;
				if(rank < e.getSourceVertex().getRank()) {
					if(rank == labelRank) {
						/*
						 * Create a label node.
						 */
						virtualNode = vertexFactory.createVertex();
						virtualNode.setLeftWidth(GraphLayoutParameters.NODE_SEPARATION);
						//FIXME: Set right width and height based on edge label dimensions.
						virtualNode.setRightWidth(50);
						virtualNode.setHeight(15);
						virtualNode.setRank(rank);
						virtualNode.setVirtual(true);
						virtualNode.setLabel(true);
						virtualNode.setLabel(e.getLabel());
						graph.addVertex(virtualNode);
						if(!this.edgeVirtualVerticesMap.containsKey(e)) {
							this.edgeVirtualVerticesMap.put(e, new HashSet<LayoutNode>()); 
						}
						this.edgeVirtualVerticesMap.get(e).add(virtualNode);
					} else {
						/*
						 * Create a plain virtual node.
						 */
						virtualNode = vertexFactory.createVertex();
						virtualNode.setLeftWidth(GraphLayoutParameters.NODE_SEPARATION/2 + 1);
						virtualNode.setRightWidth(GraphLayoutParameters.NODE_SEPARATION/2 + 1);
						virtualNode.setHeight(1);
						virtualNode.setRank(rank);
						virtualNode.setVirtual(true);
						graph.addVertex(virtualNode);
						if(!this.edgeVirtualVerticesMap.containsKey(e)) {
							this.edgeVirtualVerticesMap.put(e, new HashSet<LayoutNode>()); 
						}
						this.edgeVirtualVerticesMap.get(e).add(virtualNode);
					}
				} else {
					virtualNode = e.getSourceVertex();
				}
				
				LayoutEdge virtualEdge = graph.getEdgeFactory().createEdge(virtualNode, previousNode);
				virtualEdge.setVirtual(true);
				virtualEdge.setLabel("");
				virtualEdge.setMinLength(e.getMinLength());
				virtualEdge.setEdgeWeight(e.getEdgeWeight());
				graph.addEdge(virtualEdge);
				previousNode = virtualNode;
			}
		}
		
		graph.removeAllEdges(edges);
	}
}
