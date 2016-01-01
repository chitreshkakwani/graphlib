package com.graphlib.graph.core;

public interface WeightedEdgeFactory<V, E extends WeightedEdge<V, E>> {

	public E createWeightedEdge(V sourceVertex, V targetVertex, int edgeWeight);

}
