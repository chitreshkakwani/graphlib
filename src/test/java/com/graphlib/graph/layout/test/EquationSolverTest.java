package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.EquationSolver;

public class EquationSolverTest {

	private static void assertContains(List<Double> values, Double expected, double delta) {
		for(Double v : values) {
			if (Double.compare(v, expected) == 0) {
	            return;
	        }
	        if ((Math.abs(v - expected) <= delta)) {
	            return;
	        }
		}
		
		fail("expected : <" + expected + "> not in list : " + values);
	}
	
	private static void assertContainsAll(List<Double> actual, List<Double> expected, double delta) {
		for(Double e : expected) {
			assertContains(actual, e, delta);
		}
	}
	
	private static void assertContainsOnly(List<Double> actual, List<Double> expected, double delta) {
		for(Double a : actual) {
			/*
			 * All values from the actual list should be present in expected list.
			 */
			assertContains(expected, a, delta);
		}
	}
	
	@Test
	public void testSolveLinear() {
		List<Double> roots = EquationSolver.solveLinear(792.456, 832.715);
		assertTrue(roots.size() == 1);
		assertContains(roots, -1.051, 1E-3);
	}
	
	@Test
	public void testSolveLinearWithInvalidCoefficients() {
		List<Double> roots = EquationSolver.solveLinear(1E-12, 1E-12);
		assertTrue(roots == null);
	}
	
	@Test
	public void testSolveLinearWithOneInvalidCoefficient() {
		List<Double> roots = EquationSolver.solveLinear(1E-12, 24);
		assertTrue(roots.isEmpty());
	}
	
	@Test
	public void testSolveQuadraticWithComplexRoots() {
		List<Double> roots = EquationSolver.solveQuadratic(27.0, 9.0, 3.0);
		assertTrue(roots.isEmpty());
	}
	
	@Test
	public void testSolveQuadraticWithSingleRealRoot() {
		List<Double> roots = EquationSolver.solveQuadratic(1, -12, 36);
		assertTrue(roots.size() == 1);
	}
	
	@Test
	public void testSolveQuadraticWithMultipleRealRoots1() {
		List<Double> roots = EquationSolver.solveQuadratic(1, 2, -8);
		assertTrue(roots.size() == 2);
	}
	
	@Test
	public void testSolveQuadraticWithMultipleRealRoots2() {
		List<Double> roots = EquationSolver.solveQuadratic(2.5, 66.8, 45.8);
		assertTrue(roots.size() == 2);
		assertContainsAll(roots, Arrays.asList(new Double[] {-26.016, -0.70419}), 1E-3);
	}
	
	@Test
	public void testSolveCubicWithOneRealRoot() {
		List<Double> roots = EquationSolver.solveCubic(32.55, 74.93, 48.26, 120.07);
		assertTrue(roots.size() == 1);
		assertContains(roots, -2.342, 1E-3);
	}
	
	@Test
	public void testSolveCubicWithTwoDistinctRoots() {
		List<Double> roots = EquationSolver.solveCubic(1, 1, -1, -1);
		assertTrue(roots.size() == 3);
		assertContainsOnly(roots, Arrays.asList(new Double[] {1.0, -1.0}), 1E-3);
	}
	
	@Test
	public void testSolveCubicWithThreeRealRoot() {
		List<Double> roots = EquationSolver.solveCubic(2, -4, -22, 24);
		assertTrue(roots.size() == 3);
		assertContainsAll(roots, Arrays.asList(new Double[] {4.0, -3.0, 1.0}), 1E-3);
	}
}
