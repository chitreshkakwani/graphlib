package com.graphlib.graph.event;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.ListenableGraph;

public class GraphChangeEvent<V, E extends Edge<V, E>> {

	protected ListenableGraph<V, E> source;
	
	protected int type;

	public GraphChangeEvent(ListenableGraph<V, E> source, int type) {
		if(source == null) {
			throw new IllegalArgumentException("Event source null.");
		}
		this.source = source;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

}
