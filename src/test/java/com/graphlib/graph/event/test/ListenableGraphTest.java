package com.graphlib.graph.event.test;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import static org.junit.Assert.*;

import com.graphlib.graph.core.DefaultListenableGraph;
import com.graphlib.graph.core.GraphListener;
import com.graphlib.graph.core.test.City;
import com.graphlib.graph.core.test.Flight;
import com.graphlib.graph.core.test.FlightGraph;
import com.graphlib.graph.event.GraphEdgeChangeEvent;
import com.graphlib.graph.event.GraphVertexChangeEvent;

public class ListenableGraphTest {

	@Test
	public void testListenableGraph() {
		FlightGraph graph = new FlightGraph();
		City delhi = new City("Delhi");
		City bangalore = new City("Bangalore");
		City jaipur = new City("Jaipur");
		City mumbai = new City("Mumbai");
		City chennai = new City("Chennai");

		graph.addVertex(delhi);
		graph.addVertex(bangalore);
		graph.addVertex(jaipur);
		graph.addVertex(mumbai);
		graph.addVertex(chennai);

		Flight delhiToBangalore = new Flight(delhi, bangalore, "DEL-BLR-270");
		Flight jaipurToBangalore = new Flight(jaipur, bangalore, "JAI-BLR-222");
		Flight mumbaiToChennai = new Flight(mumbai, chennai, "MUM-CHE-340");

		graph.addEdge(delhiToBangalore);
		graph.addEdge(jaipurToBangalore);
		graph.addEdge(mumbaiToChennai);
		
		DefaultListenableGraph<City, Flight> listenableGraph = new DefaultListenableGraph<City, Flight>(graph);
		FlightGraphListener listener = new FlightGraphListener();
		listenableGraph.addListener(listener);
		
		listenableGraph.removeVertex(chennai);
		assertFalse(listener.getEdgesRemoved().isEmpty());
		assertTrue(listener.getEdgesRemoved().size() == 1);
		assertTrue(listener.getEdgesRemoved().contains(mumbaiToChennai));
		
		assertFalse(listener.getVerticesRemoved().isEmpty());
		assertTrue(listener.getVerticesRemoved().size() == 1);
		assertTrue(listener.getVerticesRemoved().contains(chennai));
	}
	
	public class FlightGraphListener implements GraphListener<City, Flight> {
		
		Queue<Flight> edgesAdded = new LinkedList<>();
		Queue<City> verticesAdded = new LinkedList<>();
		Queue<Flight> edgesRemoved = new LinkedList<>();
		Queue<City> verticesRemoved = new LinkedList<>();

		@Override
		public void vertexAdded(GraphVertexChangeEvent<City, Flight> e) {
			assertTrue(e.getType() == GraphVertexChangeEvent.VERTEX_ADDED);
			verticesAdded.add(e.getVertex());
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<City, Flight> e) {
			assertTrue(e.getType() == GraphVertexChangeEvent.VERTEX_REMOVED);
			verticesRemoved.add(e.getVertex());
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<City, Flight> e) {
			assertTrue(e.getType() == GraphEdgeChangeEvent.EDGE_ADDED);
			edgesAdded.add(e.getEdge());
		}

		@Override
		public void edgeRemoved(GraphEdgeChangeEvent<City, Flight> e) {
			assertTrue(e.getType() == GraphEdgeChangeEvent.EDGE_REMOVED);
			edgesRemoved.add(e.getEdge());
		}
		
		public Queue<City> getVerticesAdded() {
			return verticesAdded;
		}
		
		public Queue<City> getVerticesRemoved() {
			return verticesRemoved;
		}
		
		public Queue<Flight> getEdgesAdded() {
			return edgesAdded;
		}
		
		public Queue<Flight> getEdgesRemoved() {
			return edgesRemoved;
		}
		
		public void clear() {
			verticesAdded.clear();
			verticesRemoved.clear();
			edgesAdded.clear();
			edgesRemoved.clear();
		}
	}
}
