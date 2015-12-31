package com.graphlib.graph.event;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.ListenableGraph;

public class GraphVertexChangeEvent<V, E extends Edge<V, E>> extends GraphChangeEvent<V, E> {

	public static final int VERTEX_ADDED = 101;
	
	public static final int VERTEX_REMOVED = 102;
	
	protected V vertex;
	
	public GraphVertexChangeEvent(ListenableGraph<V, E> source, int type, V vertex) {
		super(source, type);
		this.vertex = vertex;
	}
	
	public V getVertex() {
		return vertex;
	}

}
