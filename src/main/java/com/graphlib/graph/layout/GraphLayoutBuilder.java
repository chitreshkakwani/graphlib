package com.graphlib.graph.layout;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public class GraphLayoutBuilder<V, E extends Edge<V, E>, G extends Graph<V, E>> {

	public static final int DEFAULT_MIN_EDGE_LENGTH = 1;
	
	G graph;
	
	public GraphLayoutBuilder(G g) {
		this.graph = g;
	}
	
	public void setMinEdgeLengthFunction() {
		
	}
}
