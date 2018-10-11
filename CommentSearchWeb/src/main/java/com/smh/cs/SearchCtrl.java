package com.smh.cs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import javax.inject.Inject;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.cloud.language.v1beta2.*;
import com.google.common.collect.Lists;
import com.smh.cs.model.ChatInfo;
import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.CommentInfoDT;
import com.smh.cs.model.VideoInfo;
import com.smh.cs.model.VideoInfoDT;
import com.smh.cs.model.VideoInfoLog;
import com.smh.cs.service.SearchService;
import com.smh.util.NLAnalyze;

@Controller
public class SearchCtrl {
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	private static final Logger logger = LoggerFactory.getLogger(SearchCtrl.class);
	
	private static String PROPERTIES_FILENAME = "youtube.properties";
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static YouTube youtube;
	
	private static final long NUMBER_OF_VIDEOS_RETURNED = 20;
	
	private static final String LIVE_CHAT_FIELDS = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
			+ "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
			+ "nextPageToken,pollingIntervalMillis";
	
	@Inject
	private SearchService service;
	
	SearchSvc searchSvc = new SearchSvc();
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/sentiment", method = RequestMethod.POST)
	public String sentiment(@RequestParam("start") String start, @RequestParam("end") String end, Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		doSentiment(start, end);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	public String categories(@RequestParam("start") String start, @RequestParam("end") String end, Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		doCategories(start, end) ;
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/comparison", method = RequestMethod.GET)
	public String live(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "comparison";
	}
	
	@RequestMapping(value = "/searchVideo", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT searchVideo(@RequestParam("keyword") String keyword, @RequestParam("senti") String senti, @RequestParam("cate") String cate, Locale locale, Model model) throws IOException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
//		List<VideoInfo> videoInfo = searchSvc.searchVideo(keyword, "search", null, null);
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "comment", senti, cate);
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideo", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideo(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, 
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("live") String live,
			Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();

//		List<VideoInfo> videolist = service.selectVideoInfo();
		
		if( "true".equals(learning) ) {
			searchVideo(keyword, "csearch", startDate, endDate);
		} else if( "true".equals(live) ) {
			searchVideo(keyword, "live", startDate, endDate);
		}

		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "comment", null, null);
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/searchPop", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT searchPop(Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		searchPop();
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideoNoComment", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideoNoComment(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		if( "true".equals(learning) ) {
			searchSvc.searchVideo(keyword, "csearch", null, null);
		}
		
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "noComment", null, null);
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideoLive", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideoLive(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		if( "true".equals(learning) ) {
			searchVideo(keyword, "live", null, null);
		}
		
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "comment", null, null);
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/getComment", method = RequestMethod.POST)
	public @ResponseBody CommentInfoDT getComment(@RequestParam("videoId") String videoId, Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		CommentInfoDT rtn = new CommentInfoDT();
		
//		List<CommentInfo> commentInfo =  searchSvc.getComment(videoId, null, null, null, 0, 0);
		List<CommentInfo> commentInfo = null;
		rtn.setData(commentInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/downloadCaption", method = RequestMethod.POST)
	public @ResponseBody String downloadCaption(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		
		try {
			searchSvc.downloadCaption("SNAp0DEW4cBQq4MdAefKbd61OXkHsppPPDJtY_8PUg4=");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	@RequestMapping(value = "/mysqlToElasticsearch", method = RequestMethod.POST)
	public @ResponseBody String mysqlToElasticsearch(@RequestParam("start") String start, @RequestParam("end") String end,@RequestParam("limit") int limit, Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		doMysqlToElasticsearch(limit, start, end);
		
		return "home";
	}
	
	public List<CommentInfo> getComment(String videoId, String title, String videoTime, String description, BigInteger viewCount, BigInteger commentCount) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
		List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
		
		
		VideoInfo videoInfo = new VideoInfo();
		
		String thumbnail = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";
		
		videoInfo.setVideoId(videoId);
		videoInfo.setTitle(title);
		videoInfo.setVideoTime(videoTime);
		videoInfo.setType("comment");
		videoInfo.setThumbnail(thumbnail);
		videoInfo.setDescription(description);
		videoInfo.setViewCount(viewCount);
		videoInfo.setCommentCount(commentCount);
		videoInfo.setTitleLength(title.length());
		videoInfo.setDescriptionLength(description.length());
		
		try {
			service.addVideoInfo(videoInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
                    
                    commentInfo.setVideoId(videoId);
                    commentInfo.setTime(snippet.getPublishedAt().toString());
                    commentInfo.setAuthor(snippet.getAuthorDisplayName());
                    commentInfo.setComment(snippet.getTextDisplay());
                    commentInfo.setCommentLength(snippet.getTextDisplay().length());
                    
                    log.setVideoId(videoId);
                    log.setTitle(title);
                    log.setVideoTime(videoTime);
                    log.setTime(snippet.getPublishedAt().toString());
                    log.setAuthor(snippet.getAuthorDisplayName());
                    log.setComment(snippet.getTextDisplay());
                    log.setDescription(description);
                    log.setViewCount(viewCount);
                    log.setCommentCount(commentCount);
                    log.setCommentLength(snippet.getTextDisplay().length());
                    log.setTitleLength(title.length());
                    log.setDescriptionLength(description.length());
                    
                    Sentiment sentiment = NLAnalyze.getInstance().analyzeSentiment(snippet.getTextDisplay());
                    
                    if( sentiment != null) {
                    	commentInfo.setSentiment(sentiment.getScore());
                        commentInfo.setMagnitude(sentiment.getMagnitude());
                    }
                    
                    commentInfoList.add(commentInfo);

                    service.addCommentInfo(commentInfo);
                    
                    requestEntity = new HttpEntity<Object>(log, headers);
					ResponseEntity<String> response = new RestTemplate().exchange("http://124.111.196.176:9200/comment100/1/", HttpMethod.POST, requestEntity, String.class);
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
	
	public List<CommentInfo> getCommentPop(String videoId, String title, String videoTime, String description, BigInteger viewCount, BigInteger commentCount) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		
		List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
		
		
		VideoInfo videoInfo = new VideoInfo();
		
		String thumbnail = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";
		
		videoInfo.setVideoId(videoId);
		videoInfo.setTitle(title);
		videoInfo.setVideoTime(videoTime);
		videoInfo.setType("pop");
		videoInfo.setThumbnail(thumbnail);
		videoInfo.setDescription(description);
		videoInfo.setViewCount(viewCount);
		videoInfo.setCommentCount(commentCount);
		videoInfo.setTitleLength(title.length());
		videoInfo.setDescriptionLength(description.length());
		
		try {
			service.addVideoInfoPop(videoInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
                    
                    commentInfo.setVideoId(videoId);
                    commentInfo.setTime(snippet.getPublishedAt().toString());
                    commentInfo.setAuthor(snippet.getAuthorDisplayName());
                    commentInfo.setComment(snippet.getTextDisplay());
                    commentInfo.setCommentLength(snippet.getTextDisplay().length());
                    
                    log.setVideoId(videoId);
                    log.setTitle(title);
                    log.setVideoTime(videoTime);
                    log.setTime(snippet.getPublishedAt().toString());
                    log.setAuthor(snippet.getAuthorDisplayName());
                    log.setComment(snippet.getTextDisplay());
                    log.setDescription(description);
                    log.setViewCount(viewCount);
                    log.setCommentCount(commentCount);
                    log.setCommentLength(snippet.getTextDisplay().length());
                    log.setTitleLength(title.length());
                    log.setDescriptionLength(description.length());
                    
//                    Sentiment sentiment = NLAnalyze.getInstance().analyzeSentiment(snippet.getTextDisplay());
//                    
//                    if( sentiment != null) {
//                    	commentInfo.setSentiment(sentiment.getScore());
//                        commentInfo.setMagnitude(sentiment.getMagnitude());
//                    }
                    
                    commentInfoList.add(commentInfo);

                    service.addCommentInfoPop(commentInfo);
                    
                    requestEntity = new HttpEntity<Object>(log, headers);
//					ResponseEntity<String> response = new RestTemplate().exchange("http://124.111.196.176:9200/comment100/1/", HttpMethod.POST, requestEntity, String.class);
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
	
	//			System.out.println(" Video Id: " + rId.getVideoId());
	//			System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				// System.out.println(" Thumbnail: " + thumbnail.getUrl());
	//			System.out.println("\n-------------------------------------------------------------\n");
				
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
	
	public void prettyPrintLive(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

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
				
				VideoInfo videoInfo = new VideoInfo();
				
				videoInfo.setVideoId(rId.getVideoId());
				videoInfo.setTitle(singleVideo.getSnippet().getTitle());
				videoInfo.setVideoTime(singleVideo.getSnippet().getPublishedAt().toString());
				videoInfo.setType("live");
				videoInfo.setThumbnail(thumbnail);
				videoInfo.setTitleLength(singleVideo.getSnippet().getTitle().length());
				if(videoResultList != null && !videoResultList.isEmpty()) {
					videoInfo.setDescription(videoResultList.get(0).getSnippet().getDescription());
					videoInfo.setViewCount(videoResultList.get(0).getStatistics().getViewCount());
					videoInfo.setCommentCount(videoResultList.get(0).getStatistics().getCommentCount());
					videoInfo.setDescriptionLength(videoResultList.get(0).getSnippet().getDescription().length());
				}
				try {
					service.addVideoInfoLive(videoInfo);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				tmpVideo.setChatList(getLiveChat(rId.getVideoId(), singleVideo.getSnippet().getTitle(), tmpVideo.getVideoTime(), tmpVideo.getDescription(), tmpVideo.getViewCount(), videoResultList));
				
				videoInfoList.add(tmpVideo);
			}
		}
	}
	
	public void prettyPrintCsearch(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

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
	
	public void prettyPrintCsearchPop(Iterator<SearchResult> iteratorSearchResults, String query, List<VideoInfo> videoInfoList) {

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
				tmpVideo.setCommentList(getCommentPop(rId.getVideoId(), singleVideo.getSnippet().getTitle(), tmpVideo.getVideoTime(), tmpVideo.getDescription(), tmpVideo.getViewCount(), tmpVideo.getCommentCount()));
				
				videoInfoList.add(tmpVideo);
			}
		}
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
	
	public List<ChatInfo> getLiveChat(String videoId, String title, String videoTime, String description, BigInteger viewCount, List<Video> videoResultList) {
		
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

	public void listChatMessages(String videoId, String title, String videoTime, String description, BigInteger viewCount, final String liveChatId, final String nextPageToken, long delayMs, final String apiKey, List<Video> videoResultList) {
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
	                log.setTitleLength(title.length());
	                log.setDescriptionLength(description.length());
	                log.setChatLength(snippet.getDisplayMessage().length());
	                
	                CommentInfo commentInfo = new CommentInfo();
	                
	                commentInfo.setVideoId(videoId);
                    commentInfo.setTime(snippet.getPublishedAt().toString());
                    commentInfo.setChat(snippet.getDisplayMessage());
                    commentInfo.setChatLength(snippet.getDisplayMessage().length());

                    try {
						service.addCommentInfoLive(commentInfo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
	               // requestEntity = new HttpEntity<Object>(log, headers);
					//ResponseEntity<String> responseLive = new RestTemplate().exchange("http://124.111.196.176:9200/newlive/1/", HttpMethod.POST, requestEntity, String.class);
					
	//						System.out.println(buildOutput(snippet.getPublishedAt(), snippet.getDisplayMessage(), message.getAuthorDetails(), snippet.getSuperChatDetails()));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void doSentiment(String start, String end) throws Exception {
		
		VideoInfo tmpVideo = new VideoInfo();
		tmpVideo.setStart(start);
		tmpVideo.setEnd(end);
		List<VideoInfo> videoInfoList = service.selectVideoInfoPop(tmpVideo);
		
		int cnt = 0;
		
		for(VideoInfo videoInfo : videoInfoList) {
			
			System.out.println("start : " + start + "===cnt:"  + cnt++);
			
			videoInfo.setSentiUpdate("true");
			List<CommentInfo> commentInfoList = service.selectCommentInfoPop(videoInfo);

			for( CommentInfo commentInfo : commentInfoList) {
//
				//System.out.println("id:" + commentInfo.getId());
				//System.out.println("VideoId:" + commentInfo.getVideoId());
				//System.out.println("chat:" + commentInfo.getChat());
				
				if( commentInfo.getSentiUpdate() == null ) {
					Sentiment sentiment = NLAnalyze.getInstance().analyzeSentiment(commentInfo.getComment());
				//	System.out.println("Sentiment:" + sentiment);
					if( sentiment != null) {
						commentInfo.setSentiment(sentiment.getScore());
						commentInfo.setMagnitude(sentiment.getMagnitude());
						
						service.updateSentimentPop(commentInfo);
					}else {
						service.updateSentimentFailPop(commentInfo);
					}
				}
			}
			
		}
	}
	
	public void doCategories(String start, String end) throws Exception {
		
		VideoInfo tmpVideo = new VideoInfo();
		tmpVideo.setStart(start);
		tmpVideo.setEnd(end);
		List<VideoInfo> videoInfoList = service.selectVideoInfoPop(tmpVideo);
		
		int cnt = 0;
		
		for(VideoInfo videoInfo : videoInfoList) {
			
			System.out.println("start : " + start + "===cnt:"  + cnt++);
			
//			videoInfo.setSentiUpdate("true");
			List<CommentInfo> commentInfoList = service.selectCommentInfoPop(videoInfo);

			for( CommentInfo commentInfo : commentInfoList) {

				System.out.println("id:" + commentInfo.getId());
				//System.out.println("VideoId:" + commentInfo.getVideoId());
				//System.out.println("chat:" + commentInfo.getChat());
				
				//if( commentInfo.getCateUpdate() == null ) {
					List<ClassificationCategory> categories = NLAnalyze.getInstance().analyzeCategories(commentInfo.getComment());
					System.out.println(categories);
					if( categories != null ) {
						
						String category = "";
						String confidence = "";
						int i = 0;
						for( ClassificationCategory cfc : categories) {
							if( i == 0) {
								category = cfc.getName();
								confidence = confidence + cfc.getConfidence();
							}else {
								category = category + "," + cfc.getName();
								confidence = confidence + "," +  cfc.getConfidence();
							}
							i++;
						}
						
						commentInfo.setCategory(category);
						commentInfo.setConfidence(confidence);
						
						service.updateCategoriPop(commentInfo);
					}else {
						service.updateCategoriFailPop(commentInfo);
					}
					
					
				//}
			}
			
		}
	}
	
	public void doMysqlToElasticsearch(int limit, String start, String end) throws Exception {
		
		VideoInfo tmpVideo = new VideoInfo();
		tmpVideo.setStart(start);
		tmpVideo.setEnd(end);
		
		List<VideoInfo> videoInfoList = service.selectVideoInfo(tmpVideo);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		VideoInfoLog log = null;
		int cnt = 0;
		HttpEntity<Object> requestEntity = null;
		ResponseEntity<String> response = null;
		
//		for( int limit = 10 ; limit < 100 ; limit += 10) {
			for(VideoInfo videoInfo : videoInfoList) {
				
				System.out.println("start : "+ start + "limit : " + limit + "===cnt:" + cnt++);
				
				if( limit != 0) {
					videoInfo.setLimit(limit);
				}
				List<CommentInfo> commentInfoList = service.selectCommentInfo(videoInfo);
				
				log = new VideoInfoLog();
				
				for( CommentInfo commentInfo : commentInfoList) {
					
					log.setVideoId(videoInfo.getVideoId());
	                log.setTitle(videoInfo.getTitle());
	                log.setVideoTime(videoInfo.getVideoTime());
	                log.setDescription(videoInfo.getDescription());
	                log.setViewCount(videoInfo.getViewCount());
	                log.setCommentCount(videoInfo.getCommentCount());
	                log.setTitleLength(videoInfo.getTitleLength());
	                log.setDescriptionLength(videoInfo.getDescriptionLength());
	                log.setTime(commentInfo.getTime());
	                log.setAuthor(commentInfo.getAuthor());
	                log.setComment(commentInfo.getComment());
	                log.setCommentLength(commentInfo.getCommentLength());
	                log.setCategory(commentInfo.getCategory());
	                log.setConfidence(commentInfo.getConfidence());
	                log.setSentiment(commentInfo.getSentiment());
	                log.setMagnitude(commentInfo.getMagnitude());
	                log.setChat(commentInfo.getChat());
	                log.setChatLength(commentInfo.getChatLength());
				
	                requestEntity = new HttpEntity<Object>(log, headers);
	                if( limit != 0) {
	                	response = new RestTemplate().exchange("https://search-csearch-3wn4atquyg36ywxw24uvnjam3e.ap-northeast-2.es.amazonaws.com/comment"+ limit + "/1/", HttpMethod.POST, requestEntity, String.class);
	                }else {
	                	response = new RestTemplate().exchange("https://search-csearch-3wn4atquyg36ywxw24uvnjam3e.ap-northeast-2.es.amazonaws.com/comment/1/", HttpMethod.POST, requestEntity, String.class);
	                }
				}
				
			}
//		}
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
			
			for(int i=0 ; i<60 ; i++) {
				
				System.out.println("====================>" + w++);
				if (searchResultList != null) {
					prettyPrintCsearchPop(searchResultList.iterator(), null, videoInfoList);
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
}
