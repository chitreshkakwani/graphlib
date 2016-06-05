package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.graphlib.graph.core.AbstractGraph;

/**
 * A dual graph of a polygon has triangles formed by polygon triangulation as
 * its vertices and the shared diagonals between those triangles as its edges.
 * 
 * @author Chitresh Kakwani
 *
 */
public class DualGraph extends AbstractGraph<Triangle, Diagonal> {

	public DualGraph() {
		super();
		vertices = new HashSet<Triangle>();
		edges = new HashSet<Diagonal>();
	}

	public List<Diagonal> getTrianglePath(Point src, Point tgt) {
		List<Diagonal> trianglePath = new ArrayList<>();
		Triangle firstTriangle = null;
		Triangle lastTriangle = null;
		for (Triangle t : this.getAllVertices()) {
			if (t.containsPoint(src)) {
				firstTriangle = t;
			}
			if (t.containsPoint(tgt)) {
				lastTriangle = t;
			}
		}

		if (firstTriangle == null) {
			throw new IllegalArgumentException("Source point not found in any triangle.");
		}

		if (lastTriangle == null) {
			throw new IllegalArgumentException("Destination point not found in any triangle.");
		}
		Set<Triangle> visited = new HashSet<>();
		dfs(trianglePath, visited, firstTriangle, lastTriangle);
		return trianglePath;
	}

	public boolean dfs(List<Diagonal> trianglePath, Set<Triangle> visited, Triangle tri, Triangle dest) {
		visited.add(tri);
		if (tri.equals(dest)) {
			return true;
		}
		for (Diagonal d : this.getOutgoingEdgesFor(tri)) {
			if (visited.contains(d.getTargetVertex())) {
				continue;
			}
			trianglePath.add(d);
			if (!dfs(trianglePath, visited, d.getTargetVertex(), dest)) {
				trianglePath.remove(trianglePath.size() - 1);
			} else {
				return true;
			}
		}
		return false;
	}

}
