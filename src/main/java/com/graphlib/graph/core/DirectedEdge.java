package com.graphlib.graph.core;

public interface DirectedEdge<V extends Vertex<V, E>, E extends DirectedEdge<V, E>> 
    extends Edge<V, E>{

	public void setOriginVertex(V v);
	
	public V getOriginVertex();
	
	public void setDestinationVertex(V v);
	
	public V getDestinationVertex();
	
	public int hashCode();
	
	public boolean equals(Object o);
	
}
