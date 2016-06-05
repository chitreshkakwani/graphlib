package com.graphlib.graph.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Funnel algorithm finds the shortest path between two points in a polygon that
 * lies entirely within the polygon.
 * 
 * @author Chitresh Kakwani
 *
 */
public class FunnelAlgorithm {

	private static final Logger LOGGER = Logger.getLogger(FunnelAlgorithm.class);

	List<Point> funnelDeque = new ArrayList<>();

	Map<Point, Point> shortestPathLinks = new HashMap<>();

	int apex = 0;

	public FunnelAlgorithm() {
	}

	public List<Point> findPath(Polygon poly, Point source, Point dest) {
		apex = 0;
		DualGraph dg = poly.getDualGraph();
		List<Diagonal> trianglePath = dg.getTrianglePath(source, dest);

		List<Point> path = new ArrayList<>();
		if (trianglePath.isEmpty()) {
			path.add(source);
			path.add(dest);
			return path;
		}

		funnelDeque.add(source);

		for (int i = 0; i <= trianglePath.size(); i++) {

			Point left = null;
			Point right = null;

			if (i == trianglePath.size()) {
				/*
				 * In the last triangle.
				 */
				if (Point.getOrientation(dest, funnelDeque.get(0),
						funnelDeque.get(funnelDeque.size() - 1)) == Point.Orientation.COUNTER_CLOCKWISE) {
					left = funnelDeque.get(funnelDeque.size() - 1);
					right = dest;
				} else {
					left = dest;
					right = funnelDeque.get(funnelDeque.size() - 1);
				}
			} else {
				Diagonal diag = trianglePath.get(i);
				Point[] diagonalEndpoints = diag.getEndpoints();
				Set<Point> points = diag.getSourceVertex().getPoints();
				points.removeAll(Arrays.asList(diagonalEndpoints));
				Point third = points.iterator().next();
				if (Point.getOrientation(diagonalEndpoints[0], third,
						diagonalEndpoints[1]) == Point.Orientation.COUNTER_CLOCKWISE) {
					left = diagonalEndpoints[1];
					right = diagonalEndpoints[0];
				} else {
					left = diagonalEndpoints[0];
					right = diagonalEndpoints[1];
				}
			}

			LOGGER.debug("Left point : " + left);
			LOGGER.debug("Right point : " + right);

			if (i == 0) {
				addToDeque(false, left);
				addToDeque(true, right);
			} else {
				if (!funnelDeque.get(0).equals(right) && !funnelDeque.get(funnelDeque.size() - 1).equals(right)) {
					/*
					 * Add right point to funnel deque.
					 */
					int splitIndex = findSplitIndex(right);
					LOGGER.debug("Adding right point. Split index : " + splitIndex);
					for (int index = 0; index < splitIndex; index++) {
						funnelDeque.remove(0);
						apex--;
					}
					addToDeque(true, right);
					if (splitIndex > apex) {
						apex = splitIndex;
					}
				} else {
					/*
					 * Add left point to funnel deque.
					 */
					int splitIndex = findSplitIndex(left);
					LOGGER.debug("Adding left point. Split index : " + splitIndex);
					for (int index = splitIndex + 1; index < funnelDeque.size(); index++) {
						funnelDeque.remove(funnelDeque.size() - 1);
					}
					addToDeque(false, left);
					if (splitIndex < apex) {
						apex = splitIndex;
					}
				}
			}
		}

		path.add(dest);
		Point last = dest;
		while (shortestPathLinks.containsKey(last)) {
			last = shortestPathLinks.get(last);
			path.add(last);
		}
		return path;
	}

	private int findSplitIndex(Point p) {
		for (int i = 0; i < apex; i++) {
			if (Point.getOrientation(funnelDeque.get(i + 1), funnelDeque.get(i),
					p) == Point.Orientation.COUNTER_CLOCKWISE) {
				return i;
			}
		}

		for (int i = funnelDeque.size() - 1; i > apex; i--) {
			if (Point.getOrientation(funnelDeque.get(i - 1), funnelDeque.get(i), p) == Point.Orientation.CLOCKWISE) {
				return i;
			}
		}

		return apex;
	}

	private void addToDeque(boolean front, Point p) {
		if (front) {
			if (!funnelDeque.isEmpty()) {
				this.shortestPathLinks.put(p, funnelDeque.get(0));
			}
			funnelDeque.add(0, p);
			apex++;
		} else {
			if (!funnelDeque.isEmpty()) {
				this.shortestPathLinks.put(p, funnelDeque.get(funnelDeque.size() - 1));
			}
			funnelDeque.add(p);
		}
	}
}
