package com.smh.cs.service;

import java.util.List;

import com.smh.cs.model.VideoInfo;

public interface SearchService {
	public List<VideoInfo> selectVideoInfo() throws Exception;
}
