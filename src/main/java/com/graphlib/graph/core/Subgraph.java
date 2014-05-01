package com.graphlib.graph.core;

public abstract class Subgraph<V extends Vertex<V, E>, E extends DirectedEdge<V, E>> {

	public abstract DirectedGraph<V, E> getContainingGraph();
	
	
}
