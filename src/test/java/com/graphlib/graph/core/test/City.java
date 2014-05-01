package com.graphlib.graph.core.test;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.core.Vertex;

public class City implements Vertex<City, Flight> {

	String name;
	
	Set<Flight> outgoingFlights;
	
	Set<Flight> incomingFlights;
	
	public City(String name){
		this.name = name;
		outgoingFlights = new HashSet<Flight>();
		incomingFlights = new HashSet<Flight>();
	}
	
	public City(City c) {
		name = c.getName();
	}

	public String getName() {
		return name;
	}
	
	public Set<Flight> getOutgoingEdges() {
		return outgoingFlights;
	}

	public Set<Flight> getIncomingEdges() {
		return incomingFlights;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		City other = (City)o;
		
		if(!name.equals(other.name))
			return false;
		
		return true;
	}
	
	public String toString() {
		return name;
	}

}
