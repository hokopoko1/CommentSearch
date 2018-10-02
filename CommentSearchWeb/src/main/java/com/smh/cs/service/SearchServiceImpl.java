package com.smh.cs.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.smh.cs.dao.SearchDao;
import com.smh.cs.model.VideoInfo;

@Service
public class SearchServiceImpl implements SearchService{
	
	@Inject
	private SearchDao dao;
	
	@Override
	public List<VideoInfo> selectVideoInfo() throws Exception {
		return dao.selectVideoInfo();
	}
}
