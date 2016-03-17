package com.graphlib.graph.layout;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.graphlib.graph.core.Graph;
import com.graphlib.graph.core.VertexFactory;
import com.graphlib.graph.core.WeightedEdge;

public class GraphLayout<V, E extends WeightedEdge<V, E>> {

	private Graph<V, E> graph;
	
	private Map<V, Integer> vertexRankMap;

	private Map<E, Integer> edgeCutValueMap;

	private Map<Integer, LinkedHashSet<V>> vertexOrder;

	private Map<V, Double> vertexMedianValues;

	private VertexFactory<V, E> vertexFactory;

	private Set<V> virtualVertices;

	private Set<E> virtualEdges;

	private Map<E, Set<V>> edgeVirtualVerticesMap;
	
	public GraphLayout(Graph g) {
		this.graph = g;
	}
}
