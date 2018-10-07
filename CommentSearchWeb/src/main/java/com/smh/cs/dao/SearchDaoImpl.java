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
	
	@Override
	public int updateCategori(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateCategori", commentInfo);
	}
	
	@Override
	public int updateCategoriFail(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateCategoriFail", commentInfo);
	}
	
	@Override
	public int addVideoInfoLive(VideoInfo videoInfo) {
		return sqlSession.insert(namespace + ".addVideoInfoLive", videoInfo);
	}
	
	@Override
	public List<VideoInfo> selectVideoInfoLive(VideoInfo videoInfo) {
		return sqlSession.selectList(namespace + ".selectVideoInfoLive", videoInfo);
	}
	
	@Override
	public int addCommentInfoLive(CommentInfo commentInfo) {
		return sqlSession.insert(namespace + ".addCommentInfoLive", commentInfo);
	}
	
	@Override
	public List<CommentInfo> selectCommentInfoLive(VideoInfo videoInfo){
		return sqlSession.selectList(namespace + ".selectCommentInfoLive", videoInfo);
	}
	
	@Override
	public int updateSentimentLive(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateSentimentLive", commentInfo);
	}
	
	@Override
	public int updateSentimentFailLive(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateSentimentFailLive", commentInfo);
	}
	
	@Override
	public int updateCategoriLive(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateCategoriLive", commentInfo);
	}
	
	@Override
	public int updateCategoriFailLive(CommentInfo commentInfo) {
		return sqlSession.update(namespace + ".updateCategoriFailLive", commentInfo);
	}
}
