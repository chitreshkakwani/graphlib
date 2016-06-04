package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.graphlib.graph.layout.Point;
import com.graphlib.graph.layout.Polygon;
import com.graphlib.graph.layout.Triangle;

public class PolygonTest {

	@Test
	public void testDiagonal() {
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
		
		assertTrue(poly.isDiagonal(1, 3));
		assertFalse(poly.isDiagonal(1, 6));
		assertTrue(poly.isDiagonal(8, 13));
		assertFalse(poly.isDiagonal(8, 17));
		assertTrue(poly.isDiagonal(3, 1));
		
	}
	
	@Test
	public void testTriangulation() {
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
		
		List<Triangle> triangles = poly.triangulate(); 
		assertTrue(triangles.size() == 17);
	}
}
