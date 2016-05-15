package com.graphlib.graph.layout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.graphlib.graph.algorithms.ConnectivityAnalyzer;
import com.graphlib.graph.algorithms.TopologicalSort;
import com.graphlib.graph.core.DefaultListenableGraph;
import com.graphlib.graph.core.ListenableGraph;
import com.graphlib.graph.core.Subgraph;

public final class NetworkSimplex {

	public static enum BalancingMode {

		TOP_BOTTOM,

		LEFT_RIGHT
	}

	private static final Logger LOGGER = Logger.getLogger(NetworkSimplex.class);

	private GraphLayout graph;

	private Subgraph<LayoutNode, LayoutEdge, ListenableGraph<LayoutNode, LayoutEdge>> tree;

	public NetworkSimplex(GraphLayout graph) {
		this.graph = graph;
	}

	public void iterate(int maxIterations, BalancingMode bmode) {
		/*
		 * Find an initial feasible tree.
		 */
		feasibleTree();

		LayoutEdge e, f;
		int iterations = 0;
		while ((e = leaveEdge()) != null) {
			f = enterEgde(e);
			exchange(e, f);
			if (++iterations >= maxIterations) {
				break;
			}
		}

		normalize();
		balance(bmode);
	}

	private void balance(BalancingMode bmode) {
		if (bmode == BalancingMode.TOP_BOTTOM) {
			topBottomBalance();
		} else {
			leftRightBalance();
		}
	}

	private void leftRightBalance() {
		// TODO Auto-generated method stub

	}

	private void topBottomBalance() {
		int lowestRank, highestRank, inWeight, outWeight;
		int maxRank = graph.getMaxRank();
		int[] ranksNodeCount = new int[maxRank + 1];
		for (int i = 0; i < ranksNodeCount.length; i++) {
			ranksNodeCount[i] = 0;
		}
		/*
		 * Find the number of nodes on each rank.
		 */
		for (LayoutNode n : graph.getAllVertices()) {
			if (n.isVirtual()) {
				continue;
			}
			ranksNodeCount[n.getRank()]++;
		}

		for (LayoutNode n : graph.getAllVertices()) {
			if (n.isVirtual()) {
				continue;
			}
			inWeight = outWeight = 0;
			lowestRank = 0;
			highestRank = maxRank;
			for (LayoutEdge e : graph.getIncomingEdgesFor(n)) {
				inWeight += e.getEdgeWeight();
				highestRank = Math.min(highestRank, e.getSourceVertex().getRank() - e.getMinLength());
			}
			for (LayoutEdge e : graph.getOutgoingEdgesFor(n)) {
				outWeight += e.getEdgeWeight();
				lowestRank = Math.max(lowestRank, e.getTargetVertex().getRank() + e.getMinLength());
			}

			if (lowestRank < 0) {
				// FIXME: Virtual nodes can have lowest ranks smaller than zero
				// ?
				lowestRank = 0;
			}

			if (inWeight == outWeight) {
				int choice = lowestRank;
				for (int i = lowestRank + 1; i <= highestRank; i++) {
					if (ranksNodeCount[i] < ranksNodeCount[choice]) {
						choice = i;
					}
				}
				ranksNodeCount[n.getRank()]--;
				ranksNodeCount[choice]++;
				n.setRank(choice);
			}
		}
	}

	private void exchange(LayoutEdge e, LayoutEdge f) {
		if (!tree.removeEdge(e)) {
			throw new IllegalArgumentException("Leaving edge doesn't exist in the feasible tree.");
		}
		tree.addEdge(f);

		/*
		 * Re-rank nodes based on the new feasible tree.
		 */
		List<LayoutNode> nodes = TopologicalSort.apply(tree);

		for (LayoutNode n : nodes) {
			int maxRank = n.getRank();
			for (LayoutEdge inEdge : tree.getIncomingEdgesFor(n)) {
				if (inEdge.getSourceVertex().getRank() + inEdge.getMinLength() > maxRank) {
					maxRank = inEdge.getSourceVertex().getRank() + inEdge.getMinLength();
				}
			}
			n.setRank(maxRank);
		}

		/*
		 * TODO: This is an expensive operation. A more efficient method is
		 * available to recompute cut values.
		 */
		for (LayoutEdge edge : tree.getAllEdges()) {
			edge.setCutValue(findCutValue(tree, edge));
		}
	}

