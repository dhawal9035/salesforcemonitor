package com.az.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	// Utilized Method Overloading to send different email messages. This method is for notifying when the status of any of the instance is not proper
	public void sendMail(Map<String, List<String>> notificationMap) {
		SimpleMailMessage message = new SimpleMailMessage();
		for (Entry<String, List<String>> entry : notificationMap.entrySet()) {
			List<String> emailList = entry.getValue();
			String[] list = new String[emailList.size()];
			list = emailList.toArray(list);
			message.setBcc(list);
			message.setSubject("Notification for "+entry.getKey()+" Instance");
			message.setText("Hello,\n\n The status of the instance "+entry.getKey()+" is NOT OK. It is currently being worked upon by the salesforce "
					+ "team. We will notify you once the status changes." );
			javaMailSender.send(message);
		}
		

	}

	// This method is used to notify the users when the status is OK
	public void sendMail(String key, List<String> userList) {
		SimpleMailMessage message = new SimpleMailMessage();
			String[] list = new String[userList.size()];
			list = userList.toArray(list);
			message.setBcc(list);
			message.setSubject("Notification for "+key+" Instance");
			message.setText("Hello,\n\n The status of the instance "+key+" is OK now. We are constantly monitoring it and we will let you know "
					+ "if anything changes." );
			javaMailSender.send(message);
	}

	// This method is used to send successful registration method.
	public void sendMail(String email, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject(subject);
		message.setText(text);
		javaMailSender.send(message);
	}
	
}
