package com.graphlib.graph.core.test;

import java.util.HashSet;
import com.graphlib.graph.core.AbstractGraph;

public class FlightGraph extends AbstractGraph<City, Flight> {

	public FlightGraph() {
		vertices = new HashSet<City>();
		edges = new HashSet<Flight>();
	}
	
	public FlightGraph(FlightGraph graph) {
		vertices = new HashSet<City>();
		edges = new HashSet<Flight>();
		
		for(City c : graph.getAllVertices()) {
			vertices.add(new City(c));
		}
		
		for(Flight f : graph.getAllEdges()) {
			City origin = null;
			City destination = null;
			for(City c : vertices) {
				if(c.equals(f.getSourceVertex()))
					origin = c;
				else if(c.equals(f.getTargetVertex()))
					destination = c;
			}
			Flight fCopy = new Flight(origin, destination, f.getFlightNumber());
			edges.add(fCopy);
		}
	}

}
