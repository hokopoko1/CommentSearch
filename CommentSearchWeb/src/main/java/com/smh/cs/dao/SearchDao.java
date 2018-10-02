package com.smh.cs.dao;

import java.sql.SQLException;
import java.util.List;

import com.smh.cs.model.VideoInfo;

public interface SearchDao {
	
//	public void addVideoInfo(List<VideoInfo> videoInfoList) throws SQLException;

	public void addVideoInfo(VideoInfo videoInfoList);
	
	public List<VideoInfo> selectVideoInfo() throws Exception;
}
