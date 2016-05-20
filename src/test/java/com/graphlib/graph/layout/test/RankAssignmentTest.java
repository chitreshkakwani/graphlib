package com.graphlib.graph.layout.test;

import org.junit.Test;

import com.graphlib.graph.core.VertexFactory;
import com.graphlib.graph.core.test.City;
import com.graphlib.graph.core.test.Flight;
import com.graphlib.graph.core.test.FlightGraph;
import com.graphlib.graph.layout.GraphLayout;
import com.graphlib.graph.layout.GraphLayoutBuilder;

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
	public void testNetworkSimplex() {
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
		
		GraphLayoutBuilder<City, Flight> layoutBuilder = new GraphLayoutBuilder<>(graph);
		GraphLayout layout = layoutBuilder.build();
		
		//assertTrue(rankComputer.getVertexRank(delhi) == 0);
		//assertTrue(rankComputer.getVertexRank(bangalore) == 1);
	}
	
	@Test
	public void testNetworkSimplexMultipleIterations() {
		FlightGraph graph = new FlightGraph();
		City a = new City("a");
		City b = new City("b");
		City c = new City("c");
		City d = new City("d");
		City e = new City("e");
		City f = new City("f");
		City g = new City("g");
		City h = new City("h");

		graph.addVertex(a);
		graph.addVertex(b);
		graph.addVertex(c);
		graph.addVertex(d);
		graph.addVertex(e);
		graph.addVertex(f);
		graph.addVertex(g);

		Flight ab = new Flight(a, b, "ab");
		Flight af = new Flight(a, f, "af");
		Flight ae = new Flight(a, e, "ae");
		Flight bc = new Flight(b, c, "bc");
		Flight cd = new Flight(c, d, "cd");
		Flight eg = new Flight(e, g, "eg");
		Flight fg = new Flight(f, g, "fg");
		Flight dh = new Flight(d, h, "dh");
		Flight gh = new Flight(g, h, "gh");

		graph.addEdge(ab);
		graph.addEdge(af);
		graph.addEdge(ae);
		graph.addEdge(bc);
		graph.addEdge(cd);
		graph.addEdge(eg);
		graph.addEdge(fg);
		graph.addEdge(dh);
		graph.addEdge(gh);
		
		GraphLayoutBuilder<City, Flight> layoutBuilder = new GraphLayoutBuilder<>(graph);
		GraphLayout layout = layoutBuilder.build();
		
		//assertTrue(rankComputer.getVertexRank(delhi) == 0);
		//assertTrue(rankComputer.getVertexRank(bangalore) == 1);
	}

}
