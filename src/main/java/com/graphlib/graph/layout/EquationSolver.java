package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.List;

public class EquationSolver {

	public EquationSolver() {
		
	}
	
	public static List<Double> solveLinear(double a, double b) {
		List<Double> roots = new ArrayList<>();
		if (isApproximatelyZero(a)) {
			if (isApproximatelyZero(b)) {
				return null;
			} else
				return roots;
		}

		roots.add(-b / a);
		return roots;
	}
	
	public static List<Double> solveQuadratic(double a, double b, double c) {
		List<Double> roots = new ArrayList<>();

		if (isApproximatelyZero(a)) {
			/*
			 * a equals 0.
			 */
			return solveLinear(b, c);
		}
		double b_over_2a = b / (2 * a);
		double c_over_a = c / a;

		double disc = b_over_2a * b_over_2a - c_over_a;
		if (disc < 0) {
			return roots;
		} else if (disc == 0) {
			roots.add(-b_over_2a);
		} else {
			roots.add(-b_over_2a + Math.sqrt(disc));
			roots.add(-2 * b_over_2a - roots.get(0));
		}
		return roots;
	}
	
	public static List<Double> solveCubic(double a, double b, double c, double d) {
		List<Double> roots = new ArrayList<>();

		if (isApproximatelyZero(a)) {
			/*
			 * a equals 0.
			 */
			return solveQuadratic(b, c, d);
		}

		double b_over_3a = b / (3 * a);
		double c_over_a = c / a;
		double d_over_a = d / a;

		double p = b_over_3a * b_over_3a;
		double q = 2 * b_over_3a * p - b_over_3a * c_over_a + d_over_a;
		p = c_over_a / 3 - p;
		double disc = q * q + 4 * p * p * p;

		if (disc < 0) {
			double r = .5 * Math.sqrt(-disc + q * q);
			double theta = Math.atan2(Math.sqrt(-disc), -q);
			double temp = 2 * Math.cbrt(r);
			roots.add(temp * Math.cos(theta / 3));
			roots.add(temp * Math.cos((theta + Math.PI + Math.PI) / 3));
			roots.add(temp * Math.cos((theta - Math.PI - Math.PI) / 3));

		} else {
			double alpha = .5 * (Math.sqrt(disc) - q);
			double beta = -q - alpha;
			roots.add(Math.cbrt(alpha) + Math.cbrt(beta));
			if (disc <= 0) {
				roots.add(-.5 * roots.get(0));
				roots.add(-.5 * roots.get(0));
			}
		}

		for (int i = 0; i < roots.size(); i++) {
			roots.set(i, roots.get(i) - b_over_3a);
		}
		return roots;
	}
	
	private static boolean isApproximatelyZero(double value) {
		if ((value < 1E-7) && (value > -1E-7)) {
			return true;
		}
		
		return false;
	}
}
