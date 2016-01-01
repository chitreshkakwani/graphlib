package com.graphlib.graph.core;

public interface VertexFactory<V, E extends Edge<V, E>> {

    public V createVertex();
    
}
