package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.graphlib.graph.core.test.City;
import com.graphlib.graph.core.test.Flight;
import com.graphlib.graph.core.test.FlightGraph;
import com.graphlib.graph.layout.GraphLayout;
import com.graphlib.graph.layout.LayoutEdge;
import com.graphlib.graph.layout.LayoutNode;

public class VertexOrderTest {

	@Test
	public void testVertexOrder() {
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
		
		GraphLayout layout = new GraphLayout();
		
		int i = 1;
		Map<City, LayoutNode> vertexLayoutNodeMap = new HashMap<>();
		for(City v : graph.getAllVertices()) {
			LayoutNode node = new LayoutNode(i++, v.toString());
			layout.addVertex(node);
			vertexLayoutNodeMap.put(v, node);
		}
		
		for(Flight e : graph.getAllEdges()) {
			LayoutNode source = vertexLayoutNodeMap.get(e.getSourceVertex());
			LayoutNode target = vertexLayoutNodeMap.get(e.getTargetVertex());
			LayoutEdge edge = new LayoutEdge(i++, source, target);
			edge.setLabel(e.toString());
			layout.addEdge(edge);
		}
		
		layout.rank();
		
		layout.order();
		
		Map<Integer, List<LayoutNode>> order = layout.getOrder();
		assertTrue(order.get(1).get(2).getLabel().equalsIgnoreCase("Goa"));
		assertTrue(order.get(1).get(3).getLabel().equalsIgnoreCase("Cochin"));
	}
}
