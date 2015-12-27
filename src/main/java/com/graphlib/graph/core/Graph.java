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
public interface Graph<V, E extends Edge<V, E>> {

	/**
	 * Returns all vertices in the graph.
	 * 
	 * @return Set of vertices in the graph
	 */
	public Set<V> getAllVertices();

	/**
	 * Returns the set of all edges in the graph.
	 * 
	 * @return Set of all edges in the graph
	 */
	public Set<E> getAllEdges();

	/**
	 * Returns the set of all edges that have the given vertex as an endpoint.
	 * 
	 * @param vertex
	 * @return Set of all edges that have the given vertex as an endpoint
	 */
	public Set<E> getAllEdges(final V vertex);

	/**
	 * Returns the edges between two vertices. In case of directed graphs this
	 * method will return edges connecting the two vertices regardless of their
	 * direction.
	 * 
	 * @param v1
	 *            Source/Target vertex
	 * @param v2
	 *            Source/Target vertex
	 * @return Set of edges between the two vertices
	 */
	public Set<E> getEdges(final V v1, final V v2);

	/**
	 * Adds an edge to the graph if it doesn't exist already.
	 * 
	 * @param e
	 *            Edge to be added to the graph
	 * @return true if the edge is added, false otherwise
	 */
	public boolean addEdge(final E e);

	/**
	 * Adds the given set of edges to the graph if they don't already exist.
	 * 
	 * @param edges
	 *            Set of edges to be added to the graph
	 * @return true if at least one edge is added, false otherwise
	 */
	public boolean addEdges(final Set<E> edges);

	/**
	 * Adds a vertex to the graph if it doesn't exist already.
	 * 
	 * @param vertex
	 *            Vertex to be added to the graph
	 * @return true if the vertex is added, false otherwise
	 */
	public boolean addVertex(final V vertex);

	/**
	 * Adds the given set of vertices to the graph if they don't already exist.
	 * 
	 * @param vertices
	 *            Set of vertices to be added to the graph
	 * @return true if at least one vertex is added, false otherwise
	 */
	public boolean addVertices(final Set<V> vertices);

	/**
	 * Checks if an edge exists in the graph.
	 * 
	 * @param e
	 *            Edge whose presence is to be checked
	 * @return true if the edge exists, false otherwise
	 */
	public boolean contains(final E e);

	/**
	 * Checks if a vertex exists in the graph.
	 * 
	 * @param v
	 *            Vertex whose presence is to be checked
	 * @return true if the vertex exists, false otherwise
	 */
	public boolean contains(final V v);

	/**
	 * Returns the set of incoming edges for the given vertex.
	 * 
	 * @param v
	 *            Vertex that belongs to this graph
	 * @return Set of incoming edges for the given vertex
	 */
	public Set<E> getIncomingEdgesFor(final V v);

	/**
	 * Gets the number of incoming edges for the given vertex.
	 * 
	 * @param vertex
	 * @return Number of incoming edges
	 */
	public int getInDegreeFor(final V vertex);

	/**
	 * Returns the set of outgoing edges for the given vertex.
	 * 
	 * @param v
	 *            Vertex that belongs to this graph
	 * @return Set of outgoing edges for the given vertex
	 */
	public Set<E> getOutgoingEdgesFor(final V v);

	/**
	 * Gets the number of outgoing edges for the given vertex.
	 * 
	 * @param vertex
	 * @return Number of outgoing edges
	 */
	public int getOutDegreeFor(final V vertex);

	/**
	 * Returns the edge factory associated with this graph.
	 * 
	 * @return Edge factory
	 */
	public EdgeFactory<V, E> getEdgeFactory();

	/**
	 * Removes the specified edge if it exists in the graph.
	 * 
	 * @param edge
	 * @return True if the edge exists, false otherwise
	 */
	public boolean removeEdge(final E edge);

	/**
	 * Removes all the edges in the graph.
	 */
	public void removeAllEdges();

	/**
	 * Removes the given edges from the graph.
	 * 
	 * @param edges
	 * @return True if at least one of the given edges exists in the graph,
	 *         false otherwise
	 */
	public boolean removeAllEdges(final Set<E> edges);

	/**
	 * Removes all the edges associated with the given vertex from the graph.
	 * 
	 * @param vertex
	 * @return True if any edge is removed, false otherwise
	 */
	public boolean removeAllEdges(V vertex);

	/**
	 * Removes all the edges that have the given vertices as endpoints.
	 * 
	 * @param v1
	 * @param v2
	 * @return True if any edge is removed, false otherwise
	 */
	boolean removeAllEdges(V v1, V v2);

	/**
	 * Removes all the incoming edges for the given vertex.
	 * 
	 * @param vertex
	 * @return True if any edge is removed, false otherwise
	 */
	boolean removeAllIncomingEdges(V vertex);

	/**
	 * Removes all the outgoing edges for the given vertex.
	 * 
	 * @param vertex
	 * @return True if any edge is removed, false otherwise
	 */
	boolean removeAllOutgoingEdges(V vertex);

	
	/**
	 * Removes the specified vertex, and the edges associated with it, from the
	 * graph if the vertex exists.
	 * 
	 * @param vertex
	 * @return True if the vertex exists, false otherwise
	 */
	public boolean removeVertex(final V vertex);

	/**
	 * Removes all the vertices and the edges from the graph.
	 */
	public void removeAllVertices();

	/**
	 * Removes all the given vertices from the graph.
	 * 
	 * @param vertices
	 * @return True if any of the given vertices exist in the graph, false otherwise 
	 */
	public boolean removeAllVertices(Set<V> vertices);
	
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
