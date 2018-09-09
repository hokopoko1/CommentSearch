package com.smh.cs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aggregations {
    
	private Dedup dedup;

	public Dedup getDedup() {
		return dedup;
	}

	public void setDedup(Dedup dedup) {
		this.dedup = dedup;
	}
}