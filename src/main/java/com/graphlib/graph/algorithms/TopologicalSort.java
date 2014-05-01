package com.graphlib.graph.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.graphlib.graph.core.DirectedGraph;
import com.graphlib.graph.core.DirectedEdge;
import com.graphlib.graph.core.Vertex;

public class TopologicalSort<V extends Vertex<V, E>, E extends DirectedEdge<V, E>> {

	public List<V> apply(DirectedGraph<V, E> graph) {

		if (graph.hasCycles())
			throw new UnsupportedOperationException(
					"Topological sort not supported on graphs with cycles");

		List<V> sortedVertices = new ArrayList<V>();

		Stack<V> stack = new Stack<V>();
		for (V v : graph.getVertices()) {
			if (sortedVertices.contains(v))
				continue;

			stack.push(v);

			while (!stack.isEmpty()) {
				V vertex = stack.peek();
				if (sortedVertices.contains(vertex)) {
					stack.pop();
					continue;
				}

				if (vertex.getIncomingEdges().isEmpty()) {
					sortedVertices.add(vertex);
					continue;
				}

				boolean flag = true;

				for (E e : vertex.getIncomingEdges()) {
					if (sortedVertices.contains(e.getOriginVertex()))
						continue;
					else {
						stack.push(e.getOriginVertex());
						flag = false;
					}
				}

				if (flag) {
					stack.pop();
					sortedVertices.add(vertex);
				}
			}
		}
		return sortedVertices;
	}

}
