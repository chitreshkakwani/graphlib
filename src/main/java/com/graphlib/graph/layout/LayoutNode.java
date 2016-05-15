package com.graphlib.graph.layout;

public class LayoutNode {
	
	int id;
	
	String label;
	
	int rank;
	
	int order;
	
	double xCoordinate;
	
	double yCoordinate;
	
	boolean isVirtual;
	
	boolean isLabel;
	
	double height;
	
	double leftWidth;
	
	double rightWidth;
	
	public LayoutNode(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	public boolean isLabel() {
		return isLabel;
	}

	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}

	public int getId() {
		return id;
	}

	public double getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(double xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public double getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(double yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getLeftWidth() {
		return leftWidth;
	}

	public void setLeftWidth(double leftWidth) {
		this.leftWidth = leftWidth;
	}

	public double getRightWidth() {
		return rightWidth;
	}

	public void setRightWidth(double rightWidth) {
		this.rightWidth = rightWidth;
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
		LayoutNode other = (LayoutNode) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return label;
	}
	
}
