package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A piecewise cubic Bezier curve with C0 and C1 continuity at the joining
 * points.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/B-spline">B-spline</a>
 * @see <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bezier
 *      Curve</a>
 * 
 * @author Chitresh Kakwani
 *
 */
public class Spline {

	/**
	 * A parametric cubic curve defined by four control points. The curve lies
	 * inside the convex hull formed by control points.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bezier
	 *      Curve</a>
	 * 
	 * @author Chitresh Kakwani
	 *
	 */
	public static class BezierCurve {

		Point[] controlPoints = new Point[4];

		public BezierCurve(Point p0, Point p1, Point p2, Point p3) {
			controlPoints[0] = p0;
			controlPoints[1] = p1;
			controlPoints[2] = p2;
			controlPoints[3] = p3;
		}

		public List<Point> getControlPoints() {
			return Arrays.asList(controlPoints);
		}

		/**
		 * Returns the curve parameter values for the intersection points of the
		 * curve with the line segment formed by the given points.
		 * 
		 * @param p0
		 *            Point on the line segment
		 * @param p1
		 *            Point on the line segment
		 * @return Curve parameter values
		 */
		public List<Double> getLineIntersectionRoots(Point p0, Point p1) {
			List<Double> intersectionRoots = new ArrayList<>();
			double[] xCoefficients = new double[2];
			double[] yCoefficients = new double[2];
			double[] sCoefficients;

			xCoefficients[0] = p0.getX();
			xCoefficients[1] = p1.getX() - p0.getX();
			yCoefficients[0] = p0.getY();
			yCoefficients[1] = p1.getY() - p0.getY();

			if (xCoefficients[1] == 0) {
				if (yCoefficients[1] == 0) {
					/*
					 * (x1 - x0) is 0 and so is (y1 - y0), i.e., (x0, y0) and
					 * (x1, y1) are the same points. Check if the point lies on
					 * the curve
					 */
					sCoefficients = getPolynomialCoefficients(controlPoints[0].getX(), controlPoints[1].getX(),
							controlPoints[2].getX(), controlPoints[3].getX());
					sCoefficients[0] -= xCoefficients[0];
					List<Double> xroots;
					try {
						xroots = EquationSolver.solveCubic(sCoefficients[3], sCoefficients[2], sCoefficients[1],
								sCoefficients[0]);
					} catch (NegligibleCoefficientsException e) {
						xroots = null;
					}
					sCoefficients = getPolynomialCoefficients(controlPoints[0].getY(), controlPoints[1].getY(),
							controlPoints[2].getY(), controlPoints[3].getY());
					sCoefficients[0] -= yCoefficients[0];
					List<Double> yroots;
					try {
						yroots = EquationSolver.solveCubic(sCoefficients[3], sCoefficients[2], sCoefficients[1],
								sCoefficients[0]);
					} catch (NegligibleCoefficientsException e) {
						yroots = null;
					}
					if (xroots == null) {
						if (yroots == null) {
							return null;
						} else {
							for (int j = 0; j < yroots.size(); j++) {
								intersectionRoots.add(yroots.get(j));
							}
						}
					} else if (yroots == null) {
						for (int i = 0; i < xroots.size(); i++) {
							intersectionRoots.add(xroots.get(i));
						}
					} else {
						for (int i = 0; i < xroots.size(); i++) {
							/*
							 * t can only take values in the [0,1] interval.
							 */
							if (xroots.get(i) < 0 || xroots.get(i) > 1) {
								continue;
							}
							for (int j = 0; j < yroots.size(); j++) {
								if (Math.abs(xroots.get(i) - yroots.get(j)) <= Point.PRECISION) {
									/*
									 * For a point on the curve, t - the Bezier
									 * curve parameter - should have same values
									 * for x and y-coordinates.
									 */
									intersectionRoots.add(xroots.get(i));
								}
							}
						}
					}

				} else {
					/*
					 * (x1 - x0) is 0. Line equation takes the form : x - x0 = 0
					 * Substitute x value of Bezier polynomial in this equation
					 * and solve the resulting cubic equation for t - the Bezier
					 * curve parameter.
					 */
					sCoefficients = getPolynomialCoefficients(controlPoints[0].getX(), controlPoints[1].getX(),
							controlPoints[2].getX(), controlPoints[3].getX());
					sCoefficients[0] -= xCoefficients[0];
					List<Double> xroots;
					try {
						xroots = EquationSolver.solveCubic(sCoefficients[3], sCoefficients[2], sCoefficients[1],
								sCoefficients[0]);
					} catch (NegligibleCoefficientsException e) {
						return null;
					}

					for (int i = 0; i < xroots.size(); i++) {
						double root = xroots.get(i);
						/*
						 * t can only take values in the [0,1] interval.
						 */
						if (root >= 0 && root <= 1) {
							sCoefficients = getPolynomialCoefficients(controlPoints[0].getY(), controlPoints[1].getY(),
									controlPoints[2].getY(), controlPoints[3].getY());

							/*
							 * Get the Bezier curve y-coordinate value for the
							 * given root.
							 */
							double y = sCoefficients[0]
									+ root * (sCoefficients[1] + root * (sCoefficients[2] + root * sCoefficients[3]));
							/*
							 * Obtain the ratio (y - y0)/(y1 - y0). If the ratio
							 * is smaller than one and greater than 0, y lies on
							 * the line segment (x0, y0), (x1, y1) and the root
							 * under consideration is a valid intersection
							 * point.
							 */
							double ratio = (y - yCoefficients[0]) / yCoefficients[1];
							if ((0 <= ratio) && (ratio <= 1)) {
								intersectionRoots.add(root);
							}
						}
					}
				}
			} else {
				double m = yCoefficients[1] / xCoefficients[1];
				sCoefficients = getPolynomialCoefficients(controlPoints[0].getY() - m * controlPoints[0].getX(),
						controlPoints[1].getY() - m * controlPoints[1].getX(),
						controlPoints[2].getY() - m * controlPoints[2].getX(),
						controlPoints[3].getY() - m * controlPoints[3].getX());
				sCoefficients[0] += m * xCoefficients[0] - yCoefficients[0];
				/*
				 * Solve for t - the Bezier curve parameter.
				 */
				List<Double> roots;
				try {
					roots = EquationSolver.solveCubic(sCoefficients[3], sCoefficients[2], sCoefficients[1],
							sCoefficients[0]);
				} catch (NegligibleCoefficientsException e) {
					return null;
				}

				for (int i = 0; i < roots.size(); i++) {
					double root = roots.get(i);
					/*
					 * t can only take values in the [0,1] interval.
					 */
					if (root >= 0 && root <= 1) {
						/*
						 * Get the Bezier polynomial x co-ordinate value for the
						 * given root.
						 */
						sCoefficients = getPolynomialCoefficients(controlPoints[0].getX(), controlPoints[1].getX(),
								controlPoints[2].getX(), controlPoints[3].getX());
						double x = sCoefficients[0]
								+ root * (sCoefficients[1] + root * (sCoefficients[2] + root * sCoefficients[3]));
						/*
						 * Obtain the ratio (x - x0)/(x1 - x0). This ratio is
						 * equal to (y - y0)/(y1-y0). If the ratio is smaller
						 * than one and greater than 0, x lies on the line
						 * segment (x0, y0), (x1, y1) and the root under
						 * consideration is a valid intersection point.
						 */
						double ratio = (x - xCoefficients[0]) / xCoefficients[1];
						if ((0 <= ratio) && (ratio <= 1)) {
							intersectionRoots.add(root);
						}
					}
				}
			}

			return intersectionRoots;
		}

