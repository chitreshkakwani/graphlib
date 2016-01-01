package com.graphlib.graph.core;

public interface WeightedEdge<V, E extends WeightedEdge<V, E>> extends Edge<V, E> {

	public int getEdgeWeight();
	
	public void setEdgeWeight(int weight);
}
