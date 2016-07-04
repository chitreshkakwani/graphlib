package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates spline routing algorithm which fits curves on a path given by a
 * set of line segments that lie inside a given polygon.
 * 
 * @see An Algorithm for Automatically Fitting Digitized Curves by Philip J.
 *      Schneider from "Graphics Gems", Academic Press, 1990
 * 
 * @author Chitresh Kakwani
 *
 */
public class SplineRouter {

	public SplineRouter() {

	}

	public Spline fitCurve(Polygon poly, List<Point> path) {
		Point tanV1 = normalizeVector(new Point(0, 0));
		Point tanV2 = normalizeVector(new Point(0, 0));
		return fitCurveRecursive(poly, path, tanV1, tanV2);

	}

	/**
	 * Fits a piecewise Bezier curve on the given sequence of points recursively
	 * that lies inside the given polygon and has the specified tangent vectors
	 * at the endpoints.
	 * 
	 * @param path
	 *            The path on which to fit the curve
	 * @param tanV1
	 *            Direction vector at the starting point
	 * @param tanV2
	 *            Direction vector at the ending point
	 * @return A piecewise Bezier curve
	 */
	public Spline fitCurveRecursive(Polygon poly, List<Point> path, Point tHat1, Point tHat2) {
		if (path.size() < 2) {
			throw new IllegalArgumentException("Can't route spline with fewer than two points in the path.");
		}

		List<Double> parameters = chordLengthParameters(path);

		Point[][] A = new Point[parameters.size()][2];
		Point[] bezCurve = new Point[4];

		/* Compute the A's */
		for (int i = 0; i < parameters.size(); i++) {
			A[i][0] = scale(tHat1, B1(parameters.get(i)));
			A[i][1] = scale(tHat2, B2(parameters.get(i)));
		}

		double[][] C = new double[2][2];
		C[0][0] = C[0][1] = C[1][0] = C[1][1] = 0.0;
		double[] X = new double[2];
		double det_C0_C1, det_C0_X, det_X_C1;

		for (int i = 0; i < parameters.size(); i++) {
			C[0][0] += dot(A[i][0], A[i][0]);
			C[1][0] = C[0][1] += dot(A[i][0], A[i][1]);
			C[1][1] += dot(A[i][1], A[i][1]);

			Point tmp = sub(path.get(i),
					add(scale(path.get(0), B0(parameters.get(i))),
							add(scale(path.get(0), B1(parameters.get(i))),
									add(scale(path.get(path.size() - 1), B2(parameters.get(i))),
											scale(path.get(path.size() - 1), B3(parameters.get(i)))))));

			X[0] += dot(A[i][0], tmp);
			X[1] += dot(A[i][1], tmp);
		}

		/* Compute the determinants of C and X */
		det_C0_C1 = C[0][0] * C[1][1] - C[1][0] * C[0][1];
		det_C0_X = C[0][0] * X[1] - C[0][1] * X[0];
		det_X_C1 = X[0] * C[1][1] - X[1] * C[0][1];

		/* Finally, derive alpha values */
		if (det_C0_C1 == 0.0) {
			det_C0_C1 = (C[0][0] * C[1][1]) * 10e-12;
		}

		/* if (det_C0_C1) { */
		double alpha_l = 0;
		double alpha_r = 0;

		if (Math.abs(det_C0_C1) >= GraphLayoutParameters.DOUBLE_PRECISION) {
			alpha_l = det_X_C1 / det_C0_C1;
			alpha_r = det_C0_X / det_C0_C1;
		}

		if (Math.abs(det_C0_C1) < GraphLayoutParameters.DOUBLE_PRECISION || alpha_l <= 0.0 || alpha_r <= 0.0) {
			double dist = distance(path.get(path.size() - 1), path.get(0)) / 3.0;
			alpha_l = dist;
			alpha_r = dist;
		}
		bezCurve[0] = path.get(0);
		bezCurve[3] = path.get(path.size() - 1);
		Point sv0 = scale(tHat1, alpha_l);
		Point sv1 = scale(tHat2, alpha_r);

		Spline.BezierCurve finalCurve = null;
		if ((finalCurve = splineFits(poly, path, bezCurve[0], sv0, bezCurve[3], sv1)) != null) {
			Spline spline = new Spline();
			spline.addCurve(finalCurve);
			return spline;
		}

		/*
		 * Compute second and third control points. TODO: Scaling factor ?
		 */
		bezCurve[1] = add(bezCurve[0], scale(sv0, 1 / 3.0));
		bezCurve[2] = sub(bezCurve[3], scale(sv1, 1 / 3.0));

		Spline.BezierCurve curve = new Spline.BezierCurve(bezCurve[0], bezCurve[1], bezCurve[2], bezCurve[3]);

		double maxd = -1;
		double dist = 0;
		int maxi = -1;
		for (int i = 1; i < path.size() - 1; i++) {
			double t = parameters.get(i);
			Point p = curve.getCurvePoint(t);
			if ((dist = p.distance(path.get(i))) > maxd) {
				maxd = dist;
				maxi = i;
			}

		}

		Point splitv1 = normalizeVector(sub(path.get(maxi), path.get(maxi - 1)));
		Point splitv2 = normalizeVector(sub(path.get(maxi + 1), path.get(maxi)));
		Point splitv = normalizeVector(add(splitv1, splitv2));
		//TODO: Split the polygon ?
		Spline s1 = fitCurveRecursive(poly, path.subList(0, maxi + 1), tHat1, splitv);
		Spline s2 = fitCurveRecursive(poly, path.subList(maxi, path.size()), splitv, tHat2);
		s1.addSpline(s2);
		return s1;
	}

