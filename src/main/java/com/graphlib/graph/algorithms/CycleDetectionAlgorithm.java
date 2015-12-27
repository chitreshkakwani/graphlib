package com.graphlib.graph.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.Graph;

public final class CycleDetectionAlgorithm<V, E extends Edge<V, E>> {

    private enum VertexState {
        BLACK, GREY, WHITE,
    }

    public final boolean isCyclic(final Graph<V, E> graph) {

        Map<V, VertexState> vertexState = new HashMap<>();

        // Initialize the vertex states
        Collection<V> vertices = graph.getAllVertices();
        for (V vertex : vertices) {
            vertexState.put(vertex, VertexState.WHITE);
        }

        for (V vertex : vertices) {
            if (vertexState.get(vertex) == VertexState.WHITE) {
                if (isCyclicAt(vertex, vertexState, graph)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isCyclicAt(final V vertex, final Map<V, VertexState> vertexState,
            final Graph<V, E> diGraph) {

        // Processing the vertex
        vertexState.put(vertex, VertexState.GREY);

        for (E edge : diGraph.getOutgoingEdgesFor(vertex)) {
            V targetVertex = edge.getTargetVertex();

            switch (vertexState.get(targetVertex)) {
            case WHITE:
                if (isCyclicAt(targetVertex, vertexState, diGraph)) {
                    return true;
                }
                break;
            case GREY:
                return true;
            default:
            }
        }

        // Processed the vertex
        vertexState.put(vertex, VertexState.BLACK);

        return false;
    }
}
