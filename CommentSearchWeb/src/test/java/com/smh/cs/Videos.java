package com.smh.cs;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Prints a list of videos based on a search term.
 *
 * @author Jeremy Walker
 */
public class Videos {

  /** Global instance properties filename. */
  private static String PROPERTIES_FILENAME = "youtube.properties";

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
  private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

  /** Global instance of Youtube object to make all API requests. */
  private static YouTube youtube;


  /**
   * Initializes YouTube object to search for videos on YouTube (Youtube.Search.List). The program
   * then prints the names and thumbnails of each of the videos (only first 50 videos).
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    // Read the developer key from youtube.properties
    Properties properties = new Properties();
    try {
      InputStream in = Videos.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
      properties.load(in);

    } catch (IOException e) {
      System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
          + " : " + e.getMessage());
      System.exit(1);
    }

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
      
      String videoId = "aR8Fe1lTKHo";
      
//      String queryTerm = getInputQuery();

      YouTube.Videos.List video = youtube.videos().list("id,snippet,contentDetails,statistics,liveStreamingDetails");

		String apiKey = properties.getProperty("youtube.apikey");
		video.setKey(apiKey);
		video.setId(videoId);
//		video.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
		video.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
		
		VideoListResponse videoResponse = video.execute();
		List<Video> videoResultList = videoResponse.getItems();
        
        Iterator<Video> iteratorVideosResults = videoResultList.iterator();
        
        while(iteratorVideosResults.hasNext()){
        	
        	Video singleActiveVideo = iteratorVideosResults.next();
        	String liveChatId = singleActiveVideo.getLiveStreamingDetails().getActiveLiveChatId();
        	System.out.println(" liveChatId = " + liveChatId);
        	
        }

      if (videoResultList != null) {
//        prettyPrint(videoResultList.iterator(), queryTerm);
      }
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    } catch (IOException e) {
      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /*
   * Returns a query term (String) from user via the terminal.
   */
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
 
}