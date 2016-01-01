package com.graphlib.graph.core;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.event.GraphEdgeChangeEvent;
import com.graphlib.graph.event.GraphVertexChangeEvent;

public class Subgraph<V, E extends Edge<V, E>, G extends ListenableGraph<V, E>> extends AbstractGraph<V, E>
		implements GraphListener<V, E> {

	protected G baseGraph;

	public Subgraph(G g, Set<V> vertexSubset, Set<E> edgeSubset) {
		super();
		if (g == null) {
			throw new IllegalArgumentException("Base graph cannot be null.");
		}
		this.baseGraph = g;
		this.vertices = new HashSet<>();
		this.edges = new HashSet<>();
		this.edgeFactory = g.getEdgeFactory();
		this.baseGraph.addListener(this);
		for (V v : vertexSubset) {
			if (g.contains(v)) {
				this.vertices.add(v);
			}
		}

		for (E e : edgeSubset) {
			if (g.contains(e)) {
				this.edges.add(e);
			}
		}
	}

	@Override
	public boolean addVertex(final V vertex) {
		if (baseGraph.contains(vertex)) {
			return super.addVertex(vertex);
		} else {
			throw new IllegalArgumentException("No such vertex in the base graph.");
		}
	}

	@Override
	public boolean addEdge(final E edge) {
		boolean result = false;

		/*
		 * Add vertices if not present.
		 */
		result |= addVertex(edge.getSourceVertex());
		result |= addVertex(edge.getTargetVertex());

		// add the edge into the graph
		if(baseGraph.contains(edge)) {
			result |= edges.add(edge);
		} else {
			throw new IllegalArgumentException("No such edge in the base graph.");
		}

		return result;
	}

	@Override
	public void vertexAdded(GraphVertexChangeEvent<V, E> e) {
		// Do nothing.
	}

	@Override
	public void vertexRemoved(GraphVertexChangeEvent<V, E> e) {
		removeVertex(e.getVertex());
	}

	@Override
	public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
		// Do nothing.
	}

	@Override
	public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
		removeEdge(e.getEdge());
	}

}