	private LayoutEdge enterEgde(LayoutEdge e) {
		tree.removeEdge(e);
		ConnectivityAnalyzer<LayoutNode, LayoutEdge> ca = new ConnectivityAnalyzer<>(tree);
		Set<Set<LayoutNode>> components = ca.getComponents();
		if (components.size() != 2) {
			throw new IllegalArgumentException("Subgraph is not a tree.");
		}

		Iterator<Set<LayoutNode>> it = components.iterator();
		Set<LayoutNode> head = it.next();
		Set<LayoutNode> tail = it.next();

		if (head.contains(e.getSourceVertex())) {
			Set<LayoutNode> temp = head;
			head = tail;
			tail = temp;
		}

		// TODO: This takes O(VE) time. A more efficient method is available.

		int minSlack = 0;
		LayoutEdge minSlackEdge = null;
		for (LayoutEdge edge : tree.getContainingGraph().getAllEdges()) {
			if (edge == e) {
				continue;
			}
			/*
			 * Consider edges going from the head component to the tail
			 * component.
			 */
			if (tail.contains(edge.getTargetVertex()) && head.contains(edge.getSourceVertex())) {
				if (minSlack == 0
						|| Math.abs(edge.getSourceVertex().getRank() - edge.getTargetVertex().getRank()) < minSlack) {
					minSlack = Math.abs(edge.getSourceVertex().getRank() - edge.getTargetVertex().getRank());
					minSlackEdge = edge;
				}
			}
		}

		tree.addEdge(e);

		return minSlackEdge;
	}

	private LayoutEdge leaveEdge() {
		for (LayoutEdge e : tree.getAllEdges()) {
			/*
			 * FIXME: Use search size to search more edges instead
			 * of returning the first edge with cut value lower than 0.
			 */
			if (e.getCutValue() < 0) {
				return e;
			}
		}

		return null;
	}

	public void feasibleTree() {
		// Assign initial ranks
		initRank();
		
		ListenableGraph<LayoutNode, LayoutEdge> listenableGraph = new DefaultListenableGraph<>(graph);

		/*
		 * Iterate till a tight tree covering all vertices is obtained.
		 */
		while ((tree = tightTree(listenableGraph)).getAllVertices().size() < graph.getAllVertices().size()) {
			LayoutEdge minSlackEdge = getMinSlackEdge(tree);
			int delta = minSlackEdge.getSlack();
			if(tree.contains(minSlackEdge.getTargetVertex())) {
				delta = -delta;
			}
			
			for (LayoutNode v : tree.getAllVertices()) {
				v.setRank(v.getRank() + delta);
			}
		}

		LOGGER.info("Tight tree obtained : " + tree.getAllEdges());

		for (LayoutEdge e : tree.getAllEdges()) {
			e.setCutValue(findCutValue(tree, e));
			LOGGER.info("Cut value for edge " + e + " : " + e.getCutValue());
		}
	}

	private void normalize() {
		int lowestRank = 0;

		for (LayoutNode v : graph.getAllVertices()) {
			if (v.getRank() < lowestRank) {
				lowestRank = v.getRank();
			}
		}

		if (lowestRank >= 0) {
			return;
		}

		int shift = 0 - lowestRank;

		for (LayoutNode v : graph.getAllVertices()) {
			v.setRank(v.getRank() + shift);
		}
	}

	private int findCutValue(Subgraph<LayoutNode, LayoutEdge, ListenableGraph<LayoutNode, LayoutEdge>> tree,
			LayoutEdge e) {
		if (!tree.removeEdge(e)) {
			throw new IllegalArgumentException("Specified edge doesn't exist in the feasible tree.");
		}

		ConnectivityAnalyzer<LayoutNode, LayoutEdge> ca = new ConnectivityAnalyzer<>(tree);
		Set<Set<LayoutNode>> components = ca.getComponents();
		if (components.size() != 2) {
			throw new IllegalArgumentException("Subgraph is not a tree.");
		}

		Iterator<Set<LayoutNode>> it = components.iterator();
		Set<LayoutNode> head = it.next();
		Set<LayoutNode> tail = it.next();

		if (head.contains(e.getSourceVertex())) {
			Set<LayoutNode> temp = head;
			head = tail;
			tail = temp;
		}

		// TODO: This takes O(VE) time. A more efficient method is available.

		/* Find all edges that have endpoints in both components */
		int cutValue = e.getEdgeWeight();
		for (LayoutEdge edge : tree.getContainingGraph().getAllEdges()) {
			if (edge == e) {
				/*
				 * Edge weight has already been added to cut value.
				 */
				continue;
			}
			if (head.contains(edge.getTargetVertex()) && tail.contains(edge.getSourceVertex())) {
				cutValue += edge.getEdgeWeight();
			} else if (tail.contains(edge.getTargetVertex()) && head.contains(edge.getSourceVertex())) {
				cutValue -= edge.getEdgeWeight();
			}
		}
		
		tree.addEdge(e);

		return cutValue;
	}

