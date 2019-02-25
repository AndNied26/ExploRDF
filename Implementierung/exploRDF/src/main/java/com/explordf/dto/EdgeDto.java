package com.explordf.dto;

/**
 * Data Transfer Object (DTO) for an edge of the visualized graph.
 * 
 * @author Andreas Niederquell
 *
 */
public class EdgeDto {

	private String source;
	private String target;
	private String edge;
	
	public EdgeDto() {
	}
	
	public EdgeDto(String source, String target, String edge) {
		this.source = source;
		this.target = target;
		this.edge = edge;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getEdge() {
		return edge;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}
}