	/**
	 * Returns a sequence of parameters in the interval [0, 1], at intervals
	 * proportional to the distance between points on the given path.
	 * 
	 * @param path
	 *            Sequence of points based on which the interval [0, 1] is to be
	 *            divided
	 * @return Sequence of parameters in the interval [0, 1]
	 */
	private List<Double> chordLengthParameters(List<Point> path) {
		List<Double> parameters = new ArrayList<>();
		parameters.add(0.0);
		for (int i = 1; i < path.size(); i++) {
			parameters.add(parameters.get(i - 1) + distance(path.get(i), path.get(i - 1)));
		}

		for (int i = 1; i < path.size(); i++) {
			parameters.set(i, parameters.get(i) / parameters.get(parameters.size() - 1));
		}

		return parameters;
	}

	private Double distance(Point p1, Point p2) {
		Double dx = p2.getX() - p1.getX();
		Double dy = p2.getY() - p1.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	private Point normalizeVector(Point v) {
		double len = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
		if (len != 0.0) {
			return new Point((v.getX() / len), (v.getY() / len));
		}

		return new Point(v.getX(), v.getY());
	}

	private double dot(Point v1, Point v2) {
		return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY());
	}

	private Point scale(Point v, double newlen) {
		return new Point(v.getX() * newlen, v.getY() * newlen);
	}

	private Point add(Point p1, Point p2) {
		return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}

	private Point sub(Point p1, Point p2) {
		return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	/*
	 * B0, B1, B2, B3 : Bezier multipliers
	 */
	private double B0(double u) {
		double tmp = 1.0 - u;
		return (tmp * tmp * tmp);
	}

	private double B1(double u) {
		double tmp = 1.0 - u;
		return (3 * u * (tmp * tmp));
	}

	private double B2(double u) {
		double tmp = 1.0 - u;
		return (3 * u * u * tmp);
	}

	private double B3(double u) {
		return (u * u * u);
	}

	public Spline.BezierCurve splineFits(Polygon poly, List<Point> path, Point p0, Point v1, Point p3, Point v2) {
		Spline.BezierCurve outputCurve = null;
		double a, b;
		a = b = 4;
		boolean first = true;
		boolean forceLine = path.size() == 2;
		while (true) {
			Point cp0 = p0;
			Point cp1 = new Point(p0.getX() + a * v1.getX() / 3.0, p0.getY() + a * v1.getY() / 3.0);
			Point cp2 = new Point(p3.getX() - b * v2.getX() / 3.0, p3.getY() - b * v2.getY() / 3.0);
			Point cp3 = p3;
			outputCurve = new Spline.BezierCurve(cp0, cp1, cp2, cp3);

			List<Point> controlPoints = Arrays.asList(new Point[] { cp0, cp1, cp2, cp3 });
			/*
			 * TODO: Shouldn't arc length be used compare the spline length with
			 * the path length ?
			 */
			if (first && Point.distanceSum(controlPoints) < Point.distanceSum(path) - Point.PRECISION) {
				return null;
			}

			if (poly.containsCurve(outputCurve)) {
				return outputCurve;
			}

			if (a == 0 && b == 0) {
				if (forceLine) {
					return outputCurve;
				}
				break;
			}

			if (a > .01) {
				a /= 2;
				b /= 2;
			} else {
				a = b = 0;
			}
		}
		return null;
	}
}
