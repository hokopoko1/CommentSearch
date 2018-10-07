package com.smh.cs.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.smh.cs.dao.SearchDao;
import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.VideoInfo;

@Service
public class SearchServiceImpl implements SearchService{
	
	@Inject
	private SearchDao dao;
	
	@Override
	public List<VideoInfo> selectVideoInfo(VideoInfo videoInfo) throws Exception {
		return dao.selectVideoInfo(videoInfo);
	}
	
	@Override
	public int addVideoInfo(VideoInfo videoInfo) throws Exception {
		return dao.addVideoInfo(videoInfo);
	}
	
	@Override
	public int addCommentInfo(CommentInfo commentInfo) throws Exception{
		return dao.addCommentInfo(commentInfo);
	}
	
	@Override
	public List<CommentInfo> selectCommentInfo(VideoInfo videoInfo) throws Exception{
		return dao.selectCommentInfo(videoInfo);
	}
	
	@Override
	public int updateSentiment(CommentInfo commentInfo) throws Exception{
		return dao.updateSentiment(commentInfo);
	}
	
	@Override
	public int updateSentimentFail(CommentInfo commentInfo) throws Exception{
		return dao.updateSentimentFail(commentInfo);
	}
	
	@Override
	public int updateCategori(CommentInfo commentInfo) throws Exception{
		return dao.updateCategori(commentInfo);
	}
	
	@Override
	public int updateCategoriFail(CommentInfo commentInfo) throws Exception{
		return dao.updateCategoriFail(commentInfo);
	}
	
	
	@Override
	public List<VideoInfo> selectVideoInfoLive(VideoInfo videoInfo) throws Exception {
		return dao.selectVideoInfoLive(videoInfo);
	}
	
	@Override
	public int addVideoInfoLive(VideoInfo videoInfo) throws Exception {
		return dao.addVideoInfoLive(videoInfo);
	}
	
	@Override
	public int addCommentInfoLive(CommentInfo commentInfo) throws Exception{
		return dao.addCommentInfoLive(commentInfo);
	}
	
	@Override
	public List<CommentInfo> selectCommentInfoLive(VideoInfo videoInfo) throws Exception{
		return dao.selectCommentInfoLive(videoInfo);
	}
	
	@Override
	public int updateSentimentLive(CommentInfo commentInfo) throws Exception{
		return dao.updateSentimentLive(commentInfo);
	}
	
	@Override
	public int updateSentimentFailLive(CommentInfo commentInfo) throws Exception{
		return dao.updateSentimentFailLive(commentInfo);
	}
	
	@Override
	public int updateCategoriLive(CommentInfo commentInfo) throws Exception{
		return dao.updateCategoriLive(commentInfo);
	}
	
	@Override
	public int updateCategoriFailLive(CommentInfo commentInfo) throws Exception{
		return dao.updateCategoriFailLive(commentInfo);
	}
}