		static double[] getPolynomialCoefficients(double v0, double v1, double v2, double v3) {
			double[] coeff = new double[4];
			coeff[3] = v3 + 3 * v1 - (v0 + 3 * v2);
			coeff[2] = 3 * v0 + 3 * v2 - 6 * v1;
			coeff[1] = 3 * (v1 - v0);
			coeff[0] = v0;

			return coeff;
		}
	}

	List<BezierCurve> curves = new ArrayList<>();

	public Spline() {
	}

	/**
	 * Returns the list of piecewise Bezier curves that form this spline.
	 * 
	 * @return List of Bezier curves
	 */
	List<BezierCurve> getBezierCurves() {
		List<BezierCurve> ret = new ArrayList<>();
		ret.addAll(curves);
		return ret;
	}

	/**
	 * Adds a Bezier curve with the specified control points to the spline.
	 * 
	 * @param p0
	 *            Bezier curve control point P0
	 * @param p1
	 *            Bezier curve control point P1
	 * @param p2
	 *            Bezier curve control point P2
	 * @param p3
	 *            Bezier curve control point P4
	 */
	public void addCurve(Point p0, Point p1, Point p2, Point p3) {
		if (p0 == null || p1 == null || p2 == null || p3 == null) {
			throw new IllegalArgumentException("Curve points can't be null.");
		}

		if (!curves.isEmpty()) {
			List<Point> prevCurveControlPoints = curves.get(curves.size() - 1).getControlPoints();
			/*
			 * Last control point from the previous curve must coincide with the
			 * first control point of the curve being added (C0 continuity).
			 */
			if (prevCurveControlPoints.get(prevCurveControlPoints.size() - 1).equals(p0)) {
				throw new IllegalArgumentException("First point of the curve doesn't coincide with the "
						+ "last control point of the previous curve");
			}
		}

		curves.add(new BezierCurve(p0, p1, p2, p3));
	}
}
