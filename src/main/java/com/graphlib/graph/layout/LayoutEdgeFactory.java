package com.graphlib.graph.layout;

import com.graphlib.graph.core.EdgeFactory;
import com.graphlib.graph.core.WeightedEdgeFactory;

public class LayoutEdgeFactory implements EdgeFactory<LayoutNode, LayoutEdge>, WeightedEdgeFactory<LayoutNode, LayoutEdge> {

	private static int counter = Integer.MAX_VALUE;
	
	@Override
	public LayoutEdge createEdge(LayoutNode sourceVertex, LayoutNode targetVertex) {
		return new LayoutEdge(counter--, sourceVertex, targetVertex);
	}
	
	@Override
	public LayoutEdge createWeightedEdge(LayoutNode sourceVertex, LayoutNode targetVertex, int edgeWeight) {
		return new LayoutEdge(counter--, sourceVertex, targetVertex, edgeWeight);
	}
}
