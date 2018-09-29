package com.smh.cs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.YouTube.Captions.Download;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import com.smh.cs.dao.SearchDao;
import com.smh.cs.model.Bucket;
import com.smh.cs.model.ChatInfo;
import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.Hit;
import com.smh.cs.model.Hits;
import com.smh.cs.model.ResponseHits;
import com.smh.cs.model.Return;
import com.smh.cs.model.VideoInfo;
import com.smh.cs.model.VideoInfoLog;
import com.smh.util.Auth;

@Component
public class SearchSvc {

	private static final Logger logger = LoggerFactory.getLogger(SearchSvc.class);
	
	private static final Logger SERVICE_LOGGER = LoggerFactory.getLogger("SERVICE_LOGGER");
	
	/** Global instance properties filename. */
	private static String PROPERTIES_FILENAME = "youtube.properties";

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static final String LIVE_CHAT_FIELDS = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
			+ "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
			+ "nextPageToken,pollingIntervalMillis";

	/**
	 * Global instance of the max number of videos we want returned (50 = upper
	 * limit per page).
	 */
	private static final long NUMBER_OF_VIDEOS_RETURNED = 20;
	
	/**
     * Define a global variable that specifies the caption download format.
     */
    private static final String SRT = "srt";

	/** Global instance of Youtube object to make all API requests. */
	private static YouTube youtube;
	
	@Autowired
	private static RestTemplate restTemplate;
	
	@Autowired
	SearchDao searchDao;
	
	public List<VideoInfo> csearchVideo(String keyword, String mode) throws IOException {
		//TODO:
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
//      ResponseEntity<Return> response = null;
//		HttpEntity<Object> requestEntity = null;
		
//    	response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Return.class);
    	
		JSONObject requestBody = new JSONObject();
		JSONObject multi_match = new JSONObject();
		JSONObject query = new JSONObject();
		
		JSONObject comment = new JSONObject();
		JSONObject terms = new JSONObject();
		JSONObject dedup = new JSONObject();
		JSONObject fields = new JSONObject();
		JSONObject field = new JSONObject();
		JSONArray fieldData =  new JSONArray();
		
		JSONObject dedup_docs = new JSONObject();
		JSONObject top_hits = new JSONObject();
		JSONObject size = new JSONObject();
		
		JSONObject aggs = new JSONObject();
		JSONObject aggsh = new JSONObject();
		JSONObject max_score = new JSONObject();
		
		JSONObject max_score2 = new JSONObject();
		JSONObject script = new JSONObject();
		
		fieldData.add("title");
		if( "comment".equals(mode) ) {
			fieldData.add("comment");
		}
		fieldData.add("description");
		
		multi_match.put("query", keyword);
		multi_match.put("fields", fieldData);
		
		query.put("multi_match", multi_match);
		
		terms.put("field", "videoTime");
		terms.put("size", "20");
		
		max_score.put("max_score", "desc");
		terms.put("order", max_score);
		
		top_hits.put("size", "1");
		dedup_docs.put("top_hits", top_hits);
		
		script.put("script", "_score");
		max_score2.put("max", script);
		aggs.put("max_score", max_score2);
		aggs.put("dedup_docs", dedup_docs);
		
		dedup.put("terms", terms);
		dedup.put("aggs", aggs);
		
		aggsh.put("dedup", dedup);
		
		requestBody.put("query", query);
		requestBody.put("aggs", aggsh);
		
//		requestEntity = new HttpEntity<Object>(requestBody, headers);
		RestClient restClient = RestClient.builder(
		        new HttpHost("124.111.196.176", 9200, "http"),
		        new HttpHost("124.111.196.176", 9201, "http")).build();
		
		Request request = new Request(
			    "POST",  
			    "/csearch/1/_search");   
		
		String reqbody = requestBody.toJSONString();
		
		request.setEntity(new NStringEntity(
				reqbody,
		        ContentType.APPLICATION_JSON));
		
		Response response = restClient.performRequest(request);
		String responseBody = EntityUtils.toString(response.getEntity());
		
		ObjectMapper mapper = new ObjectMapper();
		
		ResponseHits responseHits = mapper.readValue(responseBody, ResponseHits.class);
		
		List<VideoInfo> csearchList = new ArrayList<VideoInfo>();
		VideoInfo tmpVideoInfo = new VideoInfo();
		String thumbnail = null;
		
		for( Bucket bucket : responseHits.getAggregations().getDedup().getBuckets() ) {
			
			tmpVideoInfo = new VideoInfo();
			
			for( Hit hit :  bucket.getDedup_docs().getHits().getHits() ) {
				thumbnail = "https://i.ytimg.com/vi/" + hit.getSource().getVideoId() + "/hqdefault.jpg";
				tmpVideoInfo.setVideoId(hit.getSource().getVideoId());
				tmpVideoInfo.setTitle(hit.getSource().getTitle());
				tmpVideoInfo.setTime(hit.getSource().getTime());
				tmpVideoInfo.setScore(hit.getScore().toString());
				tmpVideoInfo.setThumbnail(thumbnail);
				
				csearchList.add(tmpVideoInfo);
			}
		}
		
		return csearchList;
	}
	
	
	public List<VideoInfo> searchVideo(String keyword, String mode, String startDate, String endDate) {
		
		Properties properties = new Properties();
	    try {
	      InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	      properties.load(in);

	    } catch (IOException e) {
	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	          + " : " + e.getMessage());
	      System.exit(1);
	    }
	    
