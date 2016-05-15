package com.graphlib.graph.layout;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

public class VertexPositionComputer {

	private static final Logger LOGGER = Logger.getLogger(VertexPositionComputer.class);

	private final GraphLayout graph;

	public VertexPositionComputer(GraphLayout graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph cannot be null.");
		}

		if (graph.getAllVertices().isEmpty()) {
			throw new IllegalArgumentException("Graph must have at least one vertex.");
		}
		this.graph = graph;
	}

	public void assignPosition() {
		assignYCoordinates();
	}
	
	private void assignYCoordinates() {
		int maxRank = graph.getMaxRank();
		double[] rankHeights1 = new double[maxRank + 1];
		double[] rankHeights2 = new double[maxRank + 1];
		double maxHeight = 0;
		for(int r = 0; r <= maxRank; r++) {
			rankHeights1[r] = rankHeights2[r] = 0;
			for(LayoutNode n : graph.getRankOrder(r)) {
				/*
				 * Symmetry assumed for a node's height above and below
				 * below the center line.
				 */
				double ht2 = n.getHeight() / 2;
				if(rankHeights1[r] < ht2) {
					rankHeights1[r] = ht2;
				}
				if(rankHeights2[r] < ht2) {
					rankHeights2[r] = ht2;
				}
			}
		}
		
		LayoutNode node = graph.getRankOrder(maxRank).get(0);
		node.setyCoordinate(rankHeights1[maxRank]);
		LOGGER.debug("Initial Y Co-ordinate assignment for rank " + maxRank + " : " + node.getyCoordinate());
		int r = maxRank;
		while(--r >= 0) {
			LayoutNode prev = graph.getRankOrder(r+1).get(0);
			double delta = rankHeights2[r+1] + rankHeights1[r] + GraphLayoutParameters.RANK_SEPARATION;
			double yCoord = prev.getyCoordinate() + delta;
			LOGGER.debug("Initial Y Co-ordinate assignment for rank " + r + " : " + yCoord);
			for(LayoutNode n : graph.getRankOrder(r)) {
				n.setyCoordinate(yCoord);
			}
			maxHeight = Math.max(maxHeight, delta);
		}		
	}
}
