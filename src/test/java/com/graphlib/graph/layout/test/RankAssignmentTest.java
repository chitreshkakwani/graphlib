package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.graphlib.graph.core.VertexFactory;
import com.graphlib.graph.core.test.City;
import com.graphlib.graph.core.test.DotExporter;
import com.graphlib.graph.core.test.Flight;
import com.graphlib.graph.core.test.FlightGraph;
import com.graphlib.graph.layout.VertexOrderComputer;
import com.graphlib.graph.layout.VertexRankComputer;

public class RankAssignmentTest {
	
	enum CityFactory implements VertexFactory<City, Flight> {
		
		INSTANCE;

		String prefix = "City_";
		
		Integer counter = 1;
		
		@Override
		public City createVertex() {
			return new City(prefix + counter++);
		}
		
	}
	
	@Test
	public void testInitialRankAssignment() {
		FlightGraph graph = new FlightGraph();
		City delhi = new City("Delhi");
		City bangalore = new City("Bangalore");
		City mumbai = new City("Mumbai");
		City cochin = new City("Cochin");
		City jaipur = new City("Jaipur");
		City goa = new City("Goa");
		City kolkatta = new City("Kolkatta");

		graph.addVertex(delhi);
		graph.addVertex(bangalore);
		graph.addVertex(mumbai);
		graph.addVertex(cochin);
		graph.addVertex(jaipur);
		graph.addVertex(goa);
		graph.addVertex(kolkatta);

		Flight blrToDel = new Flight(bangalore, delhi, "BLR-DEL-270");
		Flight mumToDel = new Flight(mumbai, delhi, "MUM-DEL-340");
		Flight blrToMum = new Flight(bangalore, mumbai, "BLR-MUM-440");
		Flight cocToJai = new Flight(cochin, jaipur, "COC-JAI-983");
		Flight blrToGoa = new Flight(bangalore, goa, "BLR-GOA-432");
		Flight blrToKol = new Flight(bangalore, kolkatta, "BLR-KOL-228");
		Flight goaToJai = new Flight(goa, jaipur, "GOA-JAI-884");
		Flight goaToMum = new Flight(goa, mumbai, "GOA-MUM-108");
		Flight jaiToKol = new Flight(jaipur, kolkatta, "JAI-KOL-743");

		graph.addEdge(blrToDel);
		graph.addEdge(mumToDel);
		graph.addEdge(blrToMum);
		graph.addEdge(cocToJai);
		graph.addEdge(blrToGoa);
		graph.addEdge(blrToKol);
		graph.addEdge(goaToJai);
		graph.addEdge(goaToMum);
		graph.addEdge(jaiToKol);
		
		VertexRankComputer<City, Flight> rankComputer = new VertexRankComputer<>(graph);
		rankComputer.assignRank();
		
		String dot = DotExporter.getDotRepresentation(graph);
		
		VertexOrderComputer<City, Flight> orderComputer = new VertexOrderComputer<>(graph, rankComputer.getVertexRankMap(),
				CityFactory.INSTANCE);
		orderComputer.orderVertices();
		
		assertTrue(rankComputer.getVertexRank(delhi) == 0);
		assertTrue(rankComputer.getVertexRank(bangalore) == 1);
	}

}
