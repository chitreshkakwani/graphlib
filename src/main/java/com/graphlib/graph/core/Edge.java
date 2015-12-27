package com.graphlib.graph.core;

/**
 * Interface for a directed edge in a {@link Graph}.
 * 
 * @author Chitresh Kakwani
 *
 * @param <V>
 * @param <E>
 */
public interface Edge<V, E extends Edge<V, E>> {
	
	public V getSourceVertex();
	
	public void setSourceVertex(V source);
	
	public V getTargetVertex();
	
	public void setTargetVertex(V target);
	
	public int hashCode();
	
	public boolean equals(Object o);
	
}
