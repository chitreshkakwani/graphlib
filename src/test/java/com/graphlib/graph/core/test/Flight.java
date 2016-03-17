package com.graphlib.graph.core.test;

import com.graphlib.graph.core.Edge;
import com.graphlib.graph.core.WeightedEdge;

public class Flight implements WeightedEdge<City, Flight> {

	City from;
	
	City to;
	
	String number;
	
	int cost;
	
	public Flight(City from, City to, String number){
		this(from, to, number, 1);
	}
	
	public Flight(City from, City to, String number, int cost){
		this.from = from;
		this.to = to;
		this.number = number;
		this.cost = cost;
	}
	
	public City getSourceVertex() {
		return from; 
	}

	public City getTargetVertex() {
		return to;
	}

	public void setSourceVertex(City v) {
		this.from = v;
	}

	public void setTargetVertex(City v) {
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
		
		if(!from.equals(other.getSourceVertex()))
			return false;
		
		if(!to.equals(other.getTargetVertex()))
			return false;
		
		if(!number.equals(other.number))
			return false;
		
		return true;
	}
	
	public String toString(){
		return number;
	}

	@Override
	public int getEdgeWeight() {
		return cost;
	}

	@Override
	public void setEdgeWeight(int weight) {
		this.cost = weight;
	}

}
