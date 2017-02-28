package com.az.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.az.model.InstanceModel;
import com.az.model.NotificationModel;
import com.az.service.MainService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;



@RestController
@EnableScheduling
public class MainController {
	
	@Autowired
	MainService mainService;
	
	// This method is used to populate the Multiselect with all the instances 
	@RequestMapping(value="/getInstances",  method=RequestMethod.GET)
	public List<String> getInstanceDetails(){
		return mainService.getInstanceDetails();
	}
	
	// This method is used to save the user data along with the instances that theu have selected for monitoring
	@RequestMapping(value="/saveUserInfo",  method=RequestMethod.POST)
	public RedirectView saveUserInfo(RedirectAttributes attribute, @RequestParam (value="firstName") String firstName, @RequestParam (value="lastName") String lastName,
							 @RequestParam (value="email") String email, @RequestParam (value="instanceList") String[] selectedInstance, HttpServletResponse response){
		 mainService.saveUserInfo(firstName, lastName, email, selectedInstance);
		 attribute.addAttribute("Information Saved successfully", "success");
		 return new RedirectView("index.html");
		 
	}
	
	// This method saves the instance data when the index.html page loads thus keeping all the information up to date.
	@RequestMapping(value="/saveInstanceData", method = RequestMethod.POST)
	public void saveInstanceData(@RequestBody InstanceModel[] apiData){
		mainService.saveInstanceData(apiData);
	}
	
	// This is an automatic scheduled method that checks the status of the instances every 15 minutes and sends notification to users
	// if the status is Not OK or if the status changed back to OK
	@Scheduled(fixedRate=900000)
	public void checkStatus() throws JsonParseException, JsonMappingException, IOException {
 		mainService.checkStatus();
	}
	
	// This method is used to get the user data to show which instances have been subscribed
	@RequestMapping(value="/getUserData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<NotificationModel> getUserData(){
		return mainService.getUserData();
		
	}
}
