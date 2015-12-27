package com.graphlib.graph.core;

/**
 * Interface for an {@link Edge} factory.
 * 
 * @author Chitresh Kakwani
 *
 * @param <V>
 * @param <E>
 */
public interface EdgeFactory<V, E extends Edge<V, E>> {

    E createEdge(V sourceVertex, V targetVertex);
}
