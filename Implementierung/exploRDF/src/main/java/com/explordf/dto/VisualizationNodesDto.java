package com.explordf.dto;

import java.util.LinkedList;
import java.util.List;

public class VisualizationNodesDto {

	private List<NodeDto> nodes;
	private List<EdgeDto> edges;
	private List<LabelDto> labels;
	
	public VisualizationNodesDto() {
		this.nodes = new LinkedList<NodeDto>();
		this.edges = new LinkedList<EdgeDto>();
		this.labels = new LinkedList<LabelDto>();
	}
	
	public VisualizationNodesDto(List<NodeDto> nodes, List<EdgeDto> edges, List<LabelDto> labels) {
		this.nodes = nodes;
		this.edges = edges;
		this.labels = labels;
	}

	public void addNode(NodeDto d) {
		nodes.add(d);
	}
	public void addEdge(EdgeDto e) {
		edges.add(e);
	}
	
	public void addLabel(LabelDto l) {
		labels.add(l);
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

	public List<LabelDto> getLabels() {
		return labels;
	}

	public void setLabels(List<LabelDto> labels) {
		this.labels = labels;
	}
	
	
	
}