	    List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
		try {
			/*
			 * The YouTube object is used to make all API requests. The last argument is
			 * required, but because we don't need anything initialized when the HttpRequest
			 * is initialized, we override the interface and provide a no-op function.
			 */
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-cmdline-search-sample").build();

			// Get query term from user.
//			String queryTerm = getInputQuery();

			
			File txt = new File(SearchSvc.class.getResource("/wordlist.txt").getFile());
			Scanner scan = new Scanner(txt);
			ArrayList<String> testData = new ArrayList<String>() ;
			while(scan.hasNextLine()){
				testData.add(scan.nextLine());
			}
			
			String queryTerm = keyword;
			
			YouTube.Search.List search = youtube.search().list("id,snippet");
			
			if("live".equals(mode)) {
				search.setMaxResults(1L);
				search.setEventType("live");
			}	
			
			/*
			 * It is important to set your developer key from the Google Developer Console
			 * for non-authenticated requests (found under the API Access tab at this link:
			 * code.google.com/apis/). This is good practice and increased your quota.
			 */
			String apiKey = properties.getProperty("youtube.apikey");
			search.setKey(apiKey);
			search.setQ(queryTerm);
			//search.setVideoDuration("short");
			search.setOrder("viewCount");
			search.setRegionCode("US");
//			search.setChannelId("UChlgI3UHCOnwUGzWzbJ3H5w");
//			search.setEventType("completed");
			/*
			 * We are only searching for videos (not playlists or channels). If we were
			 * searching for more, we would add them as a string like this:
			 * "video,playlist,channel".
			 */
			search.setType("video");
			
			if( startDate != null) {
//				startDate = "2018-09-10T00:00:00Z";
//				endDate = "2018-09-11T00:00:00Z";
			    DateTime startDateTime = DateTime.parseRfc3339(startDate);
			    DateTime endDateTime = DateTime.parseRfc3339(endDate);
				search.setPublishedAfter(startDateTime);
				search.setPublishedBefore(endDateTime);
			}
			
			/*
			 * This method reduces the info returned to only the fields we need and makes
			 * calls more efficient.
			 */
			//search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			if("csearch".equals(mode)) {
				search.setMaxResults(1L);
			}
			
			int w=0;
			
			for(String testKeywork: testData) {
				
				System.out.println("====================>" + w++);
				
				search.setQ(testKeywork);
				
				SearchListResponse searchResponse = search.execute();
	
				List<SearchResult> searchResultList = searchResponse.getItems();
				
				if( "search".equals(mode) ) {
					if (searchResultList != null) {
						prettyPrint(searchResultList.iterator(), queryTerm, videoInfoList);
					}
				}else if( "live".equals(mode) ) {
					if (searchResultList != null) {
						prettyPrintLive(searchResultList.iterator(), queryTerm, videoInfoList);
					}
				}
				else {
	//				for(int i=1 ; i>0 ; i--) {
					
						if (searchResultList != null) {
							prettyPrintCsearch(searchResultList.iterator(), queryTerm, videoInfoList);
						}
						search.setPageToken(searchResponse.getNextPageToken());
						searchResponse = search.execute();
						searchResultList = searchResponse.getItems();
	//				}
				}
			}
			
		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		//searchDao.addVideoInfo(videoInfoList.get(0));
		//searchDao.selectVideoInfo();
		
