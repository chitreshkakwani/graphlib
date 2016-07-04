package com.graphlib.graph.layout.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphlib.graph.layout.Box;
import com.graphlib.graph.layout.GraphLayout;
import com.graphlib.graph.layout.LayoutEdge;
import com.graphlib.graph.layout.LayoutNode;
import com.graphlib.graph.layout.Point;

public class GraphLayoutReader {
	
	private File file = null;
	
	private JsonNode root = null;
	
	public GraphLayoutReader(File file) {
		this.file = file;
	}
	
	public void initialize() throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		root = mapper.readTree(file);
	}
	
	public Box readBoundingBox() {
		JsonNode bb = root.get("boundingBox");
		JsonNode ll = bb.get("lowerLeft");
		JsonNode ur = bb.get("upperRight");
		
		return new Box(new Point(ll.get(0).asDouble(), ll.get(1).asDouble()), 
				new Point(ur.get(0).asDouble(), ur.get(1).asDouble()));
	}
	
	public GraphLayout readGraphLayout() {
		GraphLayout layout = new GraphLayout();
		JsonNode graph = root.get("graph");
		JsonNode vertices = graph.get("nodes");
		Map<Integer, LayoutNode> nodesMap = new HashMap<>();
		
		for(int i=0; i < vertices.size(); i++) {
			JsonNode vertex = vertices.get(i);
			int id = vertex.get("id").asInt();
			String name = vertex.get("label").asText();
			int rank = vertex.get("rank").asInt(18);
			int width = vertex.get("width").asInt(18);
			int height = vertex.get("height").asInt(18);
			double x = vertex.get("pos").get(0).asDouble();
			double y = vertex.get("pos").get(1).asDouble();
			LayoutNode node = new LayoutNode(id, name);
			node.setRank(rank);
			node.setLeftWidth(width / 2);
			node.setRightWidth(width / 2);
			node.setHeight(height);
			node.setxCoordinate(x);
			node.setyCoordinate(y);
			layout.addVertex(node);
			nodesMap.put(id, node);
		}
		
		JsonNode edges = graph.get("edges");
		for(int i=0; i < edges.size(); i++) {
			JsonNode edge = edges.get(i);
			int id = edge.get("id").asInt();
			String label = edge.get("label").asText();
			int fromId = edge.get("from").asInt();
			int toId = edge.get("to").asInt();
			LayoutNode src = nodesMap.get(fromId);
			LayoutNode tgt = nodesMap.get(toId);
			LayoutEdge e = new LayoutEdge(id, src, tgt);
			e.setLabel(label);
			layout.addEdge(e);
		}

		return layout;
	}
}
