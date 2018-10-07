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
	
	public int updateCategori(CommentInfo commentInfo);
	
	public int updateCategoriFail(CommentInfo commentInfo);
	
	public int addVideoInfoLive(VideoInfo videoInfo);
	
	public int addCommentInfoLive(CommentInfo commentInfo);
	
	public List<VideoInfo> selectVideoInfoLive(VideoInfo videoInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfoLive(VideoInfo videoInfo) throws Exception;
	
	public int updateSentimentLive(CommentInfo commentInfo);
	
	public int updateSentimentFailLive(CommentInfo commentInfo);
	
	public int updateCategoriLive(CommentInfo commentInfo);
	
	public int updateCategoriFailLive(CommentInfo commentInfo);
	
	public int addVideoInfoPop(VideoInfo videoInfo);
	
	public int addCommentInfoPop(CommentInfo commentInfo);
	
	public List<VideoInfo> selectVideoInfoPop(VideoInfo videoInfo) throws Exception;
	
	public List<CommentInfo> selectCommentInfoPop(VideoInfo videoInfo) throws Exception;
	
	public int updateSentimentPop(CommentInfo commentInfo);
	
	public int updateSentimentFailPop(CommentInfo commentInfo);
	
	public int updateCategoriPop(CommentInfo commentInfo);
	
	public int updateCategoriFailPop(CommentInfo commentInfo);
	
}
