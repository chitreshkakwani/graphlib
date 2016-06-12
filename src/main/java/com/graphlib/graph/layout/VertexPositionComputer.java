package com.graphlib.graph.layout;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VertexPositionComputer {

	private static final Logger LOGGER = LoggerFactory.getLogger(VertexPositionComputer.class);

	private final GraphLayout graph;

	private int idCounter = 1;

	public VertexPositionComputer(GraphLayout graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}

		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}
		this.graph = graph;
		for (LayoutNode v : graph.getAllVertices()) {
			if (v.getId() > idCounter) {
				idCounter = v.getId() + 1;
			}
		}
	}

	public void assignPosition() {
		for (LayoutNode n : graph.getAllVertices()) {
			LOGGER.debug("Width of node " + n + " : [" + n.getLeftWidth() + ", " + n.getRightWidth() + "]");
		}
		Map<Integer, List<LayoutNode>> vertexOrder = graph.getOrder();
		assignYCoordinates();
		createAuxiliaryNodesAndEdges();
		NetworkSimplex ns = new NetworkSimplex(graph);
		ns.iterate(100, NetworkSimplex.BalancingMode.LEFT_RIGHT);

		for (int i : vertexOrder.keySet()) {
			for (int j = 0; j < vertexOrder.get(i).size(); j++) {
				LayoutNode n = vertexOrder.get(i).get(j);
				n.setxCoordinate(n.getRank());
				n.setRank(i);
				LOGGER.debug("Co-ordinate assignment for " + n + " : [" + n.getxCoordinate()
						+ ", " + n.getyCoordinate() + "]");
			}
		}
	}

	private void assignYCoordinates() {
		int maxRank = graph.getMaxRank();
		double[] rankHeights1 = new double[maxRank + 1];
		double[] rankHeights2 = new double[maxRank + 1];
		double maxHeight = 0;
		for (int r = 0; r <= maxRank; r++) {
			rankHeights1[r] = rankHeights2[r] = 0;
			for (LayoutNode n : graph.getRankOrder(r)) {
				/*
				 * Symmetry assumed for a node's height above and below below
				 * the center line.
				 */
				double ht2 = n.getHeight() / 2;
				if (rankHeights1[r] < ht2) {
					rankHeights1[r] = ht2;
				}
				if (rankHeights2[r] < ht2) {
					rankHeights2[r] = ht2;
				}
			}
		}

		LayoutNode node = graph.getRankOrder(maxRank).get(0);
		node.setyCoordinate(rankHeights1[maxRank]);
		LOGGER.debug("Initial Y Co-ordinate assignment for rank " + maxRank + " : " + node.getyCoordinate());
		int r = maxRank;
		while (--r >= 0) {
			LayoutNode prev = graph.getRankOrder(r + 1).get(0);
			double delta = rankHeights2[r + 1] + rankHeights1[r] + GraphLayoutParameters.RANK_SEPARATION;
			double yCoord = prev.getyCoordinate() + delta;
			LOGGER.debug("Initial Y Co-ordinate assignment for rank " + r + " : " + yCoord);
			for (LayoutNode n : graph.getRankOrder(r)) {
				n.setyCoordinate(yCoord);
			}
			maxHeight = Math.max(maxHeight, delta);
		}
	}

	private void createAuxiliaryNodesAndEdges() {
		Set<LayoutEdge> edges = graph.getAllEdges();
		makeLeftRightConstraints();
		makeEdgePairs(edges);
		LOGGER.info("Auxiliary graph created :" + graph.getAllVertices().size() + " nodes, "
				+ graph.getAllEdges().size() + " edges");
	}

	private LayoutEdge makeAuxiliaryEdge(LayoutNode from, LayoutNode to, double length, int weight) {
		LayoutEdge auxEdge = graph.getEdgeFactory().createEdge(from, to);
		auxEdge.setMinLength((int) Math.round(length));
		auxEdge.setEdgeWeight(weight);
		auxEdge.setVirtual(true);
		auxEdge.setLabel("");
		return auxEdge;
	}

	private void makeLeftRightConstraints() {
		int[] nodeSeparation = new int[2];

		/*
		 * Use smaller separation on odd ranks if the graph has edge labels.
		 * Assertion : Odd ranks do not have real nodes but even ranks can have
		 * label nodes ?
		 */
		if (graph.isHasLabels()) {
			nodeSeparation[0] = GraphLayoutParameters.NODE_SEPARATION;
			nodeSeparation[1] = 5;
		} else {
			nodeSeparation[0] = nodeSeparation[1] = GraphLayoutParameters.NODE_SEPARATION;
		}

		/*
		 * Create edges to constrain left-to-right ordering.
		 */
		int maxRank = graph.getMaxRank();
		/*
		 * Obtain vertex order based on ranks computed earlier. Ranks will
		 * change in subsequent steps as the graph is prepared for another run
		 * of network simplex for computing X co-ordinates.
		 */
		Map<Integer, List<LayoutNode>> vertexOrder = graph.getOrder();

		for (int i = 0; i <= maxRank; i++) {
			List<LayoutNode> rankNodes = vertexOrder.get(i);
			double last = 0;
			rankNodes.get(0).setRank((int) last);
			int nodeSep = nodeSeparation[i & 1];

			for (int j = 0; j < rankNodes.size(); j++) {
				LayoutNode node = rankNodes.get(j);
				// FIXME: Store right width for later use.
				// double rw = node.getRightWidth();
				// TODO: Include self-edges in the computation of node size.
				if (j + 1 < rankNodes.size()) {
					LayoutNode rightNeighbor = rankNodes.get(j + 1);
					double width = node.getRightWidth() + rightNeighbor.getLeftWidth() + nodeSep;
					LayoutEdge auxEdge = makeAuxiliaryEdge(rightNeighbor, node, width, 0);
					graph.addEdge(auxEdge);
					last = last + width;
					rightNeighbor.setRank((int) last);
					LOGGER.debug(
							"Initial x co-ordinate assignment for " + rightNeighbor + " : " + rightNeighbor.getRank());
				}
			}
		}
	}

	private void makeEdgePairs(Set<LayoutEdge> edges) {
		LayoutNodeFactory vertexFactory = new LayoutNodeFactory(idCounter);
		int m0, m1;
		for (LayoutEdge e : edges) {
			LayoutNode virtualNode = vertexFactory.createVertex();
			virtualNode.setLeftWidth(GraphLayoutParameters.NODE_SEPARATION);
			// FIXME: Set right width and height based on edge label dimensions.
			virtualNode.setLeftWidth(1);
			virtualNode.setRightWidth(1);
			virtualNode.setHeight(1);
			virtualNode.setSlackNode(true);
			// TODO: Handle edge ports.
			m0 = 0; // Head port x - tail port x
			if (m0 > 0) {
				m1 = 0;
			} else {
				m1 = -m0;
				m0 = 0;
			}
			LayoutEdge auxEdge1 = makeAuxiliaryEdge(e.getSourceVertex(), virtualNode, m0 + 1, e.getEdgeWeight());
			LayoutEdge auxEdge2 = makeAuxiliaryEdge(e.getTargetVertex(), virtualNode, m1 + 1, e.getEdgeWeight());
			virtualNode
					.setRank(Math.min(e.getSourceVertex().getRank() - m0 - 1, e.getTargetVertex().getRank() - m1 - 1));
			virtualNode.setLabel("s_" + e.getSourceVertex() + "_" + e.getTargetVertex());
			graph.addVertex(virtualNode);
			graph.addEdge(auxEdge1);
			graph.addEdge(auxEdge2);
			graph.removeEdge(e);
		}
	}
}
