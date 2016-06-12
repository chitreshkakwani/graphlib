package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.Point;
import com.graphlib.graph.layout.Spline;

public class SplineTest {

	@Test
	public void testBezierCurveLineIntersectionNoRoots() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 186), new Point(75, 109), new Point(108, 64),
				new Point(194, 72));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(105, 225), new Point(223, 45));
		assertTrue(roots != null);
		assertTrue(roots.isEmpty());
	}

	@Test
	public void testBezierCurveLineIntersectionThreeRoots() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(100, 240), new Point(30, 60), new Point(210, 230),
				new Point(160, 30));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(25, 260), new Point(230, 20));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.118, 0.519, 0.868 }),
				Point.PRECISION);
	}

	@Test
	public void testBezierCurveLineIntersectionSingleRoot() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(178, 34));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(34, 218), new Point(214, 75));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.157 }),
				Point.PRECISION);
	}

	@Test
	public void testBezierCurveLineIntersectionSingleRoot2() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(214, 75));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(34, 218), new Point(228, 63));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.159, 0.997 }),
				Point.PRECISION);
	}

	@Test
	public void testBezierCurveLineIntersectionWithLineParallelToYAxis() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(122, 223), new Point(226, 188), new Point(40, 95),
				new Point(194, 72));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(150, 250), new Point(150, 46));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.137, 0.373, 0.871 }),
				Point.PRECISION);
	}

	@Test
	public void testBezierCurveLineIntersectionWithLineParallelToXAxis() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(66, 236), new Point(107, 94), new Point(119, 45),
				new Point(216, 57));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(21, 175), new Point(215, 175));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.160 }),
				Point.PRECISION);
	}

	@Test
	public void testBezierCurvePointOnCurve() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(214, 75));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(90.679, 149.746), new Point(90.679, 149.746));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.250 }),
				Point.PRECISION);
	}
	
	@Test
	public void testBezierCurvePointNotOnCurve() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(214, 75));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(88, 130), new Point(88, 130));
		assertTrue(roots != null);
		assertTrue(roots.isEmpty());
	}
	
	@Test
	public void testBezierCurvePointOnCurveOutsideInterval() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(214, 75));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(376.823, 114.146), new Point(376.823, 114.146));
		assertTrue(roots != null);
		assertTrue(roots.isEmpty());
	}
	
	@Test
	public void testBezierCurveIntersectionWithControlPoint() {
		Spline.BezierCurve curve = new Spline.BezierCurve(new Point(96, 222), new Point(75, 109), new Point(108, 64),
				new Point(214, 75));
		List<Double> roots = curve.getLineIntersectionRoots(new Point(96, 222), new Point(96, 222));
		assertTrue(roots != null);
		Assertions.assertEqualsUnordered(roots, Arrays.asList(new Double[] { 0.0 }),
				Point.PRECISION);
	}
}