	/**
	 * Returns an edge, with minimum slack and not contained in the given tree,
	 * incident on one of the vertices in the tree from the graph containing the
	 * given tree(subgraph).
	 * 
	 * @param tree
	 * @return
	 */
	private LayoutEdge getMinSlackEdge(Subgraph<LayoutNode, LayoutEdge, ListenableGraph<LayoutNode, LayoutEdge>> tree) {
		LayoutEdge minSlackEdge = null;
		int minSlack = Integer.MAX_VALUE;
		for (LayoutEdge e : tree.getContainingGraph().getAllEdges()) {
			if (tree.contains(e)) {
				continue;
			}

			if (tree.contains(e.getSourceVertex()) || tree.contains(e.getTargetVertex())) {
				int slack = e.getSlack();
				if (slack < minSlack) {
					minSlack = slack;
					minSlackEdge = e;
				}
			}
		}

		if (minSlackEdge == null) {
			throw new IllegalStateException("No minimum slack edge found");
		}

		return minSlackEdge;
	}

	/**
	 * Assigns an initial feasible ranking to vertices of the graph based on a
	 * partially ordered set formed by topologically sorting the vertices.
	 */
	public void initRank() {
		Set<LayoutEdge> scannedEdges = new HashSet<>();
		Queue<LayoutNode> poset = new LinkedList<>();

		for (LayoutNode v : graph.getAllVertices()) {
			if (graph.getOutDegreeFor(v) == 0) {
				poset.add(v);
				scannedEdges.addAll(graph.getIncomingEdgesFor(v));
			}
		}

		while (!poset.isEmpty()) {
			LayoutNode v = poset.poll();
			v.setRank(0);
			
			for (LayoutEdge e : graph.getOutgoingEdgesFor(v)) {
				v.setRank(Math.max(v.getRank(), e.getTargetVertex().getRank() + e.getMinLength()));
			}

			LOGGER.debug("Initial rank assignment for " + v.toString() + " : " + v.getRank());

			for (LayoutEdge e : graph.getIncomingEdgesFor(v)) {
				scannedEdges.add(e);
				if (scannedEdges.containsAll(graph.getOutgoingEdgesFor(e.getSourceVertex()))) {
					poset.add(e.getSourceVertex());
				}
			}
		}
	}

	public Subgraph<LayoutNode, LayoutEdge, ListenableGraph<LayoutNode, LayoutEdge>> tightTree(
			ListenableGraph<LayoutNode, LayoutEdge> graph) {
		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}

		Set<LayoutNode> vertexSubset = new HashSet<>();
		Set<LayoutEdge> edgeSubset = new HashSet<>();
		Subgraph<LayoutNode, LayoutEdge, ListenableGraph<LayoutNode, LayoutEdge>> tightTree = new Subgraph<>(graph,
				vertexSubset, edgeSubset);

		LayoutNode startVertex = graph.getAllVertices().iterator().next();
		tightTree.addVertex(startVertex);
		Queue<LayoutEdge> unexplored = new LinkedList<>();
		unexplored.addAll(graph.getAllEdges(startVertex));
		Set<LayoutEdge> explored = new HashSet<>();

		while (!unexplored.isEmpty()) {
			LayoutEdge e = unexplored.remove();
			explored.add(e);
			if (tightTree.contains(e.getSourceVertex()) && tightTree.contains(e.getTargetVertex())) {
				continue;
			}

			if (Math.abs(e.getSourceVertex().getRank()
					- e.getTargetVertex().getRank()) == e.getMinLength()) {
				/*
				 * Edge is tight. Should be added to the tree. Find out which of
				 * the two endpoints of the edge is not already in the tree and
				 * add it. By construction, one of the vertices will always be
				 * in the tree.
				 */
				if (!tightTree.contains(e.getSourceVertex())) {
					for (LayoutEdge edge : graph.getAllEdges(e.getSourceVertex())) {
						if (!explored.contains(edge)) {
							unexplored.add(edge);
						}
					}
				} else if (!tightTree.contains(e.getTargetVertex())) {
					for (LayoutEdge edge : graph.getAllEdges(e.getTargetVertex())) {
						if (!explored.contains(edge)) {
							unexplored.add(edge);
						}
					}
				}
				tightTree.addEdge(e);
			}
		}

		return tightTree;

	}
}
