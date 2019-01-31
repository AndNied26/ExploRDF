package com.explordf.dto;

public class NodeDto {

	private String id;
	private String label;
	private int num;
	private int edgeOffset;
	private String sourceNode;
	
	public NodeDto() {
		this.edgeOffset = 0;
	}
	
	public NodeDto(String id, String label) {
		this.id = id;
		this.label = label;
		this.edgeOffset = 0;
	}
	
	public NodeDto(String id, String label, String sourceNode) {
		this.id = id;
		this.label = label;
		this.edgeOffset = 0;
		this.sourceNode = sourceNode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getEdgeOffset() {
		return edgeOffset;
	}

	public void setEdgeOffset(int edgeOffset) {
		this.edgeOffset = edgeOffset;
	}

	public String getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}
	
	
	
	
}
