package com.graphlib.graph.core.test;

import com.graphlib.graph.core.DirectedEdge;

public class Flight implements DirectedEdge<City, Flight> {

	City from;
	
	City to;
	
	String number;
	
	public Flight(City from, City to, String number){
		this.from = from;
		this.to = to;
		this.number = number;
		this.from.getOutgoingEdges().add(this);
		this.to.getIncomingEdges().add(this);
	}
	
	public City getOriginVertex() {
		return from; 
	}

	public City getDestinationVertex() {
		return to;
	}

	public void setOriginVertex(City v) {
		this.from = v;
	}

	public void setDestinationVertex(City v) {
		this.to = v;
	}
	
	public String getFlightNumber() {
		return number;
	}
	
	public int hashCode() {
		return from.hashCode() + to.hashCode() + number.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		Flight other = (Flight)o;
		
		if(!from.equals(other.getOriginVertex()))
			return false;
		
		if(!to.equals(other.getDestinationVertex()))
			return false;
		
		if(!number.equals(other.number))
			return false;
		
		return true;
	}
	
	public String toString(){
		return number;
	}

}
