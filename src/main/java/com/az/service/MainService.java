package com.az.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.az.model.InstanceModel;
import com.az.model.NotificationModel;
import com.az.repository.MainRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MainService {
	@Autowired
	MainRepository mainRepository;
	
	@Autowired
	EmailService emailService;

	public List<String> getInstanceDetails() {
		return mainRepository.getInstanceDetails();
	}

	public void saveUserInfo(String firstName, String lastName, String email, String[] selectedInstance) {
		mainRepository.saveUserInfo(firstName, lastName, email, selectedInstance);
	}

	public void saveInstanceData(InstanceModel[] apiData) {
		mainRepository.saveInstanceData(apiData);
		
	}

	public void checkStatus() throws JsonParseException, JsonMappingException, IOException {
		
		//Uncoment the below two lines for real time monitoring
		
		 /*RestTemplate restTemplate = new RestTemplate();
		 InstanceModel[] instances = restTemplate.getForObject("https://api.status.salesforce.com/v1/instances", InstanceModel[].class);*/
		
		//This is for testing purpose. Comment the below to lines for real time monitoring
		 ObjectMapper mapper = new ObjectMapper();
		 InstanceModel[] instances = (InstanceModel[]) mapper.readValue(new File("C:/Users/Dhawal/git/salesforcemonitor/src/main/resources/data.json"), InstanceModel[].class);
		 
		 Map<String, String> instanceMap = new HashMap<String, String>();
		 Map<String,List<String>> notificationMap = new HashMap<String,List<String>>();
		 List<String> instanceList = mainRepository.getInstanceListForStatusNotOk();
		 
		 for(InstanceModel instance:instances){
			 if(instanceList.contains(instance.key) && instance.status.equals("OK")){
				 List<String> userList = mainRepository.changeStatus(instance.key);
				 emailService.sendMail(instance.key, userList);
			 } else if(!instance.getStatus().equals("OK")) {
				 instanceMap.put(instance.key, instance.status);
			 }
		 }
		 if(instanceMap.size() > 0) {
			 notificationMap = mainRepository.getUsers(instanceMap);
			 emailService.sendMail(notificationMap);
		 }
		
	}

	public List<NotificationModel> getUserData() {
		return mainRepository.getUserData();
	}
	
}
