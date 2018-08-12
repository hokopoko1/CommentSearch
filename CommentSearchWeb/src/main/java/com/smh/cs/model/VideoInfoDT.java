package com.smh.cs.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfoDT {

	private List<VideoInfo> data ;

	public List<VideoInfo> getData() {
		return data;
	}

	public void setData(List<VideoInfo> data) {
		this.data = data;
	}
	
}
