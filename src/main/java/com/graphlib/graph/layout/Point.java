package com.graphlib.graph.layout;

/**
 * Represents a point in a two-dimensional space.
 * 
 * @author Chitresh Kakwani
 *
 */
public final class Point {

	/*
	 * Orientation of a set of three or more points.
	 */
	public enum Orientation {

		COUNTER_CLOCKWISE,

		CLOCKWISE,

		COLINEAR
	};

	private double x;

	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Returns the orientation of the given set of ordered points.
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return Orientation of the points.
	 */
	public static Orientation getOrientation(Point p1, Point p2, Point p3) {
		double d;
		d = (p1.getY() - p2.getY()) * (p3.getX() - p2.getX()) - (p3.getY() - p2.getY()) * (p1.getX() - p2.getX());
		return (d > 0) ? Orientation.COUNTER_CLOCKWISE : ((d < 0) ? Orientation.CLOCKWISE : Orientation.COLINEAR);
	}

	/**
	 * Checks if the point b lies between point a and c.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return Returns true if the point a, b and c are colinear and b lies
	 *         between a and c, false otherwise.
	 */
	public static boolean between(Point a, Point b, Point c) {
		Point p1 = new Point(b.getX() - a.getX(), b.getY() - a.getY());
		Point p2 = new Point(c.getX() - a.getX(), c.getY() - a.getY());

		if (getOrientation(a, b, c) != Orientation.COLINEAR) {
			return false;
		}

		return (p2.getX() * p1.getX() + p2.getY() * p1.getY() >= 0)
				&& (p2.getX() * p2.getX() + p2.getY() * p2.getY() <= p1.getX() * p1.getX() + p1.getY() * p1.getY());
	}

	/**
	 * Checks if the line segments (a, b) and (c, d) intersect or not.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return True if the line segments intersect, false otherwise
	 */
	public static boolean intersects(Point a, Point b, Point c, Point d) {
		if (getOrientation(a, b, c) == Orientation.COLINEAR || getOrientation(a, b, d) == Orientation.COLINEAR
				|| getOrientation(c, d, a) == Orientation.COLINEAR || getOrientation(c, d, b) == Orientation.COLINEAR) {
			if (between(a, b, c) || between(a, b, d) || between(c, d, a) || between(c, d, b)) {
				return true;
			}
		} else {
			boolean ccw1 = (getOrientation(a, b, c) == Orientation.COUNTER_CLOCKWISE) ? true : false;
			boolean ccw2 = (getOrientation(a, b, d) == Orientation.COUNTER_CLOCKWISE) ? true : false;
			boolean ccw3 = (getOrientation(c, d, a) == Orientation.COUNTER_CLOCKWISE) ? true : false;
			boolean ccw4 = (getOrientation(c, d, b) == Orientation.COUNTER_CLOCKWISE) ? true : false;
			return (ccw1 ^ ccw2) && (ccw3 ^ ccw4);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
