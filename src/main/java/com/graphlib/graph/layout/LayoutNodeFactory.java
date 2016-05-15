package com.graphlib.graph.layout;

import com.graphlib.graph.core.VertexFactory;

public class LayoutNodeFactory implements VertexFactory<LayoutNode, LayoutEdge> {

	private static int counter = 1;
	
	public LayoutNodeFactory(int idStartIndex) {
		counter = idStartIndex;
	}
	
	@Override
	public LayoutNode createVertex() {
		return new LayoutNode(counter++, "v_" + counter);
	}
	
	public LayoutNode createVertex(String label) {
		return new LayoutNode(counter++, label);
	}

}
