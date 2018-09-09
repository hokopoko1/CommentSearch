package com.smh.cs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hit {
    @JsonProperty(value = "_index")
    private String index;
 
    @JsonProperty(value = "_type")
    private String type;
 
    @JsonProperty(value = "_id")
    private String id;
 
    @JsonProperty(value = "_score")
    private Double score;
 
    @JsonProperty(value = "_source")
    private VideoInfoLog source;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public VideoInfoLog getSource() {
		return source;
	}

	public void setSource(VideoInfoLog source) {
		this.source = source;
	}
    
}