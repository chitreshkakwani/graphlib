package com.graphlib.graph.core;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.event.GraphEdgeChangeEvent;
import com.graphlib.graph.event.GraphVertexChangeEvent;

public class DefaultListenableGraph<V, E extends Edge<V, E>> extends AbstractGraph<V, E>
		implements ListenableGraph<V, E> {

	protected Graph<V, E> baseGraph;

	protected Set<GraphListener<V, E>> listeners = new HashSet<>();

	protected boolean reuseEvents;

	protected FlyWeightVertexEvent<V, E> reusableVertexEvent;

	protected FlyWeightEdgeEvent<V, E> reusableEdgeEvent;

	public DefaultListenableGraph(Graph<V, E> g) {
		this(g, false);
	}

	public DefaultListenableGraph(Graph<V, E> g, boolean reuseEvents) {
		if (g instanceof ListenableGraph) {
			throw new IllegalArgumentException("Base graph cannot be a listenable graph.");
		}

		this.baseGraph = g;
		this.reuseEvents = reuseEvents;
		this.reusableVertexEvent = new FlyWeightVertexEvent<>(this, -1, null);
		this.reusableEdgeEvent = new FlyWeightEdgeEvent<>(this, -1, null, null, null);
	}

	@Override
	public boolean addVertex(V v) {
		boolean added = baseGraph.addVertex(v);
		if (added) {
			fireVertexAddedEvent(v);
		}

		return added;
	}

	@Override
	public boolean removeVertex(V v) {
		if(baseGraph.contains(v)) {
			this.removeAllEdges(baseGraph.getAllEdges(v));
			baseGraph.removeVertex(v);
			fireVertexRemovedEvent(v);
			return true;
		}

		return false;
	}

	@Override
	public boolean addEdge(E edge) {
		boolean added = baseGraph.addEdge(edge);
		if (added) {
			fireEdgeAddedEvent(edge, edge.getSourceVertex(), edge.getTargetVertex());
		}
		return added;
	}

	@Override
	public boolean removeEdge(final E edge) {
		boolean removed = baseGraph.removeEdge(edge);
		if (removed) {
			fireEdgeRemovedEvent(edge, edge.getSourceVertex(), edge.getTargetVertex());
		}

		return removed;
	}

	@Override
	public void addListener(GraphListener<V, E> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(GraphListener<V, E> listener) {
		this.listeners.remove(listener);
	}

	private GraphVertexChangeEvent<V, E> createGraphVertexChangeEvent(int type, V vertex) {
		if (reuseEvents) {

			reusableVertexEvent.setVertex(vertex);
			reusableVertexEvent.setType(type);
			return reusableVertexEvent;
		}

		return new GraphVertexChangeEvent<V, E>(this, type, vertex);
	}

	private GraphEdgeChangeEvent<V, E> createGraphEdgeChangeEvent(int type, E edge, V edgeSource, V edgeTarget) {
		if (reuseEvents) {
			reusableEdgeEvent.setEdge(edge);
			reusableEdgeEvent.setType(type);
			reusableEdgeEvent.setEdgeSource(edgeSource);
			reusableEdgeEvent.setEdgeTarget(edgeTarget);
			return reusableEdgeEvent;
		}

		return new GraphEdgeChangeEvent<V, E>(this, type, edge, edgeSource, edgeTarget);
	}

	private void fireVertexAddedEvent(V v) {
		GraphVertexChangeEvent<V, E> e = createGraphVertexChangeEvent(GraphVertexChangeEvent.VERTEX_ADDED, v);

		for (GraphListener<V, E> l : listeners) {
			l.vertexAdded(e);
		}

	}

	private void fireVertexRemovedEvent(V v) {
		GraphVertexChangeEvent<V, E> e = createGraphVertexChangeEvent(GraphVertexChangeEvent.VERTEX_REMOVED, v);

		for (GraphListener<V, E> l : listeners) {
			l.vertexRemoved(e);
		}
	}

	private void fireEdgeAddedEvent(E edge, V sourceVertex, V targetVertex) {
		GraphEdgeChangeEvent<V, E> e = createGraphEdgeChangeEvent(GraphEdgeChangeEvent.EDGE_ADDED, edge, sourceVertex,
				targetVertex);

		for (GraphListener<V, E> l : listeners) {
			l.edgeRemoved(e);
		}
	}

	private void fireEdgeRemovedEvent(E edge, V sourceVertex, V targetVertex) {
		GraphEdgeChangeEvent<V, E> e = createGraphEdgeChangeEvent(GraphEdgeChangeEvent.EDGE_REMOVED, edge, sourceVertex,
				targetVertex);

		for (GraphListener<V, E> l : listeners) {
			l.edgeRemoved(e);
		}
	}

	private static class FlyWeightVertexEvent<VV, EE extends Edge<VV, EE>> extends GraphVertexChangeEvent<VV, EE> {

		public FlyWeightVertexEvent(ListenableGraph<VV, EE> source, int type, VV vertex) {
			super(source, type, vertex);
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setVertex(VV vertex) {
			this.vertex = vertex;
		}
	}

	private static class FlyWeightEdgeEvent<VV, EE extends Edge<VV, EE>> extends GraphEdgeChangeEvent<VV, EE> {

		public FlyWeightEdgeEvent(ListenableGraph<VV, EE> source, int type, EE edge, VV edgeSource, VV edgeTarget) {
			super(source, type, edge, edgeSource, edgeTarget);
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setEdge(EE edge) {
			this.edge = edge;
		}

		public void setEdgeSource(VV edgeSource) {
			this.edgeSource = edgeSource;
		}

		public void setEdgeTarget(VV edgeTarget) {
			this.edgeTarget = edgeTarget;
		}
	}
}
