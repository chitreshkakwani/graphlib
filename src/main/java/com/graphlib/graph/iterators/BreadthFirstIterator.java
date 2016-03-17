package com.graphlib.graph.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public final class BreadthFirstIterator<V, E extends Edge<V, E>> implements Iterator<V> {

	private final Graph<V, E> graph;
	
	private final Queue<V> bfsQueue;
	
	private final Set<V> visited;
	
	public BreadthFirstIterator(Graph<V, E> g, V startVertex) {
		if(g == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}
		if(g.getAllVertices().isEmpty() || !g.getAllVertices().contains(startVertex)) {
			throw new IllegalArgumentException("Graph does not contain the start vertex.");
		}
		this.graph = g;
		this.bfsQueue = new LinkedList<>();
		this.bfsQueue.add(startVertex);
		this.visited = new HashSet<>();
	}
	
	@Override
	public boolean hasNext() {
		return !bfsQueue.isEmpty();
	}

	@Override
	public V next() {
		V vertex = bfsQueue.remove();
		this.visited.add(vertex);
		for(E e : graph.getOutgoingEdgesFor(vertex)) {
			if(!visited.contains(e.getTargetVertex())) {
				bfsQueue.add(e.getTargetVertex());
			}
		}
		this.visited.addAll(bfsQueue);
		return vertex;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("BFS iterator does not support removal.");
	}

}
