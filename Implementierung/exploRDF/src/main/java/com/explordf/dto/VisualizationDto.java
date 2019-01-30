package com.explordf.dto;

import java.util.LinkedList;
import java.util.List;

public class VisualizationDto {

	private List<NodeDto> nodes;
	private List<EdgeDto> edges;
	
	public VisualizationDto() {
		this.nodes = new LinkedList<NodeDto>();
		this.edges = new LinkedList<EdgeDto>();
	}
	
	public VisualizationDto(List<NodeDto> nodes, List<EdgeDto> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public void addNode(NodeDto d) {
		nodes.add(d);
	}
	public void addEdge(EdgeDto e) {
		edges.add(e);
	}
	
	public List<NodeDto> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeDto> nodes) {
		this.nodes = nodes;
	}

	public List<EdgeDto> getEdges() {
		return edges;
	}

	public void setEdges(List<EdgeDto> edges) {
		this.edges = edges;
	}	
	
}
