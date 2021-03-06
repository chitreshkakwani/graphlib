package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.FunnelAlgorithm;
import com.graphlib.graph.layout.Point;
import com.graphlib.graph.layout.Polygon;
import com.graphlib.graph.layout.Spline;
import com.graphlib.graph.layout.SplineRouter;

public class FunnelAlgorithmTest {

	@Test
	public void testStraightLinePath() {
		Polygon poly = new Polygon();
		poly.addPoint(new Point(33, 192));
		poly.addPoint(new Point(33, 174));
		poly.addPoint(new Point(-144, 174));
		poly.addPoint(new Point(-144, 156));
		poly.addPoint(new Point(22, 156));
		poly.addPoint(new Point(22, 141));
		poly.addPoint(new Point(-144, 141));
		poly.addPoint(new Point(-144, 123));
		poly.addPoint(new Point(33, 123));
		poly.addPoint(new Point(33, 105));
		poly.addPoint(new Point(105, 105));
		poly.addPoint(new Point(105, 123));
		poly.addPoint(new Point(216, 123));
		poly.addPoint(new Point(216, 141));
		poly.addPoint(new Point(84, 141));
		poly.addPoint(new Point(84, 156));
		poly.addPoint(new Point(216, 156));
		poly.addPoint(new Point(216, 174));
		poly.addPoint(new Point(216, 192)); 
		
		Point source = new Point(69, 191);
		Point dest = new Point(69, 106);
		FunnelAlgorithm fa = new FunnelAlgorithm();
		List<Point> path = fa.findPath(poly, source, dest);
		assertTrue(path.size() == 2);
		assertTrue(path.get(0).equals(dest));
		assertTrue(path.get(1).equals(source));
	}
	
	@Test
	public void testSimplePath() {
		Polygon poly = new Polygon();
		poly.addPoint(new Point(10, 20));
		poly.addPoint(new Point(0, 20));
		poly.addPoint(new Point(0, 0));
		poly.addPoint(new Point(20, 0));
		poly.addPoint(new Point(20, 10));
		poly.addPoint(new Point(10, 10)); 
		
		Point source = new Point(2, 18);
		Point dest = new Point(18, 8);
		
		FunnelAlgorithm fa = new FunnelAlgorithm();
		List<Point> path = fa.findPath(poly, source, dest);
		assertTrue(path.size() == 3);
	}
	
	@Test
	public void testCurvedPath() {
		Polygon poly = new Polygon();
		poly.addPoint(new Point(-482, 105));
		poly.addPoint(new Point(-482, 87));
		poly.addPoint(new Point(-756, 87));
		poly.addPoint(new Point(-756, 69));
		poly.addPoint(new Point(-627, 69));
		poly.addPoint(new Point(-627, 54));
		poly.addPoint(new Point(-756, 54));
		poly.addPoint(new Point(-756, 36));
		poly.addPoint(new Point(-756, 18));
		poly.addPoint(new Point(112, 18));
		poly.addPoint(new Point(112, 36));
		poly.addPoint(new Point(112, 54));
		poly.addPoint(new Point(-300, 54));
		poly.addPoint(new Point(-300, 69));
		poly.addPoint(new Point(112, 69));
		poly.addPoint(new Point(112, 87));
		poly.addPoint(new Point(-215, 87));
		poly.addPoint(new Point(-215, 105));		
		
		Point source = new Point(-310, 104);
		Point dest = new Point(-250, 19);
		
		FunnelAlgorithm fa = new FunnelAlgorithm();
		List<Point> path = fa.findPath(poly, source, dest);
		assertTrue(path.size() == 3);
	}
}
