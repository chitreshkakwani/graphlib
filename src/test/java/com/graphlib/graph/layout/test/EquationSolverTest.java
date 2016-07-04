package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.EquationSolver;
import com.graphlib.graph.layout.NegligibleCoefficientsException;

public class EquationSolverTest {

	@Test
	public void testSolveLinear() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveLinear(792.456, 832.715);
		assertTrue(roots.size() == 1);
		Assertions.assertContains(roots, -1.051, 1E-3);
	}

	@Test(expected=NegligibleCoefficientsException.class)
	public void testSolveLinearWithInvalidCoefficients() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveLinear(1E-12, 1E-12);
		assertTrue(roots == null);
	}

	@Test
	public void testSolveLinearWithOneInvalidCoefficient() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveLinear(1E-12, 24);
		assertTrue(roots.isEmpty());
	}

	@Test
	public void testSolveQuadraticWithComplexRoots() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveQuadratic(27.0, 9.0, 3.0);
		assertTrue(roots.isEmpty());
	}

	@Test
	public void testSolveQuadraticWithSingleRealRoot() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveQuadratic(1, -12, 36);
		assertTrue(roots.size() == 1);
	}

	@Test
	public void testSolveQuadraticWithMultipleRealRoots1() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveQuadratic(1, 2, -8);
		assertTrue(roots.size() == 2);
	}

	@Test
	public void testSolveQuadraticWithMultipleRealRoots2() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveQuadratic(2.5, 66.8, 45.8);
		assertTrue(roots.size() == 2);
		Assertions.assertContainsAll(roots, Arrays.asList(new Double[] { -26.016, -0.70419 }), 1E-3);
	}

	@Test
	public void testSolveCubicWithOneRealRoot() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveCubic(32.55, 74.93, 48.26, 120.07);
		assertTrue(roots.size() == 1);
		Assertions.assertContains(roots, -2.342, 1E-3);
	}

	@Test
	public void testSolveCubicWithTwoDistinctRoots() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveCubic(1, 1, -1, -1);
		assertTrue(roots.size() == 3);
		Assertions.assertContainsOnly(roots, Arrays.asList(new Double[] { 1.0, -1.0 }), 1E-3);
	}

	@Test
	public void testSolveCubicWithThreeRealRoot() throws NegligibleCoefficientsException {
		List<Double> roots = EquationSolver.solveCubic(2, -4, -22, 24);
		assertTrue(roots.size() == 3);
		Assertions.assertContainsAll(roots, Arrays.asList(new Double[] { 4.0, -3.0, 1.0 }), 1E-3);
	}
	
	@Test(expected=NegligibleCoefficientsException.class)
	public void testNegligibleCoefficientsException() throws NegligibleCoefficientsException {
		EquationSolver.solveCubic(1E-9, -1E-8, 1E-10, 1E-11);
	}
}
