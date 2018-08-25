package com.smh.cs.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfo {

	String videoId;
	String title;
	List<CommentInfo> commentList;
	
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<CommentInfo> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<CommentInfo> commentList) {
		this.commentList = commentList;
	}
	
}
