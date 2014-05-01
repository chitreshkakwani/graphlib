package com.graphlib.graph.core;

import java.util.Set;

/**
 * A graph can represent a collection of vertices that are of same type.
 * Storing different types of vertices is not an option. Generics don't allow that.
 * We are left with storing the same data
 * 
 * @author Chitresh Kakwani
 *
 */
public interface Vertex<V extends Vertex<V, E>, E extends Edge<V,E>> {
	
	 public Set<E> getOutgoingEdges();
	 
	 public Set<E> getIncomingEdges();
	 
	 public int hashCode();
	 
	 public boolean equals(Object o);
	 
}