		return videoInfoList;
	}
	
	public List<VideoInfo> searchPop() {
		
		Properties properties = new Properties();
	    try {
	      InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	      properties.load(in);

	    } catch (IOException e) {
	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	          + " : " + e.getMessage());
	      System.exit(1);
	    }
	    
	    List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
		try {
			/*
			 * The YouTube object is used to make all API requests. The last argument is
			 * required, but because we don't need anything initialized when the HttpRequest
			 * is initialized, we override the interface and provide a no-op function.
			 */
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-cmdline-search-sample").build();

			// Get query term from user.
//			String queryTerm = getInputQuery();

			
			File txt = new File(SearchSvc.class.getResource("/wordlist.txt").getFile());
			Scanner scan = new Scanner(txt);
			ArrayList<String> testData = new ArrayList<String>() ;
			while(scan.hasNextLine()){
				testData.add(scan.nextLine());
			}
			
//			String queryTerm = keyword;
			
			YouTube.Search.List search = youtube.search().list("id,snippet");
			
			/*
			 * It is important to set your developer key from the Google Developer Console
			 * for non-authenticated requests (found under the API Access tab at this link:
			 * code.google.com/apis/). This is good practice and increased your quota.
			 */
			String apiKey = properties.getProperty("youtube.apikey");
			search.setKey(apiKey);
			search.setOrder("viewCount");
			search.setRegionCode("US");
			/*
			 * We are only searching for videos (not playlists or channels). If we were
			 * searching for more, we would add them as a string like this:
			 * "video,playlist,channel".
			 */
			search.setType("video");
			
			/*
			 * This method reduces the info returned to only the fields we need and makes
			 * calls more efficient.
			 */
			//search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			search.setMaxResults(50L);
			
			int w=0;
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResultList = searchResponse.getItems();
			
			for(int i=0 ; i<6 ; i++) {
				
				System.out.println("====================>" + w++);
				if (searchResultList != null) {
					prettyPrintCsearch(searchResultList.iterator(), null, videoInfoList);
				}
				search.setPageToken(searchResponse.getNextPageToken());
				searchResponse = search.execute();
				searchResultList = searchResponse.getItems();
			}
			
		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		//searchDao.addVideoInfo(videoInfoList.get(0));
		//searchDao.selectVideoInfo();
		
		return videoInfoList;
	}
	
	private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}
		
		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			VideoInfo tmpVideo = new VideoInfo();
			
			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {
				// Thumbnail thumbnail =
				// singleVideo.getSnippet().getThumbnails().get("default");

//				System.out.println(" Video Id: " + rId.getVideoId());
//				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				// System.out.println(" Thumbnail: " + thumbnail.getUrl());
//				System.out.println("\n-------------------------------------------------------------\n");
				
				logger.debug(rId.getVideoId());
				logger.debug(singleVideo.getSnippet().getTitle());
				
				String thumbnail = "https://i.ytimg.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
				
				tmpVideo.setVideoId(rId.getVideoId());
				tmpVideo.setTitle(singleVideo.getSnippet().getTitle());
				tmpVideo.setThumbnail(thumbnail);
				
				videoInfoList.add(tmpVideo);
			}
		}
	}
	
	private static void prettyPrintLive(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}
		
		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			VideoInfo tmpVideo = new VideoInfo();
			
			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {
				// Thumbnail thumbnail =
				// singleVideo.getSnippet().getThumbnails().get("default");

//				System.out.println(" Video Id: " + rId.getVideoId());
//				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				// System.out.println(" Thumbnail: " + thumbnail.getUrl());
//				System.out.println("\n-------------------------------------------------------------\n");
				
				logger.debug(rId.getVideoId());
				logger.debug(singleVideo.getSnippet().getTitle());

				String thumbnail = "https://i.ytimg.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
				
				List<Video> videoResultList = Videos(rId.getVideoId());
				
				tmpVideo.setVideoId(rId.getVideoId());
				tmpVideo.setTitle(singleVideo.getSnippet().getTitle());
				tmpVideo.setVideoTime(singleVideo.getSnippet().getPublishedAt().toString());
				tmpVideo.setThumbnail(thumbnail);
				
				if(videoResultList != null && !videoResultList.isEmpty()) {
					tmpVideo.setDescription(videoResultList.get(0).getSnippet().getDescription());
					tmpVideo.setTags(videoResultList.get(0).getSnippet().getTags());
					tmpVideo.setViewCount(videoResultList.get(0).getStatistics().getViewCount());
					
				}
				tmpVideo.setChatList(getLiveChat(rId.getVideoId(), singleVideo.getSnippet().getTitle(), tmpVideo.getVideoTime(), tmpVideo.getDescription(), tmpVideo.getViewCount(), videoResultList));
				
				videoInfoList.add(tmpVideo);
			}
		}
	}
	
	private static void prettyPrintCsearch(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}
		
		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			VideoInfo tmpVideo = new VideoInfo();
			
			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {
				// Thumbnail thumbnail =
				// singleVideo.getSnippet().getThumbnails().get("default");

				System.out.println(" Video Id: " + rId.getVideoId());
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				// System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
				
				logger.debug(rId.getVideoId());
				logger.debug(singleVideo.getSnippet().getTitle());
				
				String thumbnail = "https://i.ytimg.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
				
				List<Video> videoResultList = Videos(rId.getVideoId());
				
				tmpVideo.setVideoId(rId.getVideoId());
				tmpVideo.setTitle(singleVideo.getSnippet().getTitle());
				tmpVideo.setVideoTime(singleVideo.getSnippet().getPublishedAt().toString());
				tmpVideo.setThumbnail(thumbnail);
				
				if(videoResultList != null && !videoResultList.isEmpty()) {
					tmpVideo.setDescription(videoResultList.get(0).getSnippet().getDescription());
					tmpVideo.setTags(videoResultList.get(0).getSnippet().getTags());
					tmpVideo.setViewCount(videoResultList.get(0).getStatistics().getViewCount());
					tmpVideo.setCommentCount(videoResultList.get(0).getStatistics().getCommentCount());
				}
				tmpVideo.setCommentList(getComment(rId.getVideoId(), singleVideo.getSnippet().getTitle(), tmpVideo.getVideoTime(), tmpVideo.getDescription(), tmpVideo.getViewCount(), tmpVideo.getCommentCount()));
				
				videoInfoList.add(tmpVideo);
			}
		}
	}
	
	private static void prettyPrintCsearchLive(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}
		
		while (iteratorSearchResults.hasNext()) {

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			VideoInfo tmpVideo = new VideoInfo();
			
			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video")) {
				// Thumbnail thumbnail =
				// singleVideo.getSnippet().getThumbnails().get("default");

				System.out.println(" Video Id: " + rId.getVideoId());
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				// System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
				
				logger.debug(rId.getVideoId());
				logger.debug(singleVideo.getSnippet().getTitle());
				
				String thumbnail = "https://i.ytimg.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
				
				List<Video> videoResultList = Videos(rId.getVideoId());
				
				tmpVideo.setVideoId(rId.getVideoId());
				tmpVideo.setTitle(singleVideo.getSnippet().getTitle());
				tmpVideo.setVideoTime(singleVideo.getSnippet().getPublishedAt().toString());
				tmpVideo.setThumbnail(thumbnail);
				tmpVideo.setDescription(videoResultList.get(0).getSnippet().getDescription());
				tmpVideo.setViewCount(videoResultList.get(0).getStatistics().getViewCount());
				tmpVideo.setCommentCount(videoResultList.get(0).getStatistics().getCommentCount());
				tmpVideo.setTags(videoResultList.get(0).getSnippet().getTags());
				tmpVideo.setCommentList(getComment(rId.getVideoId(), singleVideo.getSnippet().getTitle(), tmpVideo.getVideoTime(), tmpVideo.getDescription(), tmpVideo.getViewCount(), tmpVideo.getCommentCount()));
				
				videoInfoList.add(tmpVideo);
			}
		}
	}
	
	private static String getInputQuery() throws IOException {

		String inputQuery = "";

		System.out.print("Please enter a search term: ");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		inputQuery = bReader.readLine();

		if (inputQuery.length() < 1) {
			// If nothing is entered, defaults to "YouTube Developers Live."
			inputQuery = "YouTube Developers Live";
		}
		return inputQuery;
	}
	
	public static List<Video> Videos(String videoId){
		Properties properties = new Properties();
	    try {
	    	InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	      properties.load(in);

	    } catch (IOException e) {
	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	          + " : " + e.getMessage());
	      System.exit(1);
	    }
	    
	    List<Video> videoResultList = new ArrayList<Video>();
	    
	    try {
		    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
		        public void initialize(HttpRequest request) throws IOException {}
		      }).setApplicationName("youtube-cmdline-search-sample").build();
	    
			YouTube.Videos.List video = youtube.videos().list("id,snippet,contentDetails,statistics,liveStreamingDetails");

			String apiKey = properties.getProperty("youtube.apikey");
			video.setKey(apiKey);
			video.setId(videoId);
