package com.graphlib.graph.core.test;

public class City {

	String name;
	
	public City(String name){
		this.name = name;
	}
	
	public City(City c) {
		name = c.getName();
	}

	public String getName() {
		return name;
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
