package com.graphlib.graph.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public class TopologicalSort {

	public static <V, E extends Edge<V, E>> List<V> apply(final Graph<V, E> graph) {

		if (graph.hasCycles())
			throw new UnsupportedOperationException("Topological sort not supported on graphs with cycles");

		List<V> sortedVertices = new ArrayList<V>();

		Stack<V> stack = new Stack<V>();
		for (V v : graph.getAllVertices()) {
			if (sortedVertices.contains(v))
				continue;

			stack.push(v);

			while (!stack.isEmpty()) {
				V vertex = stack.peek();
				if (sortedVertices.contains(vertex)) {
					stack.pop();
					continue;
				}

				if (graph.getIncomingEdgesFor(vertex).isEmpty()) {
					sortedVertices.add(vertex);
					continue;
				}

				boolean flag = true;

				for (E e : graph.getIncomingEdgesFor(vertex)) {
					if (sortedVertices.contains(e.getSourceVertex()))
						continue;
					else {
						stack.push(e.getSourceVertex());
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

	private TopologicalSort() {
	}

}
