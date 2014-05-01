package com.graphlib.graph.core;

import java.util.Set;

/**
 * Interface for Graph.
 * 
 * @author Chitresh Kakwani
 *
 * @param <V>
 *            Vertex type
 * @param <E>
 *            Edge type
 */
public interface Graph<V extends Vertex<V, E>, E extends Edge<V, E>> {

	/**
	 * Returns all vertices in the graph.
	 * 
	 * @return Set of vertices in the graph
	 */
	public Set<V> getVertices();

	/**
	 * Returns the set of all edges in the graph.
	 * 
	 * @return Set of all edges in the graph
	 */
	public Set<E> getEdges();

	/**
	 * Returns the edges between two vertices. In case of directed graphs this
	 * method will return edges connecting the two vertices regardless of their
	 * direction.
	 * 
	 * @param v1
	 *            Origin/Destination vertex
	 * @param v2
	 *            Origin/Destination vertex
	 * @return Set of edges between the two vertices
	 */
	public Set<E> getEdges(V v1, V v2);

	/**
	 * Adds an edge to the graph if it doesn't exist already.
	 * 
	 * @param e
	 *            Edge to be added to the graph
	 * @return true if the edge is added, false otherwise
	 */
	public boolean addEdge(E e);

	/**
	 * Adds a vertex to the graph if it doesn't exist already.
	 * 
	 * @param v
	 *            Vertex to be added to the graph
	 * @return true if the vertex is added, false otherwise
	 */
	public boolean addVertex(V v);

	/**
	 * Checks if an edge exists in the graph.
	 * 
	 * @param e
	 *            Edge whose presence is to be checked
	 * @return true if the edge exists, false otherwise
	 */
	public boolean contains(E e);

	/**
	 * Checks if a vertex exists in the graph.
	 * 
	 * @param v
	 *            Vertex whose presence is to be checked
	 * @return true if the vertex exists, false otherwise
	 */
	public boolean contains(V v);

	/**
	 * Checks whether the graph allows multiplicity or not. Multiplicity is the
	 * property of having multiple edges between two vertices. A directed graph
	 * can have multiple edges with same direction between two vertices if it
	 * allows multiplicity.
	 * 
	 * @return true if multiplicity is allowed, false otherwise
	 */
	public boolean allowsMultiplicity();

	/**
	 * Checks if the graph has cycles.
	 * 
	 * @return true if there is a cycle present in the graph, false otherwise
	 */
	public boolean hasCycles();

}
