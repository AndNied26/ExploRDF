package com.explordf.dto;

public class PredicateDto {

	private String predicate;
	private boolean label;
	private boolean edge;
	
	
	public PredicateDto() {}
	
	public PredicateDto(String predicate, boolean label, boolean edge) {
		this.predicate = predicate;
		this.label = label;
		this.edge = edge;
	}
	
	
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public boolean isLabel() {
		return label;
	}
	public void setLabel(boolean label) {
		this.label = label;
	}
	public boolean isEdge() {
		return edge;
	}
	public void setEdge(boolean edge) {
		this.edge = edge;
	}
	
	
}
