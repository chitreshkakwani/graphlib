package com.graphlib.graph.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.graphlib.graph.algorithms.ConnectivityAnalyzer;
import com.graphlib.graph.core.DefaultListenableGraph;
import com.graphlib.graph.core.Graph;
import com.graphlib.graph.core.ListenableGraph;
import com.graphlib.graph.core.Subgraph;
import com.graphlib.graph.core.WeightedEdge;

public final class VertexRankComputer<V, E extends WeightedEdge<V, E>> {

	private static final Logger LOGGER = Logger.getLogger(VertexRankComputer.class);

	public static final int MIN_EDGE_LENGTH = 1;

	private final Graph<V, E> graph;

	private final Map<V, Integer> vertexRankMap;

	private final Map<E, Integer> edgeCutValueMap;

	public VertexRankComputer(Graph<V, E> g) {
		if (g == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}
		if (g.hasCycles()) {
			throw new IllegalArgumentException("Graph must not have cycles.");
		}
		this.graph = g;
		this.vertexRankMap = new HashMap<>();
		this.edgeCutValueMap = new HashMap<>();
	}

	public Map<V, Integer> getVertexRankMap() {
		Map<V, Integer> rankMap = new HashMap<>();
		rankMap.putAll(vertexRankMap);
		return rankMap;
	}

	public int getVertexRank(V v) {
		return vertexRankMap.get(v);
	}

	public int getEdgeCutValue(E e) {
		return edgeCutValueMap.get(e);
	}

	public void assignRank() {
		// Assign initial ranks
		initRank();
		
		// TODO: Network simplex iterations to find optimal ranking
		
		normalize();
	}

	public void initRank() {
		int rank = 0;
		Queue<Set<V>> poset = getPartiallyOrderedSet();
		for (Set<V> vSet : poset) {
			for (V v : vSet) {
				vertexRankMap.put(v, rank);
			}
			rank++;
		}

		LOGGER.debug("Vertex ranks based on partially ordered set : " + vertexRankMap);

		ListenableGraph<V, E> listenableGraph = new DefaultListenableGraph<>(graph);
		Subgraph<V, E, ListenableGraph<V, E>> tree = null;
		/*
		 * Iterate till a tight tree covering all vertices is obtained.
		 */
		while ((tree = tightTree(listenableGraph)).getAllVertices().size() < graph.getAllVertices().size()) {
			E minSlackEdge = getMinSlackEdge(tree);
			int delta = vertexRankMap.get(minSlackEdge.getSourceVertex())
					- vertexRankMap.get(minSlackEdge.getTargetVertex());
			delta = (delta < 0) ? delta + VertexRankComputer.MIN_EDGE_LENGTH
					: delta - VertexRankComputer.MIN_EDGE_LENGTH;
			for (V v : tree.getAllVertices()) {
				vertexRankMap.put(v, vertexRankMap.get(v) + delta);
			}
		}

		LOGGER.info("Tight tree obtained : " + tree.getAllEdges());
		
		for (E e : tree.getAllEdges()) {
			edgeCutValueMap.put(e, findCutValue(tree, e));
		}
		
		LOGGER.info("Cut values for edges in tight tree: " + edgeCutValueMap);
	}

	private void normalize() {
		int lowestRank = 0;

		for (V v : vertexRankMap.keySet()) {
			if (vertexRankMap.get(v) < lowestRank) {
				lowestRank = vertexRankMap.get(v);
			}
		}

		if (lowestRank >= 0) {
			return;
		}

		int shift = 0 - lowestRank;

		for (V v : vertexRankMap.keySet()) {
			vertexRankMap.put(v, vertexRankMap.get(v) + shift);
		}
	}

