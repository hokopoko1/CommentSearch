package com.smh.cs.service;

import java.util.List;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

public interface SearchService {
	public List<VideoInfo> selectVideoInfo(VideoInfo videoInfo) throws Exception;
	
	public int addVideoInfo(VideoInfo videoInfo) throws Exception;
	
	public int addCommentInfo(CommentInfo commentInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfo(VideoInfo videoInfo) throws Exception;
	
	public int updateSentiment(CommentInfo commentInfo) throws Exception;
	
	public int updateSentimentFail(CommentInfo commentInfo) throws Exception;
	
	public int updateCategori(CommentInfo commentInfo) throws Exception;
	
	public int updateCategoriFail(CommentInfo commentInfo) throws Exception;
	
	public List<VideoInfo> selectVideoInfoLive(VideoInfo videoInfo) throws Exception;
	
	public int addVideoInfoLive(VideoInfo videoInfo) throws Exception;
	
	public int addCommentInfoLive(CommentInfo commentInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfoLive(VideoInfo videoInfo) throws Exception;
	
	public int updateSentimentLive(CommentInfo commentInfo) throws Exception;
	
	public int updateSentimentFailLive(CommentInfo commentInfo) throws Exception;
	
	public int updateCategoriLive(CommentInfo commentInfo) throws Exception;
	
	public int updateCategoriFailLive(CommentInfo commentInfo) throws Exception;
}
