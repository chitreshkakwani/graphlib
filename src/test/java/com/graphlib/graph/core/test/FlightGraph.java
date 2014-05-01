package com.graphlib.graph.core.test;

import java.util.HashSet;
import com.graphlib.graph.core.AbstractDirectedGraph;

public class FlightGraph extends AbstractDirectedGraph<City, Flight> {

	public FlightGraph() {
		vertices = new HashSet<City>();
		edges = new HashSet<Flight>();
	}
	
	public FlightGraph(FlightGraph graph) {
		vertices = new HashSet<City>();
		edges = new HashSet<Flight>();
		
		for(City c : graph.getVertices()) {
			vertices.add(new City(c));
		}
		
		for(Flight f : graph.getEdges()) {
			City origin = null;
			City destination = null;
			for(City c : vertices) {
				if(c.equals(f.getOriginVertex()))
					origin = c;
				else if(c.equals(f.getDestinationVertex()))
					destination = c;
			}
			Flight fCopy = new Flight(origin, destination, f.getFlightNumber());
			edges.add(fCopy);
		}
	}

	@Override
	public boolean hasCycles() {
		// TODO Auto-generated method stub
		return false;
	}

}
