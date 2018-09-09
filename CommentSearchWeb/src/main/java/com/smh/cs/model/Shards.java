package com.smh.cs.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shards {
	
	private String total;
	
	private String successful;
	
	private String skipped;
	
	private String failed;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getSuccessful() {
		return successful;
	}

	public void setSuccessful(String successful) {
		this.successful = successful;
	}

	public String getSkipped() {
		return skipped;
	}

	public void setSkipped(String skipped) {
		this.skipped = skipped;
	}

	public String getFailed() {
		return failed;
	}

	public void setFailed(String failed) {
		this.failed = failed;
	}
    
}