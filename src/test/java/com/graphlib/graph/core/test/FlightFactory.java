package com.graphlib.graph.core.test;

import com.graphlib.graph.core.EdgeFactory;
import com.graphlib.graph.core.WeightedEdgeFactory;

public class FlightFactory implements EdgeFactory<City, Flight>, WeightedEdgeFactory<City, Flight>  {

	private static Integer counter = 1;
	
	@Override
	public Flight createEdge(City sourceVertex, City targetVertex) {
		return new Flight(sourceVertex, targetVertex, "FLIGHT-" + counter++);
	}

	@Override
	public Flight createWeightedEdge(City sourceVertex, City targetVertex, int edgeWeight) {
		return new Flight(sourceVertex, targetVertex, "FLIGHT-" + counter++, edgeWeight);
	}

}
