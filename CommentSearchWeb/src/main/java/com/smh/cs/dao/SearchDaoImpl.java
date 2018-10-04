package com.smh.cs.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

@Repository
public class SearchDaoImpl implements SearchDao{
	
	@Inject
	private SqlSession sqlSession;

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	private static final String namespace = "com.smh.cs.dao.SearchDao";
	
	@Override
	public int addVideoInfo(VideoInfo videoInfo) {
		return sqlSession.insert(namespace + ".addVideoInfo", videoInfo);
	}
	
	@Override
	public List<VideoInfo> selectVideoInfo(VideoInfo videoInfo) {
		return sqlSession.selectList(namespace + ".selectVideoInfo", videoInfo);
	}
	
	@Override
	public int addCommentInfo(CommentInfo commentInfo) {
		return sqlSession.insert(namespace + ".addCommentInfo", commentInfo);
	}
	
	@Override
	public List<CommentInfo> selectCommentInfo(VideoInfo videoInfo){
		return sqlSession.selectList(namespace + ".selectCommentInfo", videoInfo);
	}
	
	@Override
	public int updateSentiment(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateSentiment", commentInfo);
	}
	
	@Override
	public int updateSentimentFail(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateSentimentFail", commentInfo);
	}
}
