package com.az.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.az.model.InstanceModel;
import com.az.model.NotificationModel;
import com.az.model.UserModel;
import com.az.service.EmailService;

@Repository
public class MainRepository {
	@Autowired
	EmailService emailService;
	
	private static final Logger logger = Logger.getLogger(MainRepository.class);
	
	protected final JdbcTemplate jdbc;
	public String query;
	public MainRepository(JdbcTemplate jdbc){
		this.jdbc = jdbc;
	}

	public List<String> getInstanceDetails() {
		List<String> instanceList = new ArrayList<String>();
		query = "select instance_id from instance_info";
		SqlRowSet rs = jdbc.queryForRowSet(query);
		while(rs.next()){
			instanceList.add(rs.getString("instance_id"));
		}
		logger.info("Instance details retrieved and sent back to controller");
		return instanceList;
	}

	public void saveUserInfo(String firstName, String lastName, String email, String[] selectedInstance) {
		final String uuid = UUID.randomUUID().toString();
		query = "insert into user_info(user_id, first_name, last_name, email) values(?,?,?,?)";
		jdbc.update(query,new Object[]{uuid,firstName, lastName, email});
		query = "insert into notification_info (user_id, instance_id) values (?, ?)";
		for(String instance:selectedInstance){
			jdbc.update(query, new Object[]{uuid, instance});
		}
		
		logger.info("User Registered");
		String text = "Hello " +firstName +",\n\n Your information has succesfully been saved along with the instances you have selected for monitoring."
				+ " We will notify you if there is any change in status of your selected Instances.";
		
		emailService.sendMail(email, "Regsitration", text);
	}

	public void saveInstanceData(InstanceModel[] apiData) {
		query = "insert into instance_info (instance_id, location, environment, release_version, status, active) values (?,?,?,?,?,?) on duplicate key update location = ?, environment = ?, release_version = ?, status = ?, active = ? ";
		for(int i=0;i<apiData.length;i++){
			jdbc.update(query, new Object[]{apiData[i].key,apiData[i].location,apiData[i].environment,apiData[i].releaseVersion,apiData[i].status,apiData[i].isActive(),apiData[i].location,apiData[i].environment,apiData[i].releaseVersion,apiData[i].status,apiData[i].isActive()});
		}
	}

	public Map<String,List<String>> getUsers(Map<String, String> instanceMap) {
		List<String> userList = new ArrayList<String>();
		Map<String, List<String>> notificationMap = new HashMap<String, List<String>>();
		query = "insert into status_info (instance_id, status) values (? , ?) on duplicate key update status = ?";
		for (Map.Entry<String, String> entry : instanceMap.entrySet()) {
		    jdbc.update(query, new Object[] {entry.getKey(), entry.getValue(), entry.getValue()});
		}
		
		query = "select email from user_info UI,notification_info NI where UI.user_id = NI.user_id and NI.instance_id = ?";
		for(String key:instanceMap.keySet()){
			SqlRowSet rs = jdbc.queryForRowSet(query, new Object[] {key});
			while(rs.next()){
				userList.add(rs.getString("email"));
			}
			notificationMap.put(key, userList);
		}
		
		return notificationMap;
	}

	public List<String> getInstanceListForStatusNotOk() {
		List<String> instanceList = new ArrayList<String>();
		query = "select instance_id from status_info";
		SqlRowSet rs = jdbc.queryForRowSet(query);
		while(rs.next()){
			instanceList.add(rs.getString("instance_id"));
		}
		return instanceList;
	}

	public List<String> changeStatus(String key) {
		List<String> userList = new ArrayList<String>();
		query = "delete from status_info where instance_id=?";
		jdbc.update(query, new Object[]{key});
		query = "select email from user_info UI,notification_info NI where UI.user_id = NI.user_id and NI.instance_id = ?";
		SqlRowSet rs = jdbc.queryForRowSet(query, new Object[] {key});
		while(rs.next()){
			userList.add(rs.getString("email"));
		}
		return userList;
	}

	public List<NotificationModel> getUserData() {
		List<NotificationModel> notificationList = new ArrayList<NotificationModel>();
		UserModel userModel = new UserModel();
		NotificationModel notificationModel = new NotificationModel();
		query = "select first_name, last_name,email, group_concat(instance_id separator ',') as instance from user_info UI, notification_info NI where UI.user_id = NI.user_id group by email";
		SqlRowSet rs = jdbc.queryForRowSet(query);
		while(rs.next()){
			userModel.setFirstName(rs.getString("first_name"));
			userModel.setLastName(rs.getString("last_name"));
			userModel.setEmail(rs.getString("email"));
			notificationModel.setUser(userModel);
			String instances = rs.getString("instance");
			notificationModel.setInstances(instances);
			notificationList.add(notificationModel);
			notificationModel = new NotificationModel();
			userModel = new UserModel();
		}
		return notificationList;
	}

}
