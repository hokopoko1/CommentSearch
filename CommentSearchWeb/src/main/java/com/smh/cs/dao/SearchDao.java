package com.smh.cs.dao;

import java.sql.SQLException;
import java.util.List;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

public interface SearchDao {
	
//	public void addVideoInfo(List<VideoInfo> videoInfoList) throws SQLException;

	public int addVideoInfo(VideoInfo videoInfo);
	
	public int addCommentInfo(CommentInfo commentInfo);
	
	public List<VideoInfo> selectVideoInfo(VideoInfo videoInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfo(VideoInfo videoInfo) throws Exception;
	
	public int updateSentiment(CommentInfo commentInfo);
	
	public int updateSentimentFail(CommentInfo commentInfo);
	
}
