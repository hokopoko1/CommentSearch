package com.smh.cs.dao;

import java.sql.SQLException;
import java.util.List;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

public interface SearchDao {
	
//	public void addVideoInfo(List<VideoInfo> videoInfoList) throws SQLException;

	public int addVideoInfo(VideoInfo videoInfo);
	
	public int addCommentInfo(CommentInfo commentInfo);
	
	public List<VideoInfo> selectVideoInfo() throws Exception;
}
