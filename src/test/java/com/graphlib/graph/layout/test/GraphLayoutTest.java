package com.graphlib.graph.layout.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphlib.graph.layout.Box;
import com.graphlib.graph.layout.GraphLayout;

@RunWith(Parameterized.class)
public class GraphLayoutTest {

	@Parameters
	public static Iterable<Object[]> data() throws JsonProcessingException, IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("bounding_box_0.json");
		File file = new File(url.getFile());
		GraphLayoutReader inputReader = new GraphLayoutReader(file);
		inputReader.initialize();
		GraphLayout layout = inputReader.readGraphLayout();
		Box bb = inputReader.readBoundingBox();
		return Arrays.asList(new Object[][] { { layout, bb } });
	}

	private GraphLayout inputGraph;

	private Box expectedOutputBox;

	public GraphLayoutTest(GraphLayout layout, Box boundingBox) {
		this.inputGraph = layout;
		this.expectedOutputBox = boundingBox;
	}
	
	@Test
	public void testBoundingBox() {
		assertEquals(inputGraph.computeBoundingBox(), expectedOutputBox);
	}
}
