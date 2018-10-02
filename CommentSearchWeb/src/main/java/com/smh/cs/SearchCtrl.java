package com.smh.cs;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smh.cs.model.CommentInfo;
import com.smh.cs.model.CommentInfoDT;
import com.smh.cs.model.VideoInfo;
import com.smh.cs.model.VideoInfoDT;
import com.smh.cs.service.SearchService;

@Controller
public class SearchCtrl {
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	private static final Logger logger = LoggerFactory.getLogger(SearchCtrl.class);
	
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
	public @ResponseBody VideoInfoDT searchVideo(@RequestParam("keyword") String keyword, Locale locale, Model model) throws IOException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
//		List<VideoInfo> videoInfo = searchSvc.searchVideo(keyword, "search", null, null);
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "tag");
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideo", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideo(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, 
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("live") String live,
			Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
//		if( "true".equals(learning) ) {
//			searchSvc.searchVideo(keyword, "csearch", startDate, endDate);
//		} else if( "true".equals(live) ) {
//			searchSvc.searchVideo(keyword, "live", startDate, endDate);
//		}

		List<VideoInfo> videolist = service.selectVideoInfo();
		
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "comment");
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/searchPop", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT searchPop(Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		searchSvc.searchPop();
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideoNoComment", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideoNoComment(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		if( "true".equals(learning) ) {
			searchSvc.searchVideo(keyword, "csearch", null, null);
		}
		
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "noComment");
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideoLive", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideoLive(@RequestParam("keyword") String keyword, @RequestParam("learning") String learning, Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		if( "true".equals(learning) ) {
			searchSvc.searchVideo(keyword, "live", null, null);
		}
		
		List<VideoInfo> videoInfo = searchSvc.csearchVideo(keyword, "comment");
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
}