	public int findCutValue(Subgraph<V, E, ListenableGraph<V, E>> tree, E e) {
		tree.removeEdge(e);
		ConnectivityAnalyzer<V, E> ca = new ConnectivityAnalyzer<>(tree);
		Set<Set<V>> components = ca.getComponents();
		if (components.size() != 2) {
			throw new IllegalArgumentException("Subgraph is not a tree.");
		}

		Iterator<Set<V>> it = components.iterator();
		Set<V> head = it.next();
		Set<V> tail = it.next();

		if (head.contains(e.getSourceVertex())) {
			Set<V> temp = head;
			head = tail;
			tail = temp;
		}

		//TODO: This takes O(VE) time. A more efficient method is available.
		
		/* Find all edges that have endpoints in both components */
		int cutValue = e.getEdgeWeight();
		for (E edge : tree.getContainingGraph().getAllEdges()) {
			if (edge == e) {
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
	private E getMinSlackEdge(Subgraph<V, E, ListenableGraph<V, E>> tree) {
		E minSlackEdge = null;
		int minSlack = Integer.MAX_VALUE;
		for (E e : tree.getContainingGraph().getAllEdges()) {
			if (tree.contains(e)) {
				continue;
			}

			if (tree.contains(e.getSourceVertex()) || tree.contains(e.getTargetVertex())) {
				int edgeLen = Math.abs(vertexRankMap.get(e.getSourceVertex()) - vertexRankMap.get(e.getTargetVertex()));
				int slack = edgeLen - MIN_EDGE_LENGTH;
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

	public Queue<Set<V>> getPartiallyOrderedSet() {
		Queue<Set<V>> partiallyOrderedSet = new LinkedList<>();
		Set<E> scannedEdges = new HashSet<>();
		Set<V> scannedVertices = new HashSet<>();
		Set<V> vertexSet = new HashSet<>();

		for (V v : graph.getAllVertices()) {
			if (graph.getInDegreeFor(v) == 0) {
				scannedVertices.add(v);
				scannedEdges.addAll(graph.getOutgoingEdgesFor(v));
				vertexSet.add(v);
			}
		}

		LOGGER.debug("Evaluating partially ordered set with initial vertex set: " + vertexSet);

		partiallyOrderedSet.add(vertexSet);

		while (scannedVertices.size() != graph.getAllVertices().size()) {
			vertexSet = new HashSet<>();
			for (V v : graph.getAllVertices()) {
				if (scannedVertices.contains(v)) {
					continue;
				}

				if (scannedEdges.containsAll(graph.getIncomingEdgesFor(v))) {
					vertexSet.add(v);
				}
			}

			for (V v : vertexSet) {
				scannedVertices.add(v);
				scannedEdges.addAll(graph.getOutgoingEdgesFor(v));
			}

			partiallyOrderedSet.add(vertexSet);
		}

		LOGGER.debug("Partially ordered set: " + partiallyOrderedSet);

		return partiallyOrderedSet;
	}

	public Subgraph<V, E, ListenableGraph<V, E>> tightTree(ListenableGraph<V, E> graph) {
		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}

		Set<V> vertexSubset = new HashSet<>();
		Set<E> edgeSubset = new HashSet<>();
		Subgraph<V, E, ListenableGraph<V, E>> tightTree = new Subgraph<>(graph, vertexSubset, edgeSubset);

		V startVertex = graph.getAllVertices().iterator().next();
		tightTree.addVertex(startVertex);
		Queue<E> unexplored = new LinkedList<>();
		unexplored.addAll(graph.getAllEdges(startVertex));
		Set<E> explored = new HashSet<>();

		while (!unexplored.isEmpty()) {
			E e = unexplored.remove();
			explored.add(e);
			if (tightTree.contains(e.getSourceVertex()) && tightTree.contains(e.getTargetVertex())) {
				continue;
			}

			if (Math.abs(vertexRankMap.get(e.getSourceVertex())
					- vertexRankMap.get(e.getTargetVertex())) == MIN_EDGE_LENGTH) {
				/*
				 * Edge is tight. Should be added to the tree. Find out which of
				 * the two endpoints of the edge is not already in the tree and
				 * add it. By construction, one of the vertices will always be
				 * in the tree.
				 */
				if (!tightTree.contains(e.getSourceVertex())) {
					for (E edge : graph.getAllEdges(e.getSourceVertex())) {
						if (!explored.contains(edge)) {
							unexplored.add(edge);
						}
					}
				} else if (!tightTree.contains(e.getTargetVertex())) {
					for (E edge : graph.getAllEdges(e.getTargetVertex())) {
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
