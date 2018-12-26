package com.explordf.dto;

public class LabelDto {

	private String name;
	private String data;
	
	public LabelDto(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public LabelDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
}
