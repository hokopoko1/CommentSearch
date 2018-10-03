package com.smh.cs.model;

import java.math.BigInteger;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfo {

	String id;
	String videoId;
	String title;
	String videoTime;
	String type;
	String time;
	String thumbnail;
	String description;
	String score;
	BigInteger viewCount;
	BigInteger commentCount;
	int titleLength;
	int descriptionLength;
	List<String> tags;
	List<CommentInfo> commentList;
	List<ChatInfo> chatList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public List<CommentInfo> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<CommentInfo> commentList) {
		this.commentList = commentList;
	}
	public String getVideoTime() {
		return videoTime;
	}
	public void setVideoTime(String videoTime) {
		this.videoTime = videoTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public List<ChatInfo> getChatList() {
		return chatList;
	}
	public void setChatList(List<ChatInfo> chatList) {
		this.chatList = chatList;
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
	
}
