package com.graphlib.graph.layout;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

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
		if (bmode == BalancingMode.LEFT_RIGHT) {
			LOGGER.debug("------------X co-ordinate assignment before NS------------");
			for (LayoutNode v : graph.getAllVertices()) {
				LOGGER.debug("x-coordinate assignment for " + v.toString() + " : [" + v.getRank() + "]");
			}
		}
		
		if (bmode == BalancingMode.TOP_BOTTOM) {
			// Assign initial ranks
			initRank();
		}
		
		/*
		 * Find an initial feasible tree.
		 */
		feasibleTree();

		if (bmode == BalancingMode.LEFT_RIGHT) {
			LOGGER.debug("------------ Initial X co-ordinate assignment------------");
			for (LayoutNode v : graph.getAllVertices()) {
				LOGGER.debug("x-coordinate assignment for " + v.toString() + " : [" + v.getRank() + "]");
			}
		}
		LayoutEdge e, f;
		int iterations = 0;
		while ((e = leaveEdge()) != null) {
			f = enterEdge(e);
			exchange(e, f);
			if (++iterations >= maxIterations) {
				break;
			}
		}

		if (bmode == BalancingMode.LEFT_RIGHT) {
			LOGGER.debug("------------X co-ordinate assignment before balancing------------");
			for (LayoutNode v : graph.getAllVertices()) {
				LOGGER.debug("x-coordinate assignment for " + v.toString() + " : [" + v.getRank() + "]");
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
		for (LayoutEdge e : tree.getAllEdges()) {
			if (e.getCutValue() == 0) {
				LayoutEdge f = enterEdge(e);
				if (f == null) {
					continue;
				}

				int delta = f.getSlack();
				if (delta <= 1) {
					continue;
				}
				if (e.getTargetVertex().getLim() < e.getSourceVertex().getLim()) {
					updateRank(e.getTargetVertex(), delta / 2);
				} else {
					updateRank(e.getSourceVertex(), -delta / 2);
				}

			}
		}
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
		int delta = f.getSlack();
		
		if(delta > 0) {
			int s = tree.getOutDegreeFor(e.getTargetVertex()) + tree.getInDegreeFor(e.getTargetVertex());
			if(s == 1) {
				updateRank(e.getTargetVertex(), delta);
			} else {
				s = tree.getOutDegreeFor(e.getSourceVertex()) + tree.getInDegreeFor(e.getSourceVertex());
				if(s == 1) {
					updateRank(e.getSourceVertex(), -delta);
				} else {
					if(e.getTargetVertex().getLim() < e.getSourceVertex().getLim()) {
						updateRank(e.getTargetVertex(), delta);
					} else {
						updateRank(e.getSourceVertex(), -delta);
					}
				}
			}
		}
		
		int cutValue = e.getCutValue();
		LayoutNode lca = updateTree(f.getTargetVertex(), f.getSourceVertex(), cutValue, true);
		if(updateTree(f.getSourceVertex(), f.getTargetVertex(), cutValue, false) != lca) {
			throw new IllegalStateException("Lowest common ancestors of the nodes [" + f.getTargetVertex() 
			+ ", " + f.getSourceVertex() + "] don't match.");
		}
		f.setCutValue(f.getCutValue() - cutValue);
		e.setCutValue(0);
		tree.removeEdge(e);
		tree.addEdge(f);
		postOrderTraversal(lca, lca.getParent(), lca.getLow());
	}
	
	private boolean isSequence(int a, int b, int c) {
		return ((a <= b) && (b <= c));
	}
	
	private LayoutNode updateTree(LayoutNode v, LayoutNode w, int cutValue, boolean direction) {
		LayoutEdge e;
		boolean d;
		while(!isSequence(v.getLow(), w.getLim(), v.getLim())) {
			e = v.getParent();
			if(v == e.getTargetVertex()) {
				d = direction;
			} else {
				d = !direction;
			}
			
			if(d) {
				e.setCutValue(e.getCutValue() + cutValue);
			} else {
				e.setCutValue(e.getCutValue() - cutValue);
			}
			
			if(e.getTargetVertex().getLim() > e.getSourceVertex().getLim()) {
				v = e.getTargetVertex();
			} else {
				v = e.getSourceVertex();
			}
		}
		
		return v;
	}

	private void updateRank(LayoutNode node, int delta) {
		node.setRank(node.getRank() - delta);
		for(LayoutEdge e : tree.getIncomingEdgesFor(node)) {
			if(e != node.getParent()) {
				updateRank(e.getSourceVertex(), delta);
			}
		}
		for(LayoutEdge e : tree.getOutgoingEdgesFor(node)) {
			if(e != node.getParent()) {
				updateRank(e.getTargetVertex(), delta);
			}
		}
	}

	/**
	 * Finds a non-tree edge to replace the given edge. The edge that is found has minimum slack and
	 * connects the components formed by removing the given edge.
	 * @param e
	 * @return
	 */
	private LayoutEdge enterEdge(LayoutEdge e) {
		/*
		 * Obtain the edge end-point that is lower down in the tree based on the
		 * post order traversal number. Find an edge to/from tail component that
		 * crosses over to the head component and has the minimum slack.
		 */
		if (e.getTargetVertex().getLim() < e.getSourceVertex().getLim()) {
			return dfsOutgoingEdges(e.getTargetVertex(), e.getTargetVertex().getLow(), e.getTargetVertex().getLim(),
					null);
		} else {
			return dfsIncomingEdges(e.getSourceVertex(), e.getSourceVertex().getLow(), e.getSourceVertex().getLim(),
					null);
		}
	}

	private LayoutEdge dfsIncomingEdges(LayoutNode node, int low, int lim, LayoutEdge minSlackEdge) {
		for (LayoutEdge e : graph.getIncomingEdgesFor(node)) {
			if (!tree.contains(e)) {
				if (!((low <= e.getSourceVertex().getLim()) && (e.getSourceVertex().getLim() <= lim))) {
					/*
					 * If the source vertex is not in the given component, then
					 * this edge connects the two components.
					 */
					if (minSlackEdge == null || e.getSlack() < minSlackEdge.getSlack()) {
						minSlackEdge = e;
					}
				}
			} else if (e.getSourceVertex().getLim() < node.getLim()) {
				/*
				 * If it is a tree edge and the other end-point is lower down in
				 * the tree, look at its incoming edges.
				 */
				minSlackEdge = dfsIncomingEdges(e.getSourceVertex(), low, lim, minSlackEdge);
			}
		}

		for (LayoutEdge e : tree.getOutgoingEdgesFor(node)) {
			if (minSlackEdge != null && minSlackEdge.getSlack() <= 0) {
				break;
			}
			if (e.getTargetVertex().getLim() < node.getLim()) {
				minSlackEdge = dfsIncomingEdges(e.getTargetVertex(), low, lim, minSlackEdge);
			}
		}

		return minSlackEdge;
	}

	private LayoutEdge dfsOutgoingEdges(LayoutNode node, int low, int lim, LayoutEdge minSlackEdge) {
		for (LayoutEdge e : graph.getOutgoingEdgesFor(node)) {
			if (!tree.contains(e)) {
				if (!((low <= e.getTargetVertex().getLim()) && (e.getTargetVertex().getLim() <= lim))) {
					/*
					 * If the source vertex is not in the given component, then
					 * this edge connects the two components.
					 */
					if (minSlackEdge == null || e.getSlack() < minSlackEdge.getSlack()) {
						minSlackEdge = e;
					}
				}
			} else if (e.getTargetVertex().getLim() < node.getLim()) {
				/*
				 * If it is a tree edge and the other end-point is lower down in
				 * the tree, look at its incoming edges.
				 */
				minSlackEdge = dfsOutgoingEdges(e.getTargetVertex(), low, lim, minSlackEdge);
			}
		}

		for (LayoutEdge e : tree.getIncomingEdgesFor(node)) {
			if (minSlackEdge != null && minSlackEdge.getSlack() <= 0) {
				break;
			}
			if (e.getSourceVertex().getLim() < node.getLim()) {
				minSlackEdge = dfsOutgoingEdges(e.getSourceVertex(), low, lim, minSlackEdge);
			}
		}

		return minSlackEdge;
	}

	private LayoutEdge leaveEdge() {
		for (LayoutEdge e : tree.getAllEdges()) {
			/*
			 * FIXME: Use search size to search more edges instead of returning
			 * the first edge with cut value lower than 0. Also search
			 * cyclically through the edges instead of starting from the
			 * beginning to save some iterations.
			 */
			if (e.getCutValue() < 0) {
				return e;
			}
		}

		return null;
	}

	public void feasibleTree() {

		ListenableGraph<LayoutNode, LayoutEdge> listenableGraph = new DefaultListenableGraph<>(graph);

		int iterations = 0;
		/*
		 * Iterate till a tight tree covering all vertices is obtained.
		 */
		while ((tree = tightTree(listenableGraph)).getAllVertices().size() < graph.getAllVertices().size()) {
			if(iterations++ > 50) {
				throw new IllegalStateException("Failed to obtain tight tree.");
			}
			LayoutEdge minSlackEdge = getMinSlackEdge(tree);
			int delta = minSlackEdge.getSlack();
			if (tree.contains(minSlackEdge.getTargetVertex())) {
				delta = -delta;
			}

			for (LayoutNode v : tree.getAllVertices()) {
				v.setRank(v.getRank() + delta);
			}
		}

		LOGGER.info("Tight tree obtained : " + tree.getAllEdges());

		initCutValues();
	}

	private void initCutValues() {
		LayoutNode root = tree.getAllVertices().iterator().next();
		postOrderTraversal(root, null, 1);
		dfsCutValue(root, null);
	}

	private void dfsCutValue(LayoutNode node, LayoutEdge parent) {
		for (LayoutEdge e : tree.getIncomingEdgesFor(node)) {
			if (e != parent) {
				dfsCutValue(e.getSourceVertex(), e);
			}
		}
		for (LayoutEdge e : tree.getOutgoingEdgesFor(node)) {
			if (e != parent) {
				dfsCutValue(e.getTargetVertex(), e);
			}
		}
		if (parent != null) {
			cutValueLocal(parent);
		}
	}

	/**
	 * Sets the cut value of the given tree edge computed using information
	 * local to the edge as opposed to iterating over the entire graph to
	 * identify edges crossing over from the tail component to the head
	 * component. It is assumed that the cut values of edges on one side are
	 * already set.
	 * 
	 * @param treeEdge
	 *            Tree edge for which cut value is to be computed.
	 */
	private void cutValueLocal(LayoutEdge treeEdge) {
		LayoutNode v = null;
		int direction;
		if (treeEdge.getTargetVertex().getParent() == treeEdge) {
			v = treeEdge.getTargetVertex();
			direction = 1;
		} else {
			v = treeEdge.getSourceVertex();
			direction = -1;
		}

		int sum = 0;
		for (LayoutEdge e : graph.getIncomingEdgesFor(v)) {
			sum += cutValueLocal(e, v, direction);
		}
		for (LayoutEdge e : graph.getOutgoingEdgesFor(v)) {
			sum += cutValueLocal(e, v, direction);
		}
		treeEdge.setCutValue(sum);
		LOGGER.info("Cut value for edge " + treeEdge + " : " + treeEdge.getCutValue());
	}

	private int cutValueLocal(LayoutEdge e, LayoutNode v, int direction) {
		LayoutNode other;
		if (e.getSourceVertex() == v) {
			other = e.getTargetVertex();
		} else {
			other = e.getSourceVertex();
		}
		int d, rv, f;
		if (!((v.getLow() <= other.getLim()) && (other.getLim() <= v.getLim()))) {
			f = 1;
			rv = e.getEdgeWeight();
		} else {
			f = 0;
			if (tree.contains(e)) {
				rv = e.getCutValue();
			} else {
				rv = 0;
			}
			rv -= e.getEdgeWeight();
		}

		if (direction > 0) {
			if (e.getSourceVertex() == v) {
				d = 1;
			} else {
				d = -1;
			}
		} else {
			if (e.getTargetVertex() == v) {
				d = 1;
			} else {
				d = -1;
			}
		}

		if (f != 0) {
			d = -d;
		}
		if (d < 0) {
			rv = -rv;
		}
		return rv;
	}

	private int postOrderTraversal(LayoutNode node, LayoutEdge parent, int low) {
		int lim = low;
		/*
		 * Parent would be null for the root node.
		 */
		node.setParent(parent);
		node.setLow(low);
		for (LayoutEdge e : tree.getOutgoingEdgesFor(node)) {
			if (e != parent) {
				lim = postOrderTraversal(e.getTargetVertex(), e, lim);
			}
		}

		for (LayoutEdge e : tree.getIncomingEdgesFor(node)) {
			if (e != parent) {
				lim = postOrderTraversal(e.getSourceVertex(), e, lim);
			}
		}
		node.setLim(lim);
		return lim + 1;
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

			if (e.getSlack() == 0) {
				/*
				 * Edge is tight. Should be added to the tree. Find out which of
				 * the two endpoints of the edge is not already in the tree and
				 * add it. By construction, one of the vertices will always be
				 * in the tree.
				 */
				if (!tightTree.contains(e.getSourceVertex())) {
					for (LayoutEdge edge : graph.getAllEdges(e.getSourceVertex())) {
						if (!explored.contains(edge) && !unexplored.contains(edge)) {
							unexplored.add(edge);
						}
					}
				} else if (!tightTree.contains(e.getTargetVertex())) {
					for (LayoutEdge edge : graph.getAllEdges(e.getTargetVertex())) {
						if (!explored.contains(edge) && !unexplored.contains(edge)) {
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
