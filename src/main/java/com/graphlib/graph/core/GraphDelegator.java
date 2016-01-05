package com.graphlib.graph.core;

import java.util.Set;

/**
 * Forwarding class for an {@link AbstractGraph}.
 *
 * @param <V>
 * @param <E>
 */
public class GraphDelegator<V, E extends Edge<V, E>> extends AbstractGraph<V, E> implements Graph<V, E> {

	protected Graph<V, E> delegate;
	
	public GraphDelegator(Graph<V, E> g) {
		super();
		if(g == null) {
			throw new IllegalArgumentException("Base graph cannot be null.");
		}
		this.delegate = g;
	}
	
	@Override
	public Set<V> getAllVertices() {
		return delegate.getAllVertices();
	}

	@Override
	public Set<E> getAllEdges() {
		return delegate.getAllEdges();
	}

	@Override
	public Set<E> getAllEdges(final V vertex) {
		return delegate.getAllEdges(vertex);
	}

	@Override
	public boolean addVertex(final V vertex) {
		return delegate.addVertex(vertex);
	}

	@Override
	public Set<E> getEdges(final V v1, final V v2) {
		return delegate.getEdges(v1, v2);
	}

	@Override
	public boolean addVertices(final Set<V> vertices) {
		return delegate.addVertices(vertices);
	}

	@Override
	public boolean addEdge(final E edge) {
		return delegate.addEdge(edge);
	}

	@Override
	public boolean addEdges(final Set<E> edges) {
		return delegate.addEdges(edges);
	}

	@Override
	public boolean contains(final E edge) {
		return delegate.contains(edge);
	}

	@Override
	public boolean contains(final V vertex) {
		return delegate.contains(vertex);
	}

	@Override
	public EdgeFactory<V, E> getEdgeFactory() {
		return delegate.getEdgeFactory();
	}

	@Override
	public void setEdgeFactory(final EdgeFactory<V, E> edgeFactory) {
		((AbstractGraph<V, E>) delegate).setEdgeFactory(edgeFactory);
	}

	@Override
	public Set<E> getIncomingEdgesFor(final V vertex) {
		return delegate.getIncomingEdgesFor(vertex);
	}

	@Override
	public Set<E> getOutgoingEdgesFor(final V vertex) {
		return delegate.getOutgoingEdgesFor(vertex);
	}

	@Override
	public boolean removeEdge(final E edge) {
		return delegate.removeEdge(edge);
	}

	@Override
	public void removeAllEdges() {
		delegate.removeAllEdges();
	}

	@Override
	public boolean removeVertex(final V vertex) {
		return delegate.removeVertex(vertex);
	}

	@Override
	public void removeAllVertices() {
		delegate.removeAllVertices();
	}

	@Override
	public boolean hasCycles() {
		return delegate.hasCycles();
	}
	
	@Override
	public boolean isConnected() {
		return delegate.isConnected();
	}
}
