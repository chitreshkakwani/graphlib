package com.graphlib.graph.layout;

import com.graphlib.graph.core.WeightedEdge;

public class LayoutEdge implements WeightedEdge<LayoutNode, LayoutEdge> {

	public static final int DEFAULT_MIN_EDGE_LENGTH = 1;

	public static final int DEFAULT_EDGE_WEIGHT = 1;
	
	public static final String DEFAULT_EDGE_LABEL = "e";

	final int id;

	LayoutNode source;

	LayoutNode target;

	String label;

	int cutValue;

	int weight;

	int minLength;

	boolean isVirtual;

	public LayoutEdge(int id, LayoutNode source, LayoutNode target, int weight, int minLength, String label) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.weight = weight;
		this.minLength = minLength;
		this.label = label;
	}

	public LayoutEdge(int id, LayoutNode source, LayoutNode target, int weight) {
		this(id, source, target, weight, DEFAULT_MIN_EDGE_LENGTH, DEFAULT_EDGE_LABEL);
	}
	
	public LayoutEdge(int id, LayoutNode source, LayoutNode target) {
		this(id, source, target, DEFAULT_EDGE_WEIGHT, DEFAULT_MIN_EDGE_LENGTH, DEFAULT_EDGE_LABEL);
	}

	public int getId() {
		return id;
	}

	@Override
	public LayoutNode getSourceVertex() {
		return source;
	}

	@Override
	public void setSourceVertex(LayoutNode source) {
		this.source = source;
	}

	@Override
	public LayoutNode getTargetVertex() {
		return target;
	}

	@Override
	public void setTargetVertex(LayoutNode target) {
		this.target = target;
	}

	@Override
	public int getEdgeWeight() {
		return weight;
	}

	@Override
	public void setEdgeWeight(int weight) {
		this.weight = weight;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getCutValue() {
		return cutValue;
	}

	public void setCutValue(int cutValue) {
		this.cutValue = cutValue;
	}

	public int getLength() {
		return source.getRank() - target.getRank();
	}
	
	public int getSlack() {
		return getLength() - minLength;
	}
	
	public boolean isTight() {
		return (getSlack() == 0);
	}
	
	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayoutEdge other = (LayoutEdge) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return label;
	}

}
