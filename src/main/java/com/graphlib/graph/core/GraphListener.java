package com.graphlib.graph.core;

import java.util.EventListener;

import com.graphlib.graph.event.GraphEdgeChangeEvent;
import com.graphlib.graph.event.GraphVertexChangeEvent;

public interface GraphListener<V, E extends Edge<V, E>> extends EventListener {

	public void vertexAdded(GraphVertexChangeEvent<V, E> e);

	public void vertexRemoved(GraphVertexChangeEvent<V, E> e);

	public void edgeAdded(GraphEdgeChangeEvent<V, E> e);

	public void edgeRemoved(GraphEdgeChangeEvent<V, E> e);
}
