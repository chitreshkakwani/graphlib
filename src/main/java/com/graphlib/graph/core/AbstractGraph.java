package com.graphlib.graph.core;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.algorithms.CycleDetectionAlgorithm;

public abstract class AbstractGraph<V, E extends Edge<V, E>> implements Graph<V, E> {

	protected EdgeFactory<V, E> edgeFactory;

	protected Set<V> vertices;

	protected Set<E> edges;

	public Set<V> getAllVertices() {
		Set<V> vSet = new HashSet<V>();
		vSet.addAll(vertices);
		return vSet;
	}

	public Set<E> getAllEdges() {
		Set<E> edgeSet = new HashSet<E>();
		edgeSet.addAll(edges);
		return edgeSet;
	}

	public Set<E> getAllEdges(final V vertex) {
		Set<E> result = new HashSet<E>();
		for (E e : edges) {
			if (e.getSourceVertex().equals(vertex) || e.getTargetVertex().equals(vertex)) {
				result.add(e);
			}
		}
		return result;
	}

	public boolean addVertex(final V vertex) {
		return vertices.add(vertex);
	}

	public Set<E> getEdges(final V v1, final V v2) {
		Set<E> result = new HashSet<E>();
		for (E e : edges) {
			if (e.getSourceVertex().equals(v1) && e.getTargetVertex().equals(v2)) {
				result.add(e);
			} else if (e.getSourceVertex().equals(v2) && e.getTargetVertex().equals(v1)) {
				result.add(e);
			}
		}
		return result;
	}

	public boolean addVertices(final Set<V> vertices) {
		boolean result = false;

		for (V vertex : vertices) {
			result |= addVertex(vertex);
		}

		return result;
	}

	public boolean addEdge(final E edge) {
		boolean result = false;

		/*
		 * Add vertices if not present.
		 */
		result |= addVertex(edge.getSourceVertex());
		result |= addVertex(edge.getTargetVertex());

		// add the edge into the graph
		result |= edges.add(edge);

		return result;
	}

	public boolean addEdges(final Set<E> edges) {
		boolean result = false;

		for (E edge : edges) {
			result |= addEdge(edge);
		}

		return result;
	}

	public boolean contains(final E edge) {
		return edges.contains(edge);
	}

	public boolean contains(final V vertex) {
		return vertices.contains(vertex);
	}

	public EdgeFactory<V, E> getEdgeFactory() {
		return edgeFactory;
	}

	public void setEdgeFactory(final EdgeFactory<V, E> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}

	public Set<E> getIncomingEdgesFor(final V vertex) {
		Set<E> incomingEdges = new HashSet<E>();

		for (E edge : edges) {
			if (edge.getTargetVertex().equals(vertex)) {
				incomingEdges.add(edge);
			}
		}

		return incomingEdges;
	}

	public int getInDegreeFor(final V vertex) {
		return getIncomingEdgesFor(vertex).size();
	}

	public Set<E> getOutgoingEdgesFor(final V vertex) {
		Set<E> outgoingEdges = new HashSet<E>();

		for (E edge : edges) {
			if (edge.getSourceVertex().equals(vertex)) {
				outgoingEdges.add(edge);
			}
		}

		return outgoingEdges;
	}

	public int getOutDegreeFor(final V vertex) {
		return getOutgoingEdgesFor(vertex).size();
	}

	public boolean removeEdge(final E edge) {
		return edges.remove(edge);
	}

	public void removeAllEdges() {
		edges.clear();
	}

	public boolean removeAllEdges(final Set<E> edges) {
		boolean result = false;

		for (E edge : edges) {
			result |= removeEdge(edge);
		}

		return result;
	}

	public boolean removeAllEdges(final V vertex) {
		return removeAllEdges(getAllEdges(vertex));
	}

	public boolean removeAllEdges(V sourceVertex, V targetVertex) {
		return removeAllEdges(getEdges(sourceVertex, targetVertex));
	}

	public boolean removeAllIncomingEdges(final V vertex) {
		return removeAllEdges(getIncomingEdgesFor(vertex));
	}

	public boolean removeAllOutgoingEdges(final V vertex) {
		return removeAllEdges(getOutgoingEdgesFor(vertex));
	}

	public boolean removeVertex(final V vertex) {
		boolean result = false;

		// remove all edges associated with the vertex
		result |= removeAllEdges(vertex);

		// remove vertex
		result |= vertices.remove(vertex);

		return result;
	}

	public boolean removeAllVertices(final Set<V> vertices) {
		boolean result = false;

		for (V vertex : vertices) {
			result |= removeVertex(vertex);
		}

		return result;
	}

	public void removeAllVertices() {
		removeAllEdges();
		vertices.clear();
	}

	public boolean allowsMultiplicity() {
		return true;
	}

	public boolean hasCycles() {
		CycleDetectionAlgorithm<V, E> algo = new CycleDetectionAlgorithm<>();
		return algo.isCyclic(this);
	}
	
	public boolean isConnected() {
		if (this.getAllVertices().isEmpty()) {
			return true;
		}

		Set<V> visited = new HashSet<>();

		findReachableVertices(visited, this.getAllVertices().iterator().next());

		return !(visited.size() != this.getAllVertices().size());
	}
	
	private void findReachableVertices(Set<V> visited, V v) {
		if (visited.contains(v))
			return;

		visited.add(v);
		for (E in : this.getIncomingEdgesFor(v)) {
			findReachableVertices(visited, in.getSourceVertex());
		}
		for (E out : this.getOutgoingEdgesFor(v)) {
			findReachableVertices(visited, out.getTargetVertex());
		}

	}
}
