package com.smh.cs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bucket {

	String key;
	
	String key_as_string;
	
	String doc_count;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey_as_string() {
		return key_as_string;
	}

	public void setKey_as_string(String key_as_string) {
		this.key_as_string = key_as_string;
	}

	public String getDoc_count() {
		return doc_count;
	}

	public void setDoc_count(String doc_count) {
		this.doc_count = doc_count;
	}
}
