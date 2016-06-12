package com.graphlib.graph.layout.test;

import static org.junit.Assert.fail;

import java.util.List;

public class Assertions {

	public static void assertContains(List<Double> values, Double expected, double delta) {
		for (Double v : values) {
			if (Double.compare(v, expected) == 0) {
				return;
			}
			if ((Math.abs(v - expected) <= delta)) {
				return;
			}
		}

		fail("expected : <" + expected + "> not in list : " + values);
	}

	public static void assertContainsAll(List<Double> actual, List<Double> expected, double delta) {
		for (Double e : expected) {
			assertContains(actual, e, delta);
		}
	}

	public static void assertContainsOnly(List<Double> actual, List<Double> expected, double delta) {
		for (Double a : actual) {
			/*
			 * All values from the actual list should be present in expected
			 * list.
			 */
			assertContains(expected, a, delta);
		}
	}
	
	public static void assertEqualsUnordered(List<Double> actual, List<Double> expected, double delta) {
		if(actual.size() != expected.size()) {
			fail("actual and expected list sizes are different. actual : " + actual + " expected : " + expected);
		}
		
		assertContainsOnly(actual, expected, delta);
	}
}
