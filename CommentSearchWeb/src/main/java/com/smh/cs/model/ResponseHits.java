package com.smh.cs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseHits {
	
	private String took;
	
	private String timed_out;
	
    private Hits hits;
    
    private Aggregations aggregations;
    
    @JsonProperty(value = "_shards")
    private Shards shards;
    
	public String getTook() {
		return took;
	}

	public void setTook(String took) {
		this.took = took;
	}

	public String getTimed_out() {
		return timed_out;
	}

	public void setTimed_out(String timed_out) {
		this.timed_out = timed_out;
	}

	public Hits getHits() {
		return hits;
	}

	public void setHits(Hits hits) {
		this.hits = hits;
	}

	public Aggregations getAggregations() {
		return aggregations;
	}

	public void setAggregations(Aggregations aggregations) {
		this.aggregations = aggregations;
	}

	public Shards getShards() {
		return shards;
	}

	public void setShards(Shards shards) {
		this.shards = shards;
	}
}
