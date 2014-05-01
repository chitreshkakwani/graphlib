package com.graphlib.graph.core;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDirectedGraph<V extends Vertex<V, E>, E extends DirectedEdge<V, E>>
		implements DirectedGraph<V, E> {
	
	protected Set<V> vertices;

	protected Set<E> edges;

	public Set<V> getVertices() {
		return vertices;
	}

	public Set<E> getEdges() {
		return edges;
	}

	public Set<E> getEdges(V v1, V v2) {
		Set<E> result = new HashSet<E>();
		for (E e : edges) {
			if (e.getOriginVertex().equals(v1)
					&& e.getDestinationVertex().equals(v2))
				result.add(e);
			else if (e.getOriginVertex().equals(v2)
					&& e.getDestinationVertex().equals(v1))
				result.add(e);
		}
		return result;
	}

	public boolean addEdge(E e) {
		return edges.add(e);
	}

	public boolean addVertex(V v) {
		return vertices.add(v);
	}

	public boolean contains(E e) {
		return edges.contains(e);
	}

	public boolean contains(V v) {
		return vertices.contains(v);
	}

	public boolean allowsMultiplicity() {
		return true;
	}
	
	public abstract boolean hasCycles();

}
