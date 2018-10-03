package com.smh.cs.service;

import java.util.List;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

public interface SearchService {
	public List<VideoInfo> selectVideoInfo() throws Exception;
	
	public int addVideoInfo(VideoInfo videoInfo) throws Exception;
	
	public int addCommentInfo(CommentInfo commentInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfo(VideoInfo videoInfo) throws Exception;
	
	public int updateSentiment(CommentInfo commentInfo) throws Exception;
}
