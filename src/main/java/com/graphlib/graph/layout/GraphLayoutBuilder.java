package com.graphlib.graph.layout;

import java.util.HashMap;
import java.util.Map;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public class GraphLayoutBuilder<V, E extends Edge<V, E>> {
	
	Graph<V, E> graph;
	
	GraphLayout graphLayout;
	
	RenderingContext rc;
	
	public GraphLayoutBuilder(Graph<V, E> graph) {
		this.graph = graph;
		this.graphLayout = new GraphLayout();
		
		int i = 1;
		Map<V, LayoutNode> vertexLayoutNodeMap = new HashMap<>();
		for(V v : graph.getAllVertices()) {
			LayoutNode node = new LayoutNode(i++, v.toString());
			node.setHeight(GraphLayoutParameters.DEFAULT_HEIGHT);
			node.setLeftWidth(27);
			node.setRightWidth(27);
			graphLayout.addVertex(node);
			vertexLayoutNodeMap.put(v, node);
		}
		
		for(E e : graph.getAllEdges()) {
			LayoutNode source = vertexLayoutNodeMap.get(e.getSourceVertex());
			LayoutNode target = vertexLayoutNodeMap.get(e.getTargetVertex());
			LayoutEdge edge = new LayoutEdge(i++, source, target);
			if(e.toString() == null || e.toString().isEmpty()) {
				edge.setLabel("");
			} else {
				edge.setLabel(e.toString());
				edge.setMinLength(edge.getMinLength() * 2);
				graphLayout.setHasLabels(true);
			}
			edge.setLabel(e.toString());
			graphLayout.addEdge(edge);
		}
	}	
	
	public GraphLayoutBuilder<V, E> renderingContext(RenderingContext rc) {
		this.rc = rc;
		this.graphLayout.setRenderingContext(rc);
		return this;
	}
	
	public GraphLayout build() {
		graphLayout.rank();
		graphLayout.order();
		//graphLayout.positionVertices();
		//graphLayout.drawEdges();
		return graphLayout;
	}
}
