package com.graphlib.graph.algorithms;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public final class ConnectivityAnalyzer<V, E extends Edge<V, E>> {

	private Graph<V, E> graph;
	
	public ConnectivityAnalyzer(Graph<V, E> g) {
		if(g == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}
		
		if(g.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}
		this.graph = g;
	}
	
	public Set<Set<V>> getComponents() {
		Set<Set<V>> components = new HashSet<>();
		
		Set<V> visited = new HashSet<>();
		for(V v : graph.getAllVertices()) {
			Set<V> component = new HashSet<>();
			if(!visited.contains(v)) {
				traverseComponent(v, component);
				components.add(component);
				visited.addAll(component);
			}
		}
		
		return components;
	}

	private void traverseComponent(V v, Set<V> visited) {
		visited.add(v);
		for(E e : graph.getIncomingEdgesFor(v)) {
			if(!visited.contains(e.getSourceVertex())) {
				traverseComponent(e.getSourceVertex(), visited);
			}
		}
		
		for(E e : graph.getOutgoingEdgesFor(v)) {
			if(!visited.contains(e.getTargetVertex())) {
				traverseComponent(e.getTargetVertex(), visited);
			}
		}
	}
}
