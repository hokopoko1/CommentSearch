package com.smh.cs;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

@Controller
public class SearchCtrl {
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	private static final Logger logger = LoggerFactory.getLogger(SearchCtrl.class);
	
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
	
	@RequestMapping(value = "/searchVideo", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT searchVideo(@RequestParam("keyword") String keyword, Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		List<VideoInfo> videoInfo = searchSvc.searchVideo(keyword, "search");
		rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/csearchVideo", method = RequestMethod.POST)
	public @ResponseBody VideoInfoDT csearchVideo(@RequestParam("keyword") String keyword, Locale locale, Model model) throws IOException, ParseException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		VideoInfoDT rtn = new VideoInfoDT();
		
		//List<VideoInfo> videoInfo = searchSvc.searchVideo(keyword, "csearch");
		
		searchSvc.csearchVideo(keyword);
		
		//rtn.setData(videoInfo);
		
		return rtn;
	}
	
	@RequestMapping(value = "/getComment", method = RequestMethod.POST)
	public @ResponseBody CommentInfoDT getComment(@RequestParam("videoId") String videoId, Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		CommentInfoDT rtn = new CommentInfoDT();
		
		List<CommentInfo> commentInfo =  searchSvc.getComment(videoId, null);
		rtn.setData(commentInfo);
		
		return rtn;
	}
	
}
