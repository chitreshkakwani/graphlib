package com.graphlib.graph.layout;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a triangle in a two-dimensional space.
 * 
 * @author Chitresh Kakwani
 *
 */
public class Triangle {

	private Point a;

	private Point b;

	private Point c;

	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Point getA() {
		return a;
	}

	public void setA(Point a) {
		this.a = a;
	}

	public Point getB() {
		return b;
	}

	public void setB(Point b) {
		this.b = b;
	}

	public Point getC() {
		return c;
	}

	public void setC(Point c) {
		this.c = c;
	}

	public Set<Point> getPoints() {
		Set<Point> points = new HashSet<>();
		points.add(a);
		points.add(b);
		points.add(c);
		return points;
	}

	public Point[] getSharedEdge(Triangle tri) {
		if (this.equals(tri)) {
			throw new IllegalArgumentException("Shared edge can't be found for the same triangle.");
		}
		Set<Point> points = tri.getPoints();
		if (points.contains(a) && points.contains(b)) {
			return new Point[] { a, b };
		} else if (points.contains(a) && points.contains(c)) {
			return new Point[] { a, c };
		} else if (points.contains(b) && points.contains(c)) {
			return new Point[] { b, c };
		}
		return null;
	}

	/**
	 * Checks if the given point is inside the triangle.
	 * 
	 * @param p
	 *            Point
	 * @return Returns true if the point is inside the triangle, false otherwise
	 */
	public boolean containsPoint(Point p) {
		int sum = 0;

		if (Point.getOrientation(a, b, p) != Point.Orientation.CLOCKWISE) {
			sum++;
		}
		if (Point.getOrientation(b, c, p) != Point.Orientation.CLOCKWISE) {
			sum++;
		}
		if (Point.getOrientation(c, a, p) != Point.Orientation.CLOCKWISE) {
			sum++;
		}

		return (sum == 3 || sum == 0);
	}

	@Override
	public String toString() {
		return "Triangle [" + a + ", " + b + ", " + c + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
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
		Triangle other = (Triangle) obj;
		Set<Point> points = new HashSet<>();
		points.add(other.a);
		points.add(other.b);
		points.add(other.c);
		if (points.contains(a) && points.contains(b) && points.contains(c)) {
			return true;
		}
		return false;
	}
}
