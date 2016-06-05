package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.graphlib.graph.layout.Point;
import com.graphlib.graph.layout.Triangle;

public class TriangleTest {

	@Test
	public void testPointContainment() {
		Point a = new Point(0, 0);
		Point b = new Point(10, 0);
		Point c = new Point(0, 10);
		
		Triangle t = new Triangle(a, b, c);
		assertFalse(t.containsPoint(new Point(11, 11)));
		assertTrue(t.containsPoint(new Point(2, 2)));
		assertFalse(t.containsPoint(new Point(-2, -2)));
		assertTrue(t.containsPoint(new Point(1, 3)));
	}
}
