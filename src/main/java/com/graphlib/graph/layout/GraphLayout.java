package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.graphlib.graph.core.AbstractGraph;

public class GraphLayout extends AbstractGraph<LayoutNode, LayoutEdge> {

	private RenderingContext rc;

	/*
	 * Indicates if the edges have labels.
	 */
	boolean hasLabels = false;

	public GraphLayout() {
		super();
		vertices = new HashSet<LayoutNode>();
		edges = new HashSet<LayoutEdge>();
		edgeFactory = new LayoutEdgeFactory();
	}

	public RenderingContext getRenderingContext() {
		return rc;
	}

	public void setRenderingContext(RenderingContext rc) {
		this.rc = rc;
	}

	public boolean isHasLabels() {
		return hasLabels;
	}

	public void setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
	}

	public Map<LayoutNode, Integer> getVertexRankMap() {
		Map<LayoutNode, Integer> rankMap = new HashMap<>();
		for (LayoutNode n : getAllVertices()) {
			rankMap.put(n, n.getRank());
		}
		return rankMap;
	}

	public int getMaxRank() {
		int maxRank = GraphLayoutParameters.MIN_RANK;
		for (LayoutNode n : getAllVertices()) {
			if (maxRank < n.getRank()) {
				maxRank = n.getRank();
			}
		}

		return maxRank;
	}

	/**
	 * Returns the order of vertices for the given rank.
	 * 
	 * @param rank
	 * @return
	 */
	public List<LayoutNode> getRankOrder(int rank) {
		List<LayoutNode> order = new ArrayList<>();
		for (LayoutNode v : getAllVertices()) {
			if (v.getRank() == rank) {
				order.add(v);
			}
		}

		Collections.sort(order, new Comparator<LayoutNode>() {

			@Override
			public int compare(LayoutNode o1, LayoutNode o2) {
				return Integer.compare(o1.getOrder(), o2.getOrder());
			}

		});

		return order;
	}

	public void setRankOrder(int rank, List<LayoutNode> order) {
		List<LayoutNode> oldRankOrder = getRankOrder(rank);
		for (LayoutNode n : oldRankOrder) {
			n.setOrder(order.indexOf(n));
		}
	}

	public Map<Integer, List<LayoutNode>> getOrder() {
		Map<Integer, List<LayoutNode>> rankOrderMap = new HashMap<>();
		for (int i = GraphLayoutParameters.MIN_RANK; i <= getMaxRank(); i++) {
			rankOrderMap.put(i, getRankOrder(i));
		}

		return rankOrderMap;
	}

	public void setOrder(Map<Integer, List<LayoutNode>> rankOrderMap) {
		for (Integer rank : rankOrderMap.keySet()) {
			setRankOrder(rank, rankOrderMap.get(rank));
		}
	}

	public void rank() {
		NetworkSimplex ns = new NetworkSimplex(this);
		ns.iterate(5, NetworkSimplex.BalancingMode.TOP_BOTTOM);
	}

	public void order() {
		VertexOrderingHeuristic orderingHeuristic = new VertexOrderingHeuristic(this);
		orderingHeuristic.orderVertices();
	}

	public void positionVertices() {
		VertexPositionComputer positionComputer = new VertexPositionComputer(this);
		positionComputer.assignPosition();
	}

	public void drawEdges() {

	}

	/**
	 * Returns the number of edge crossings in the given vertex order.
	 * 
	 * @param vertexOrder
	 * @return
	 */
	public int getEdgeCrossings() {
		int crossings = 0;
		int maxRank = getMaxRank();
		for (int i = GraphLayoutParameters.MIN_RANK; i <= maxRank; i++) {
			crossings += getAdjacentRankCrossings(i, i + 1);
		}
		return crossings;
	}

	public int getAdjacentRankCrossings(int rank) {
		/*
		 * The rank must have at least one adjacent rank.
		 */
		int maxRank = getMaxRank();
		if (rank == GraphLayoutParameters.MIN_RANK && rank == maxRank) {
			throw new IllegalArgumentException("Vertex order doesn't have ranks adjacent to specified rank.");
		}

		int crossings = 0;

		if (rank > GraphLayoutParameters.MIN_RANK) {
			crossings += getAdjacentRankCrossings(rank - 1, rank);
		}

		if (rank < maxRank) {
			crossings += getAdjacentRankCrossings(rank, rank + 1);
		}
		return crossings;
	}

	public int getAdjacentRankCrossings(int lowerRank, int higherRank) {
		final List<LayoutNode> lowerRankOrder = getRankOrder(lowerRank);
		final List<LayoutNode> higherRankOrder = getRankOrder(higherRank);

		Map<LayoutNode, Integer> higherRankVertexEdgeCount = new HashMap<>();

		int crossings = 0;

		int higherRankIndex = 0;
		for (higherRankIndex = 0; higherRankIndex < higherRankOrder.size(); higherRankIndex++) {
			higherRankVertexEdgeCount.put(higherRankOrder.get(higherRankIndex), 0);
		}

		int lastHigherRankEdgeIndex = 0;
		for (int lowerRankIndex = 0; lowerRankIndex < lowerRankOrder.size(); lowerRankIndex++) {
			LayoutNode lowerRankVertex = lowerRankOrder.get(lowerRankIndex);
			Set<LayoutEdge> outEdges = this.getOutgoingEdgesFor(lowerRankVertex);
			List<LayoutNode> higherRankVertices = new ArrayList<>();
			for (LayoutEdge e : outEdges) {
				higherRankVertices.add(e.getTargetVertex());
			}
			/*
			 * Consider the vertices to which a vertex has outgoing edges in the
			 * order in which they appear in the higher rank to avoid counting
			 * crossings of edges from same vertex.
			 */
			Collections.sort(higherRankVertices, new Comparator<LayoutNode>() {

				@Override
				public int compare(LayoutNode o1, LayoutNode o2) {
					return Integer.compare(higherRankOrder.indexOf(o1), higherRankOrder.indexOf(o2));
				}

			});

			for (LayoutNode higherRankVertex : higherRankVertices) {
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

	public Box computeBoundingBox() {
		double minLeftX = Integer.MAX_VALUE;
		double maxRightX = Integer.MIN_VALUE;
		double minLowerY = Integer.MAX_VALUE;
		double maxUpperY = Integer.MIN_VALUE;
		int maxRank = this.getMaxRank();

		for (int i = 0; i <= maxRank; i++) {
			List<LayoutNode> nodes = this.getRankOrder(i);
			boolean hasNormalNode = false;
			for (int j = 0; j < nodes.size(); j++) {
				if (!nodes.get(j).isVirtual()) {
					double nodeLeftX = nodes.get(j).getxCoordinate() - nodes.get(j).getLeftWidth();
					minLeftX = Math.min(minLeftX, nodeLeftX);
					hasNormalNode = true;
				}
			}
			if (!hasNormalNode) {
				continue;
			}

			for (int j = nodes.size() - 1; j >= 0; j--) {
				if (!nodes.get(j).isVirtual()) {
					double nodeRightX = nodes.get(j).getxCoordinate() + nodes.get(j).getRightWidth();
					maxRightX = Math.max(maxRightX, nodeRightX);
				}
			}
		}
		
		List<LayoutNode> nodes = this.getRankOrder(maxRank);
		if(!nodes.isEmpty()) {
			minLowerY = nodes.get(0).getyCoordinate() - nodes.get(0).getHeightBelowCenter();
		}
		
		nodes = this.getRankOrder(0);
		if(!nodes.isEmpty()) {
			maxUpperY = nodes.get(0).getyCoordinate() + nodes.get(0).getHeightAboveCenter();
		}

		Point ll = new Point(minLeftX, minLowerY);
		Point ur = new Point(maxRightX, maxUpperY);
		return new Box(ll, ur);
	}
}