//			video.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			video.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			
			VideoListResponse videoResponse = video.execute();
			videoResultList = videoResponse.getItems();
			
		} catch (GoogleJsonResponseException e) {
		      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
			          + e.getDetails().getMessage());
	    } catch (IOException e) {
	      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	    
	    return videoResultList;
	}
	
	public List<SearchResult> Search(String channelId){
		
		Properties properties = new Properties();
	    try {
	      InputStream in = SearchTest.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	      properties.load(in);

	    } catch (IOException e) {
	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	          + " : " + e.getMessage());
	      System.exit(1);
	    }

	    List<SearchResult> searchResultList = new ArrayList<SearchResult>();
	    
	    try {
	      /*
	       * The YouTube object is used to make all API requests. The last argument is required, but
	       * because we don't need anything initialized when the HttpRequest is initialized, we override
	       * the interface and provide a no-op function.
	       */
	      youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
	        public void initialize(HttpRequest request) throws IOException {}
	      }).setApplicationName("youtube-cmdline-search-sample").build();

	      // Get query term from user.
//	      String queryTerm = getInputQuery();

	      YouTube.Search.List search = youtube.search().list("id,snippet");
	      /*
	       * It is important to set your developer key from the Google Developer Console for
	       * non-authenticated requests (found under the API Access tab at this link:
	       * code.google.com/apis/). This is good practice and increased your quota.
	       */
	      String apiKey = properties.getProperty("youtube.apikey");
	      search.setKey(apiKey);
