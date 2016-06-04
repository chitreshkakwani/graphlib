package com.graphlib.graph.layout;

/**
 * A box is a rectangular region in a two-dimensional space specified by two
 * points that form its lower left and upper right corners.
 * 
 * @author Chitresh Kakwani
 *
 */
public final class Box {

	private Point lowerLeft;

	private Point upperRight;

	public Box(Point lowerLeft, Point upperRight) {
		super();
		this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
	}

	public Point getLowerLeft() {
		return lowerLeft;
	}

	public void setLowerLeft(Point lowerLeft) {
		this.lowerLeft = lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

	public void setUpperRight(Point upperRight) {
		this.upperRight = upperRight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lowerLeft == null) ? 0 : lowerLeft.hashCode());
		result = prime * result + ((upperRight == null) ? 0 : upperRight.hashCode());
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
		Box other = (Box) obj;
		if (lowerLeft == null) {
			if (other.lowerLeft != null)
				return false;
		} else if (!lowerLeft.equals(other.lowerLeft))
			return false;
		if (upperRight == null) {
			if (other.upperRight != null)
				return false;
		} else if (!upperRight.equals(other.upperRight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[LL : " + lowerLeft + ", UR : " + upperRight + "]";
	}
}
