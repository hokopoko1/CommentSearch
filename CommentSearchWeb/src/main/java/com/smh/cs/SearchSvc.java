package com.smh.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import com.smh.cs.dao.SearchDao;
import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.Hits;
import com.smh.cs.model.ResponseHits;
import com.smh.cs.model.Return;
import com.smh.cs.model.VideoInfo;
import com.smh.cs.model.VideoInfoLog;

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

	/**
	 * Global instance of the max number of videos we want returned (50 = upper
	 * limit per page).
	 */
	private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

	/** Global instance of Youtube object to make all API requests. */
	private static YouTube youtube;
	
	@Autowired
	private static RestTemplate restTemplate;
	
	@Autowired
	SearchDao searchDao;
	
	public List<VideoInfo> csearchVideo(String keyword) throws IOException {
		//TODO:
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
//      ResponseEntity<Return> response = null;
//		HttpEntity<Object> requestEntity = null;
		
//    	response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Return.class);
    	
		JSONObject requestBody = new JSONObject();
		JSONObject match = new JSONObject();
		JSONObject comment = new JSONObject();
		JSONObject terms = new JSONObject();
		JSONObject dedup = new JSONObject();
		JSONObject field = new JSONObject();
		
		comment.put("comment", keyword);
		match.put("match", comment);
		requestBody.put("query", match);
		
		field.put("field", "time");
		terms.put("terms", field);
		dedup.put("dedup", terms);
		
		requestBody.put("aggs", dedup);
		
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
		
		
		return null;
	}
	
	
	public List<VideoInfo> searchVideo(String keyword, String mode) {
		
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

			String queryTerm = keyword;
			
			YouTube.Search.List search = youtube.search().list("id,snippet");
			/*
			 * It is important to set your developer key from the Google Developer Console
			 * for non-authenticated requests (found under the API Access tab at this link:
			 * code.google.com/apis/). This is good practice and increased your quota.
			 */
			String apiKey = properties.getProperty("youtube.apikey");
			search.setKey(apiKey);
			search.setQ(queryTerm);
//			search.setMaxResults(50L);
//			search.setChannelId("UChlgI3UHCOnwUGzWzbJ3H5w");
			search.setEventType("completed");
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
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			List<SearchResult> searchResultList = searchResponse.getItems();

			if (searchResultList != null) {
				if( "csearch".equals(mode)) {
					prettyPrintCsearch(searchResultList.iterator(), queryTerm, videoInfoList);
				}else {
					prettyPrint(searchResultList.iterator(), queryTerm, videoInfoList);
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
				
				tmpVideo.setVideoId(rId.getVideoId());
				tmpVideo.setTitle(singleVideo.getSnippet().getTitle());
				tmpVideo.setThumbnail(thumbnail);
				
				tmpVideo.setCommentList(getComment(rId.getVideoId(), singleVideo.getSnippet().getTitle()));
				
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
	
	public void getLiveChatInfo(){
		
		String channelId = "";
		
		List<SearchResult> searchResultList = Search(channelId);
		
		Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
		
		while (iteratorSearchResults.hasNext()) {

	      SearchResult singleVideo = iteratorSearchResults.next();
	      ResourceId rId = singleVideo.getId();

	      // Double checks the kind is video.
	      if (rId.getKind().equals("youtube#video")) {
//		    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().get("default");

	        System.out.println(" Video Id" + rId.getVideoId());
	        System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
//		        System.out.println(" Thumbnail: " + thumbnail.getUrl());
	        System.out.println("\n-------------------------------------------------------------\n");
	       
	        List<Video> videoResultList = Videos(rId.getVideoId());
	        
	        Iterator<Video> iteratorVideosResults = videoResultList.iterator();
	        
	        while(iteratorVideosResults.hasNext()){
	        	
	        	Video singleActiveVideo = iteratorVideosResults.next();
	        	String liveChatId = singleActiveVideo.getLiveStreamingDetails().getActiveLiveChatId();
	        	System.out.println(" liveChatId" + liveChatId);
	        	
	        }
	        
	      }
	    }
		
	}
	
	public List<Video> Videos(String videoId){
		Properties properties = new Properties();
	    try {
	      InputStream in = SearchTest.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
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
	
	static public List<CommentInfo> getComment(String videoId, String title) {
		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		
		String url = "http://localhost:9200/";
		
		List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
		
        try {
            // Authorize the request.
//            Credential credential = Auth.authorize(scopes, "commentthreads");

            // This object is used to make YouTube Data API requests.
//            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
//                    .setApplicationName("youtube-cmdline-commentthreads-sample").build();

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
        	
        	youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
		        public void initialize(HttpRequest request) throws IOException {}
		      }).setApplicationName("youtube-cmdline-search-sample").build();
        	
            // Prompt the user for the ID of a video to comment on.
            // Retrieve the video ID that the user is commenting to.
            System.out.println("You chose " + videoId + " to subscribe.");

            // Prompt the user for the comment text.
            // Retrieve the text that the user is commenting.
//            String text = getText();
//            System.out.println("You chose " + text + " to subscribe.");

            // All the available methods are used in sequence just for the sake
            // of an example.

            // Call the YouTube Data API's commentThreads.list method to
            // retrieve video comment threads.
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
//                ResponseEntity<Return> response = null;
//                HttpEntity<Object> requestEntity = null;
                
                for (CommentThread videoComment : videoComments) {
                	commentInfo = new CommentInfo();
                	
                    CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment()
                            .getSnippet();
                    System.out.println("  - Time: " + snippet.getPublishedAt());
                    System.out.println("  - Author: " + snippet.getAuthorDisplayName());
                    System.out.println("  - Comment: " + snippet.getTextDisplay());
                    System.out
                            .println("\n-------------------------------------------------------------\n");
                    
                    commentInfo.setTime(snippet.getPublishedAt().toString());
                    commentInfo.setAuthor(snippet.getAuthorDisplayName());
                    commentInfo.setComment(snippet.getTextDisplay());
                    
                    log.setVideoId(videoId);
                    log.setTitle(title);
                    log.setTime(snippet.getPublishedAt().toString());
                    log.setAuthor(snippet.getAuthorDisplayName());
                    log.setComment(snippet.getTextDisplay());
                    
                    commentInfoList.add(commentInfo);
                    
                    try {
                    	url = url + "csearch/1";
                    	
//                    	requestEntity = new HttpEntity<Object>(log, headers);
//                    	response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Return.class);
                    	
//                    	HttpHeaders header = new HttpHeaders();
//                	    header.add(HttpHeaders.ACCEPT, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
//                	    ResponseEntity<String> response = new RestTemplate().exchange("http://124.111.196.176:9200/csearch/1/", HttpMethod.POST, requestEntity, String.class);
                    	
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
	
}
