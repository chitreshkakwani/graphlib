package com.graphlib.graph.core.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.graphlib.graph.algorithms.TopologicalSort;

public class GraphTest {

	@Test
	public void test() {
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

		List<City> sorted = TopologicalSort.apply(graph);

		assert (sorted.indexOf(delhi) < sorted.indexOf(bangalore));
		assert (sorted.indexOf(jaipur) < sorted.indexOf(bangalore));
		assert (sorted.indexOf(mumbai) < sorted.indexOf(chennai));

	}

}
