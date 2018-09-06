package com.smh.cs.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.smh.cs.model.VideoInfo;

@Component
public class SearchDaoImpl implements SearchDao{
	
	@Autowired
	private SqlSession sqlSession;

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	private static final String namespace = "com.smh.cs.dao.SearchDao";
	
	@Override
	public void addVideoInfo(VideoInfo videoInfoList) {
		sqlSession.insert(namespace + ".addVideoInfo", videoInfoList);
	}
	
	@Override
	public void selectVideoInfo() {
		sqlSession.selectOne(namespace + ".selectVideoInfo");
	}
}
