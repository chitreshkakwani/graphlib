package com.graphlib.graph.event;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.ListenableGraph;

public class GraphEdgeChangeEvent<V, E extends Edge<V, E>> extends GraphChangeEvent<V, E> {
	
	public static final int EDGE_ADDED = 201;
	
	public static final int EDGE_REMOVED = 202;
	
	protected E edge;
	
	protected V edgeSource;
	
	protected V edgeTarget;

	public GraphEdgeChangeEvent(ListenableGraph<V, E> source, int type, E edge, V edgeSource, V edgeTarget) {
		super(source, type);
		this.edge = edge;
		this.edgeSource = edgeSource;
		this.edgeTarget = edgeTarget;
	}
	
	public E getEdge() {
		return edge;
	}
	
	public V getEdgeSource() {
		return edgeSource;
	}
	
	public V getEdgeTarget() {
		return edgeTarget;
	}

}
