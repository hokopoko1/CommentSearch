package com.smh.cs.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentInfoDT {

	List<CommentInfo> data;

	public List<CommentInfo> getData() {
		return data;
	}

	public void setData(List<CommentInfo> data) {
		this.data = data;
	}
	
}
