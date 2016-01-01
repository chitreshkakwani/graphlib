package com.graphlib.graph.core.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.graphlib.graph.core.DefaultListenableGraph;
import com.graphlib.graph.core.Subgraph;

public class SubgraphTest {
	
	@Test
	public void testSubgraph() {
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
		
		Set<City> vertexSubset = new HashSet<>();
		Set<Flight> edgeSubset = new HashSet<>();
		
		DefaultListenableGraph<City, Flight> listenableGraph = new DefaultListenableGraph<>(graph);
		Subgraph<City, Flight, DefaultListenableGraph<City, Flight>> sub = new Subgraph<>(listenableGraph, vertexSubset, edgeSubset);
		
		assertTrue(sub.addEdge(mumbaiToChennai));
		assertTrue(sub.contains(mumbai));
		assertTrue(sub.contains(chennai));
		assertFalse(sub.contains(bangalore));
	}

}
