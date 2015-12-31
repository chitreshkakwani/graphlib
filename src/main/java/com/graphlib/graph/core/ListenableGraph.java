package com.graphlib.graph.core;

public interface ListenableGraph<V, E extends Edge<V, E>> extends Graph<V, E> {

	public void addListener(GraphListener<V, E> observer);
	
	public void removeListener(GraphListener<V, E> observer);
}
