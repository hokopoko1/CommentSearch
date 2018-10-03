package com.smh.cs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentInfo {

	String videoId;
	String time;
	String author;
	String comment;
	int commentLength;
	String chat;
	int chatLength;
	float sentiment;
	float magnitude;
	
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getCommentLength() {
		return commentLength;
	}
	public void setCommentLength(int commentLength) {
		this.commentLength = commentLength;
	}
	public float getSentiment() {
		return sentiment;
	}
	public void setSentiment(float sentiment) {
		this.sentiment = sentiment;
	}
	public float getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}
	public String getChat() {
		return chat;
	}
	public void setChat(String chat) {
		this.chat = chat;
	}
	public int getChatLength() {
		return chatLength;
	}
	public void setChatLength(int chatLength) {
		this.chatLength = chatLength;
	}
	
}
