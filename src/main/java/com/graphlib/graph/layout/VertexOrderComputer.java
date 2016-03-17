package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.graphlib.graph.core.Graph;
import com.graphlib.graph.core.VertexFactory;
import com.graphlib.graph.core.WeightedEdge;
import com.graphlib.graph.iterators.BreadthFirstIterator;

public final class VertexOrderComputer<V, E extends WeightedEdge<V, E>> {

	private static final Logger LOGGER = Logger.getLogger(VertexOrderComputer.class);

	private static final Integer MIN_RANK = 0;

	private static final int MAX_ITERATIONS = 24;

	private final Graph<V, E> graph;

	private final Map<V, Integer> vertexRankMap;

	private final VertexFactory<V, E> vertexFactory;

	private final Set<V> virtualVertices;

	private final Set<E> virtualEdges;

	private final Map<E, Set<V>> edgeVirtualVerticesMap;

	private Map<Integer, LinkedHashSet<V>> vertexOrder;

	private Map<V, Double> vertexMedianValues;

	public VertexOrderComputer(Graph<V, E> graph, Map<V, Integer> vertexRankMap, VertexFactory<V, E> vertexFactory) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}

		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}
		this.graph = graph;
		for (V v : graph.getAllVertices()) {
			if (!vertexRankMap.containsKey(v)) {
				throw new IllegalArgumentException("Vertex rank map does not contain the rank for :" + v);
			}
		}
		this.vertexRankMap = new HashMap<>();
		this.vertexRankMap.putAll(vertexRankMap);
		this.vertexOrder = new HashMap<>();
		this.vertexMedianValues = new HashMap<>();
		this.vertexFactory = vertexFactory;
		this.virtualVertices = new HashSet<>();
		this.virtualEdges = new HashSet<>();
		this.edgeVirtualVerticesMap = new HashMap<>();
	}

	public void orderVertices() {
		createVirtualNodesAndEdges();
		/*
		 * Breadth first iteration, starting from vertex of lowest rank.
		 */
		List<V> vList = new ArrayList<>();
		for (V v : graph.getAllVertices()) {
			if (graph.getInDegreeFor(v) == 0) {
				vList.add(v);
			}
		}
		Collections.sort(vList, new Comparator<V>() {

			@Override
			public int compare(V o1, V o2) {
				return vertexRankMap.get(o1).compareTo(vertexRankMap.get(o2));
			}

		});

		Set<V> visited = new HashSet<>();
		while (!vList.isEmpty()) {
			V minV = vList.remove(0);
			if (visited.contains(minV)) {
				continue;
			}
			BreadthFirstIterator<V, E> bfsIt = new BreadthFirstIterator<>(graph, minV);

			while (bfsIt.hasNext()) {
				V v = bfsIt.next();
				visited.add(v);
				if (!vertexOrder.containsKey(vertexRankMap.get(v))) {
					vertexOrder.put(vertexRankMap.get(v), new LinkedHashSet<V>());
				}
				this.vertexOrder.get(vertexRankMap.get(v)).add(v);
			}
		}

		LOGGER.info("Initial vertex order: " + vertexOrder);

		for (int i = 0; i < MAX_ITERATIONS; i++) {
			Map<Integer, LinkedHashSet<V>> oldVertexOrder = copyVertexOrder(vertexOrder);
			wmedian(i);
			transpose();
			int oldOrderCrossings = getCrossings(oldVertexOrder);
			int newOrderCrossings = getCrossings(vertexOrder);
			if (newOrderCrossings == 0) {
				break;
			} else if (oldOrderCrossings < newOrderCrossings) {
				vertexOrder = oldVertexOrder;
			}

			LOGGER.info("Total edge crossings: "
					+ ((oldOrderCrossings < newOrderCrossings) ? oldOrderCrossings : newOrderCrossings));
		}

		LOGGER.info("Final vertex order: " + vertexOrder);
	}

	private Map<Integer, LinkedHashSet<V>> copyVertexOrder(Map<Integer, LinkedHashSet<V>> vOrder) {
		Map<Integer, LinkedHashSet<V>> bestVertexOrder = new HashMap<>();
		for (Integer i : vOrder.keySet()) {
			bestVertexOrder.put(i, new LinkedHashSet<>(vOrder.get(i)));
		}
		return bestVertexOrder;
	}

	/**
	 * Returns the number of edge crossings in the given vertex order.
	 * 
	 * @param vertexOrder
	 * @return
	 */
	private int getCrossings(Map<Integer, LinkedHashSet<V>> vertexOrder) {
		int crossings = 0;
		for (int i = MIN_RANK; i + 1 < vertexOrder.keySet().size(); i++) {
			crossings += getAdjacentRankCrossings(i, i + 1);
		}
		return crossings;
	}

	private void transpose() {
		boolean improved = true;
		List<Integer> ranks = new ArrayList<>(vertexRankMap.values());
		Collections.sort(ranks);
		int maxRank = ranks.get(ranks.size() - 1);

		while (improved) {
			improved = false;

			for (int rank = 0; rank <= maxRank; rank++) {
				for (int i = 0; i <= vertexOrder.get(rank).size() - 2; i++) {
					List<V> vertices = new ArrayList<>(vertexOrder.get(rank));
					V v = vertices.get(i);
					V w = vertices.get(i + 1);
					int oldOrderEdgeCrossings = getAdjacentRankCrossings(rank);
					Map<Integer, LinkedHashSet<V>> oldVertexOrder = copyVertexOrder(vertexOrder);
					/*
					 * Swap v and w and compare edge crossings with the old
					 * order.
					 */
					vertices.set(i, w);
					vertices.set(i + 1, v);

					vertexOrder.put(rank, new LinkedHashSet<>(vertices));
					int newOrderEdgeCrossings = getAdjacentRankCrossings(rank);
					if (oldOrderEdgeCrossings < newOrderEdgeCrossings) {
						vertexOrder = oldVertexOrder;
					} else {
						improved = true;
						LOGGER.info("Swapped " + v + " and " + w);
					}
				}
			}
		}
	}

	private int getAdjacentRankCrossings(int lowerRank, int higherRank) {
		final List<V> lowerRankOrder = new ArrayList<V>(vertexOrder.get(lowerRank));
		final List<V> higherRankOrder = new ArrayList<V>(vertexOrder.get(higherRank));

		Map<V, Integer> higherRankVertexEdgeCount = new HashMap<>();

		int crossings = 0;

		int higherRankIndex = 0;
		for (higherRankIndex = 0; higherRankIndex < higherRankOrder.size(); higherRankIndex++) {
			higherRankVertexEdgeCount.put(higherRankOrder.get(higherRankIndex), 0);
		}

		int lastHigherRankEdgeIndex = 0;
		for (int lowerRankIndex = 0; lowerRankIndex < lowerRankOrder.size(); lowerRankIndex++) {
			V lowerRankVertex = lowerRankOrder.get(lowerRankIndex);
			Set<E> outEdges = this.graph.getOutgoingEdgesFor(lowerRankVertex);
			List<V> higherRankVertices = new ArrayList<>();
			for (E e : outEdges) {
				higherRankVertices.add(e.getTargetVertex());
			}
			/*
			 * Consider the vertices to which a vertex has outgoing edges in the
			 * order in which they appear in the higher rank to avoid counting
			 * crossings of edges from same vertex.
			 */
			Collections.sort(higherRankVertices, new Comparator<V>() {

				@Override
				public int compare(V o1, V o2) {
					return Integer.compare(higherRankOrder.indexOf(o1), higherRankOrder.indexOf(o2));
				}

			});
			for (V higherRankVertex : higherRankVertices) {
				if (higherRankOrder.contains(higherRankVertex)) {
					higherRankIndex = higherRankOrder.indexOf(higherRankVertex);
					if (higherRankIndex < lastHigherRankEdgeIndex) {
						/*
						 * There are edge crossings.
						 */
						for (int j = higherRankIndex + 1; j <= lastHigherRankEdgeIndex; j++) {
							crossings += higherRankVertexEdgeCount.get(higherRankOrder.get(j));
						}
					} else {
						lastHigherRankEdgeIndex = higherRankIndex;
					}
					higherRankVertexEdgeCount.put(higherRankVertex,
							higherRankVertexEdgeCount.get(higherRankVertex) + 1);
				}
			}
		}

		return crossings;
	}

	private int getAdjacentRankCrossings(int rank) {
		if (!vertexOrder.containsKey(rank - 1) && !vertexOrder.containsKey(rank + 1)) {
			throw new IllegalArgumentException("Vertex order doesn't have ranks adjacent to specified rank.");
		}

		int crossings = 0;

		if(vertexOrder.containsKey(rank - 1)) {
			crossings += getAdjacentRankCrossings(rank - 1, rank);
		}
		
		if(vertexOrder.containsKey(rank + 1)) {
			crossings += getAdjacentRankCrossings(rank, rank + 1);
		}
		return crossings;
	}

	private void wmedian(int iteration) {
		List<Integer> ranks = new ArrayList<>(vertexRankMap.values());
		if (ranks.size() == 1) {
			return;
		}

		Collections.sort(ranks);
		int maxRank = ranks.get(ranks.size() - 1);

		if (iteration % 2 == 0) {
			for (int rank = 1; rank <= maxRank; rank++) {
				for (V v : vertexOrder.get(rank)) {
					vertexMedianValues.put(v, medianValue(v, rank - 1));
				}

				/*
				 * Sort vertices with the current rank based on median values.
				 */
				List<V> vertices = new ArrayList<>(vertexOrder.get(rank));
				List<Double> medianValues = new ArrayList<>(new HashSet<>(vertexMedianValues.values()));
				Collections.sort(medianValues);
				final Map<V, Integer> vertexPositions = new HashMap<>();
				int position = 0;
				for(Double m : medianValues) {
					for(int i = 0; i < vertices.size(); i++) {
						if(vertexMedianValues.get(vertices.get(i)).equals(m)) {
							if(m == -1.0) {
								vertexPositions.put(vertices.get(i), i);
							} else {
								while(vertexPositions.values().contains(position)) {
									position++;
								}
								vertexPositions.put(vertices.get(i), position);
							}
						}
					}
				}
				
				Collections.sort(vertices, new Comparator<V>() {

					@Override
					public int compare(V o1, V o2) {
						return Integer.compare(vertexPositions.get(o1), vertexPositions.get(o2));
					}

				});

				vertexOrder.get(rank).clear();
				vertexOrder.get(rank).addAll(vertices);
			}
		}
	}

	private double medianValue(V v, int rank) {
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
	private List<Integer> adjacentPosition(V v, int rank) {
		List<Integer> positions = new ArrayList<>();
		List<V> order = new ArrayList<>(vertexOrder.get(rank));

		for (V orderVertex : order) {
			for (E e : graph.getAllEdges(orderVertex)) {
				if (e.getSourceVertex().equals(v) || e.getTargetVertex().equals(v)) {
					positions.add(order.indexOf(orderVertex));
				}
			}
		}

		return positions;
	}

	private void createVirtualNodesAndEdges() {
		Set<E> edgesToRemove = new HashSet<>();
		for (E e : graph.getAllEdges()) {
			int length = vertexRankMap.get(e.getSourceVertex()) - vertexRankMap.get(e.getTargetVertex());
			if (Math.abs(length) > VertexRankComputer.MIN_EDGE_LENGTH) {
				/*
				 * Edge is not tight. Replace with virtual nodes and edges.
				 */
				int numVertices = Math.abs(length) - VertexRankComputer.MIN_EDGE_LENGTH;

				V previousVertex = e.getSourceVertex();
				Set<V> newVertices = new HashSet<>();
				for (int i = 0; i < numVertices; i++) {
					V virtualVertex = vertexFactory.createVertex();
					E virtualEdge = graph.getEdgeFactory().createEdge(previousVertex, virtualVertex);
					this.vertexRankMap.put(virtualVertex,
							vertexRankMap.get(previousVertex) + VertexRankComputer.MIN_EDGE_LENGTH);
					this.virtualVertices.add(virtualVertex);
					this.virtualEdges.add(virtualEdge);
					newVertices.add(virtualVertex);
					previousVertex = virtualVertex;
				}
				E lastEdge = graph.getEdgeFactory().createEdge(previousVertex, e.getTargetVertex());
				this.virtualEdges.add(lastEdge);
				this.edgeVirtualVerticesMap.put(e, newVertices);
				edgesToRemove.add(e);
			}
		}

		graph.removeAllEdges(edgesToRemove);
		graph.addVertices(this.virtualVertices);
		graph.addEdges(this.virtualEdges);
	}
}
