package com.graphlib.graph.core.test;

import java.util.HashMap;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public class DotExporter {

	/**
	 * Returns the DOT representation for this graph.
	 * 
	 * @return String DOT representation
	 * 
	 * @see <a
	 *      href="https://en.wikipedia.org/wiki/DOT_(graph_description_language)">DOT_(graph_description_language)</a>
	 */
	public static <V, E extends Edge<V, E>> String getDotRepresentation(Graph<V, E> graph) {
		StringBuffer gvBuffer = new StringBuffer();

		// Defining graph Header
		gvBuffer.append("digraph graphname {node [color=lightblue2, style=filled]; ");

		// Creating and defining vertex node map.
		int count = 0;
		HashMap<V, Integer> vertexIntMap = new HashMap<>();
		for (V obj : graph.getAllVertices()) {
			vertexIntMap.put(obj, count);
			gvBuffer.append(count + "[label=\"" + obj + "\"];");
			count++;
		}

		for (E relation : graph.getAllEdges()) {
			String color = "grey";

			/*
			 * NOTE: Spaces required before and after the label otherwise the
			 * labels touch the edges in the rendered graph.
			 */
			String label = " " + relation.toString() + " ";

			gvBuffer.append(vertexIntMap.get(relation.getTargetVertex()) + "->"
					+ vertexIntMap.get(relation.getSourceVertex()) + "[color="
					+ color + "][dir=back][label=\"" + label + "\"];");
		}

		gvBuffer.append("}");

		return gvBuffer.toString();
	}
}
