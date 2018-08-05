package com.smh.cs;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	
	@RequestMapping(value = "/searchVideo", method = RequestMethod.GET)
	public String searchVideo(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		String keyword = "뉴스";
		
		searchSvc.searchVideo(keyword);
		
		return "home";
	}
}
