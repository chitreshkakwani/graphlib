package com.graphlib.graph.layout.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.Diagonal;
import com.graphlib.graph.layout.DualGraph;
import com.graphlib.graph.layout.Point;
import com.graphlib.graph.layout.Polygon;

public class DualGraphTest {

	
	@Test
	public void testTrianglePath() {
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
		
		DualGraph dg = poly.getDualGraph();
		List<Diagonal> trianglePath = dg.getTrianglePath(new Point(69, 191), new Point(69, 106));
		assertTrue(!trianglePath.isEmpty());
	}
}
