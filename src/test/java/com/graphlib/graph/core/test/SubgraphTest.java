package com.graphlib.graph.core.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.graphlib.graph.core.DefaultListenableGraph;
import com.graphlib.graph.core.ListenableGraph;
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
		City kolkatta = new City("Kolkatta");

		graph.addVertex(delhi);
		graph.addVertex(bangalore);
		graph.addVertex(jaipur);
		graph.addVertex(mumbai);
		graph.addVertex(chennai);
		graph.addVertex(kolkatta);

		Flight bangaloreToDelhi = new Flight(bangalore, delhi, "BLR-DEL-270");
		graph.addEdge(bangaloreToDelhi);
		Flight bangaloreToJaipur = new Flight(bangalore, jaipur, "BLR-JAI-222");
		graph.addEdge(bangaloreToJaipur);
		Flight chennaiToMumbai = new Flight(chennai, mumbai, "CHE-MUM-340");
		graph.addEdge(chennaiToMumbai);
		Flight chennaiToBangalore = new Flight(chennai, bangalore, "CHE-BLR-213");
		graph.addEdge(chennaiToBangalore);
		Flight mumbaiToDelhi = new Flight(mumbai, delhi, "MUM-DEL-445");
		graph.addEdge(mumbaiToDelhi);
		Flight chennaiToKolkatta = new Flight(chennai, kolkatta, "CHE-KOL-798");
		graph.addEdge(chennaiToKolkatta);
		
		assertTrue(graph.isConnected());
		
		Set<City> vertexSubset = new HashSet<>();
		Set<Flight> edgeSubset = new HashSet<>();
		
		ListenableGraph<City, Flight> listenableGraph = new DefaultListenableGraph<>(graph);
		Subgraph<City, Flight, ListenableGraph<City, Flight>> sub = new Subgraph<>(listenableGraph, vertexSubset, edgeSubset);
		
		assertTrue(sub.addEdge(chennaiToMumbai));
		assertTrue(sub.contains(mumbai));
		assertTrue(sub.contains(chennai));
		assertFalse(sub.contains(bangalore));
		
		assertTrue(sub.addEdge(bangaloreToJaipur));
		assertTrue(sub.contains(bangalore));
		assertTrue(sub.contains(jaipur));
		assertFalse(sub.isConnected());
	}

}
