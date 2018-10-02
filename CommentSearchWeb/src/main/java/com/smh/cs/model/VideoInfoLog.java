package com.smh.cs.model;

import java.math.BigInteger;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfoLog {

	String videoId;
	String title;
	String videoTime;
	String time;
	String author;
	String comment;
	String description;
	BigInteger viewCount;
	BigInteger commentCount;
	int chatLength;
	int commentLength;
	int titleLength;
	int descriptionLength;
	String chat;
	
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
	public String getVideoTime() {
		return videoTime;
	}
	public void setVideoTime(String videoTime) {
		this.videoTime = videoTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getChat() {
		return chat;
	}
	public void setChat(String chat) {
		this.chat = chat;
	}
	public BigInteger getViewCount() {
		return viewCount;
	}
	public void setViewCount(BigInteger viewCount) {
		this.viewCount = viewCount;
	}
	public BigInteger getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(BigInteger commentCount) {
		this.commentCount = commentCount;
	}
	public int getCommentLength() {
		return commentLength;
	}
	public void setCommentLength(int i) {
		this.commentLength = i;
	}
	public int getTitleLength() {
		return titleLength;
	}
	public void setTitleLength(int titleLength) {
		this.titleLength = titleLength;
	}
	public int getDescriptionLength() {
		return descriptionLength;
	}
	public void setDescriptionLength(int descriptionLength) {
		this.descriptionLength = descriptionLength;
	}
	public int getChatLength() {
		return chatLength;
	}
	public void setChatLength(int chatLength) {
		this.chatLength = chatLength;
	}
	
}
