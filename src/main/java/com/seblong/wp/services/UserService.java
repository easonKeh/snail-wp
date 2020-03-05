package com.seblong.wp.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.seblong.wp.entities.mongo.User;

public interface UserService {

	User getBriefUser(String userId);
	
	List<User> getBriefUsers(Set<String> userIds);
	
	String getUserAvatar(String userId);
	
	List<User> getUserAvatars(Collection<String> userIds);
	
	List<User> getUserAvatarAndName(Collection<String> userIds);
	
	String getUserName(String userId);
	
	int getIndex(List<User> users, String userId);

}
