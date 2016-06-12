package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.List;

import com.graphlib.graph.layout.Spline.BezierCurve;

/**
 * A polygon represents a closed two-dimensional region bounded by a set of line
 * segments. The endpoints of the line segments form the vertices of the
 * polygon.
 * 
 * @author Chitresh Kakwani
 *
 */
public final class Polygon {

	private List<Point> points = new ArrayList<>();

	public Polygon() {
	}

	public Polygon(Polygon poly) {
		setPoints(poly.getPoints());
	}

	public void addPoint(Point p) {
		points.add(p);
	}

	public Point removePoint(int index) {
		return points.remove(index);
	}

	public Point getPoint(int index) {
		return points.get(index);
	}

	public List<Point> getPoints() {
		List<Point> res = new ArrayList<>();
		res.addAll(points);
		return res;
	}

	public void setPoints(List<Point> points) {
		this.points.clear();
		this.points.addAll(points);
	}

	public int getNumPoints() {
		return points.size();
	}

	/**
	 * Checks if the line segment formed by joining the points at the given
	 * indices lies entirely within the polygon or not.
	 * 
	 * @param p1Index
	 * @param p2Index
	 * @return True if the line segment lies entirely within the polygon, false
	 *         otherwise
	 */
	public boolean isDiagonal(int p1Index, int p2Index) {
		Point p1 = points.get(p1Index);
		Point p2 = points.get(p2Index);
		boolean result;
		/*
		 * Neighborhood test.
		 */
		Point ccwNeighbor = points.get((p1Index + 1) % points.size());
		Point cwNeighbor = points.get((p1Index + points.size() - 1) % points.size());
		if (Point.getOrientation(cwNeighbor, p1, ccwNeighbor) == Point.Orientation.COUNTER_CLOCKWISE) {
			result = (Point.getOrientation(p1, p2, cwNeighbor) == Point.Orientation.COUNTER_CLOCKWISE)
					&& (Point.getOrientation(p2, p1, ccwNeighbor) == Point.Orientation.COUNTER_CLOCKWISE);
		} else {
			/*
			 * (cwNeighbor, p1, ccwNeighbor) are assumed to be non colinear.
			 */
			result = Point.getOrientation(p1, p2, ccwNeighbor) == Point.Orientation.CLOCKWISE;
		}

		if (!result) {
			return false;
		}

		/*
		 * Check if the line segment formed by the given points intersects any
		 * edge of the polygon. If it does, then the line segment is not
		 * entirely inside the polygon.
		 */
		for (int i = 0; i < points.size(); i++) {
			Point j1 = points.get(i);
			Point j2 = points.get((i + 1) % points.size());
			if (!(j1.equals(p1) || j2.equals(p1) || j1.equals(p2) || j2.equals(p2))) {
				if (Point.intersects(p1, p2, j1, j2)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Decomposes the polygon into a set of triangles with pairwise
	 * non-intersecting interiors whose union is the polygon.
	 * 
	 * @return List of triangles whose union is the polygon
	 */
	public List<Triangle> triangulate() {
		return triangulateRec(this);
	}

	/**
	 * 
	 * @param poly
	 * @return
	 */
	private static List<Triangle> triangulateRec(Polygon poly) {
		List<Triangle> triangles = new ArrayList<>();
		if (poly.getNumPoints() > 3) {
			for (int pnli = 0; pnli < poly.getNumPoints(); pnli++) {
				int pnlip1 = (pnli + 1) % poly.getNumPoints();
				int pnlip2 = (pnli + 2) % poly.getNumPoints();
				if (poly.isDiagonal(pnli, pnlip2)) {
					triangles.add(new Triangle(poly.getPoint(pnli), poly.getPoint(pnlip1), poly.getPoint(pnlip2)));
					Polygon poly2 = new Polygon(poly);
					poly2.removePoint(pnlip1);
					triangles.addAll(triangulateRec(poly2));
					return triangles;
				}
			}
			throw new IllegalStateException("Polygon triangulation failure: " + poly.getPoints());
		} else {
			triangles.add(new Triangle(poly.getPoint(0), poly.getPoint(1), poly.getPoint(2)));
		}

		return triangles;
	}

	public DualGraph getDualGraph() {
		DualGraph dg = new DualGraph();
		List<Triangle> triangles = triangulate();
		for (Triangle t : triangles) {
			dg.addVertex(t);
			for (Triangle t1 : triangles) {
				if (!t1.equals(t)) {
					dg.addVertex(t1);
					Point[] sharedEdge = t.getSharedEdge(t1);
					if (sharedEdge != null) {
						dg.addEdge(new Diagonal(t, t1, sharedEdge[0], sharedEdge[1]));
						dg.addEdge(new Diagonal(t1, t, sharedEdge[0], sharedEdge[1]));
					}
				}
			}
		}
		return dg;
	}

	/**
	 * Checks if the given Bezier curve lies inside the polygon or not assuming the
	 * first and last control points of the curve are inside the polygon.
	 * 
	 * @param curve Bezier curve to test for containment
	 * @return true if the curve is inside the polygon, false otherwise
	 */
	public boolean containsCurve(BezierCurve curve) {
		Point[] line = new Point[2];
		for (int i = 0; i < points.size(); i++) {
			line[0] = points.get(i);
			line[1] = points.get((i + 1) % points.size());

			List<Double> roots = curve.getLineIntersectionRoots(line[0], line[1]);
			if (roots == null) {
				continue;
			}

			for (int r = 0; r < roots.size(); r++) {
				if (roots.get(r) < GraphLayoutParameters.DOUBLE_PRECISION
						|| roots.get(r) > 1 - GraphLayoutParameters.DOUBLE_PRECISION) {
					/*
					 * Skip the roots that are approximately 0 or 1. They
					 * correspond to (x,y) values on the curve near first and
					 * last control points. Since the first and last control
					 * points of the curve are known to be inside the polygon at
					 * this point, the roots with values approximately 0 or 1
					 * are going to be inside the polygon as well.
					 */
					continue;
				}

				Point intersectionPoint = curve.getCurvePoint(roots.get(r));
				if (intersectionPoint.distanceSquared(line[0]) < Point.PRECISION
						|| intersectionPoint.distanceSquared(line[1]) < Point.PRECISION) {
					/*
					 * If the intersection point lies approximately on the endpoints of the line
					 * segment, i.e., corners of the polygon, it can be ignored.
					 */
					continue;
				}
				return false;
			}
		}
		return true;
	}
}
