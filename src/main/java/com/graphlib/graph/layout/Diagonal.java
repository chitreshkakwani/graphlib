package com.graphlib.graph.layout;

import java.util.HashSet;
import java.util.Set;

import com.graphlib.graph.core.Edge;

/**
 * Represents a diagonal in a polygon which is shared between two triangles
 * given by polygon triangulation and forms an edge in the {@link DualGraph} of
 * the polygon.
 * 
 * @author Chitresh Kakwani
 *
 */
public class Diagonal implements Edge<Triangle, Diagonal> {

	private Point a;

	private Point b;

	private Triangle source;

	private Triangle target;

	public Diagonal(Triangle source, Triangle target, Point a, Point b) {
		this.source = source;
		this.target = target;
		this.a = a;
		this.b = b;
	}

	public Point[] getEndpoints() {
		Point[] endpoints = new Point[2];
		endpoints[0] = a;
		endpoints[1] = b;
		return endpoints;
	}

	@Override
	public Triangle getSourceVertex() {
		return source;
	}

	@Override
	public void setSourceVertex(Triangle source) {
		this.source = source;
	}

	@Override
	public Triangle getTargetVertex() {
		return target;
	}

	@Override
	public void setTargetVertex(Triangle target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		Diagonal other = (Diagonal) obj;
		Set<Point> points = new HashSet<>();
		points.add(other.a);
		points.add(other.b);
		if (!points.contains(a) || !points.contains(b)) {
			return false;
		}
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Diagonal [" + a + ", " + b + "]";
	}

}