//	      search.setQ(queryTerm);
	      search.setChannelId(channelId);
	      search.setEventType("live");
	      /*
	       * We are only searching for videos (not playlists or channels). If we were searching for
	       * more, we would add them as a string like this: "video,playlist,channel".
	       */
	      search.setType("video");
	      /*
	       * This method reduces the info returned to only the fields we need and makes calls more
	       * efficient.
	       */
	      search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
	      search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	      SearchListResponse searchResponse = search.execute();

	      searchResultList = searchResponse.getItems();

//	      if (searchResultList != null) {
//		        prettyPrint(searchResultList.iterator(), queryTerm);
//		      }
	      
	    } catch (GoogleJsonResponseException e) {
	      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	          + e.getDetails().getMessage());
	    } catch (IOException e) {
	      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	    
	    return searchResultList;
	}
	
	static public List<ChatInfo> getLiveChat(String videoId, String title, String videoTime, String description, BigInteger viewCount, List<Video> videoResultList) {
		
		List<ChatInfo> chatInfoList = new ArrayList<ChatInfo>();
		
		// Read the developer key from youtube.properties
	    Properties properties = new Properties();
	    try {
	      InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	      properties.load(in);

	    } catch (IOException e) {
	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
	          + " : " + e.getMessage());
	      System.exit(1);
	    }

	    String apiKey = properties.getProperty("youtube.apikey");
		// This OAuth 2.0 access scope allows for read-only access to the
		// authenticated user's account, but not other types of account access.
		List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_READONLY);

		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
		        public void initialize(HttpRequest request) throws IOException {}
		      }).setApplicationName("youtube-cmdline-search-sample").build();
			
			if( videoResultList!= null) {
				String liveChatId = videoResultList.get(0).getLiveStreamingDetails().getActiveLiveChatId();

				if (liveChatId != null) {
					System.out.println("Live chat id: " + liveChatId);
					listChatMessages(videoId, title, videoTime, description, viewCount, liveChatId, null, 0, apiKey, videoResultList);
				} else {
					System.err.println("Unable to find a live chat id");
					//System.exit(1);
				}
				
	            System.out.println("You chose " + videoId + " to subscribe.");
			}
			
		} catch (Throwable t) {
			System.err.println("Throwable: " + t.getMessage());
			t.printStackTrace();
		}
            
        return chatInfoList;
	}
	
	private static void listChatMessages(String videoId, String title, String videoTime, String description, BigInteger viewCount, final String liveChatId, final String nextPageToken, long delayMs, final String apiKey, List<Video> videoResultList) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			// Get chat messages from YouTube
			LiveChatMessageListResponse response;
			
			try {
				response = youtube.liveChatMessages() .list(liveChatId, "snippet, authorDetails").setPageToken(nextPageToken).setKey(apiKey).setMaxResults(2000L)
						.setFields(LIVE_CHAT_FIELDS).execute();
	
				// Display messages and super chat details
				List<LiveChatMessage> messages = response.getItems();
				VideoInfoLog log = new VideoInfoLog();
				HttpEntity<Object> requestEntity = null;
				
				for (int i = 0; i < messages.size(); i++) {
					LiveChatMessage message = messages.get(i);
					LiveChatMessageSnippet snippet = message.getSnippet();
	                
					System.out.println("  - Time: " + snippet.getPublishedAt());
	                System.out.println("  - Chat: " + snippet.getDisplayMessage());
	                System.out
	                        .println("\n-------------------------------------------------------------\n");
	                
	                log.setVideoId(videoId);
	                log.setTitle(title);
	                log.setVideoTime(videoTime);
	                log.setTime(snippet.getPublishedAt().toString());
	//	                    log.setAuthor(snippet.getAuthorDisplayName());
	                log.setChat(snippet.getDisplayMessage());
	                log.setDescription(description);
	                log.setViewCount(viewCount);
	                
	                requestEntity = new HttpEntity<Object>(log, headers);
					
					ResponseEntity<String> responseLive = new RestTemplate().exchange("http://124.111.196.176:9200/live/1/", HttpMethod.POST, requestEntity, String.class);
					
	//						System.out.println(buildOutput(snippet.getPublishedAt(), snippet.getDisplayMessage(), message.getAuthorDetails(), snippet.getSuperChatDetails()));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	static public List<CommentInfo> getComment(String videoId, String title, String videoTime, String description, BigInteger viewCount, BigInteger commentCount) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
		List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
		
        try {
    	    Properties properties = new Properties();
    	    try {
    	      InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
    	      properties.load(in);

    	    } catch (IOException e) {
    	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
    	          + " : " + e.getMessage());
    	      System.exit(1);
    	    }

    	    String apiKey = properties.getProperty("youtube.apikey");
        	
        	youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
		        public void initialize(HttpRequest request) throws IOException {}
		      }).setApplicationName("youtube-cmdline-search-sample").build();
        	
            System.out.println("You chose " + videoId + " to subscribe.");
            CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads()
                    .list("snippet").setKey(apiKey).setVideoId(videoId).setMaxResults(100L).setTextFormat("plainText").execute();
            List<CommentThread> videoComments = videoCommentsListResponse.getItems();

            if (videoComments.isEmpty()) {
                System.out.println("Can't get video comments.");
            } else {
                // Print information from the API response.
                System.out
                        .println("\n================== Returned Video Comments ==================\n");

                CommentInfo commentInfo = null;
                
                VideoInfoLog log = new VideoInfoLog();
                HttpEntity<Object> requestEntity = null;
                
                for (CommentThread videoComment : videoComments) {
                	commentInfo = new CommentInfo();
                	
                    CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment()
                            .getSnippet();
//                    System.out.println("  - Time: " + snippet.getPublishedAt());
//                    System.out.println("  - Author: " + snippet.getAuthorDisplayName());
//                    System.out.println("  - Comment: " + snippet.getTextDisplay());
//                    System.out
//                            .println("\n-------------------------------------------------------------\n");
                    
                    commentInfo.setTime(snippet.getPublishedAt().toString());
                    commentInfo.setAuthor(snippet.getAuthorDisplayName());
                    commentInfo.setComment(snippet.getTextDisplay());
                    
                    log.setVideoId(videoId);
                    log.setTitle(title);
                    log.setVideoTime(videoTime);
                    log.setTime(snippet.getPublishedAt().toString());
                    log.setAuthor(snippet.getAuthorDisplayName());
                    log.setComment(snippet.getTextDisplay());
                    log.setDescription(description);
                    log.setViewCount(viewCount);
                    log.setCommentCount(commentCount);
                    
                    commentInfoList.add(commentInfo);
                    
                    try {
                    	requestEntity = new HttpEntity<Object>(log, headers);
                    	
                	    ResponseEntity<String> response = new RestTemplate().exchange("http://124.111.196.176:9200/pop/1/", HttpMethod.POST, requestEntity, String.class);
                    	
            			SERVICE_LOGGER.info(new ObjectMapper().writeValueAsString(log));
            		} catch (JsonProcessingException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
                }
                CommentThread firstComment = videoComments.get(0);
 
                // Will use this thread as parent to new reply.
                String parentId = firstComment.getId();

                // Create a comment snippet with text.
                CommentSnippet commentSnippet = new CommentSnippet();
//                commentSnippet.setTextOriginal(text);
                commentSnippet.setParentId(parentId);

                // Create a comment with snippet.
                Comment comment = new Comment();
                comment.setSnippet(commentSnippet);

            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode()
                    + " : " + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t);
            t.printStackTrace();
        }
        
        return commentInfoList;
	}


    /*
     * Prompt the user to enter text for a comment. Then return the text.
     */
    private static String getText() throws IOException {

        String text = "";

        System.out.print("Please enter a comment text: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        text = bReader.readLine();

        if (text.length() < 1) {
            // If nothing is entered, defaults to "YouTube For Developers."
            text = "YouTube For Developers.";
        }
    
        return text;
    }

    public static void downloadCaption(String captionId) throws IOException {
        // Create an API request to the YouTube Data API's captions.download
        // method to download an existing caption track.
      	 Properties properties = new Properties();
   	    try {
   	      InputStream in = SearchSvc.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
   	      properties.load(in);

   	    } catch (IOException e) {
   	      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
   	          + " : " + e.getMessage());
   	      System.exit(1);
   	    }
   	    
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		Credential credential = Auth.authorize(scopes, "captions");

		// This object is used to make YouTube Data API requests.
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("youtube-cmdline-captions-sample").build();
   	    
      	String apiKey = properties.getProperty("youtube.apikey");
        Download captionDownload = youtube.captions().download(captionId).setTfmt(SRT).setKey(apiKey);
        
        // Set the download type and add an event listener.
        MediaHttpDownloader downloader = captionDownload.getMediaHttpDownloader();

        // Indicate whether direct media download is enabled. A value of
        // "True" indicates that direct media download is enabled and that
        // the entire media content will be downloaded in a single request.
        // A value of "False," which is the default, indicates that the
        // request will use the resumable media download protocol, which
        // supports the ability to resume a download operation after a
        // network interruption or other transmission failure, saving
        // time and bandwidth in the event of network failures.
//        downloader.setDirectDownloadEnabled(false);

        // Set the download state for the caption track file.
        MediaHttpDownloaderProgressListener downloadProgressListener = new MediaHttpDownloaderProgressListener() {
            @Override
            public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                switch (downloader.getDownloadState()) {
                    case MEDIA_IN_PROGRESS:
                        System.out.println("Download in progress");
                        System.out.println("Download percentage: " + downloader.getProgress());
                        break;
                    // This value is set after the entire media file has
                    //  been successfully downloaded.
                    case MEDIA_COMPLETE:
                        System.out.println("Download Completed!");
                        break;
                    // This value indicates that the download process has
                    //  not started yet.
                    case NOT_STARTED:
                        System.out.println("Download Not Started!");
                        break;
                }
            }
        };
        downloader.setProgressListener(downloadProgressListener);

        OutputStream outputFile = new FileOutputStream("captionFile.srt");
        // Download the caption track.
        captionDownload.executeAndDownloadTo(outputFile);
      }
    
}
