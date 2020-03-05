package com.seblong.wp.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seblong.wp.entities.mongo.User;
import com.seblong.wp.repositories.mongodb.UserRepository;
import com.seblong.wp.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public User getBriefUser(String userId) {
		try {
			ObjectId uId = new ObjectId(userId);
			User user = userRepo.findBriefUserById(uId);
			if(user == null){
				return null;
			}
			return user;
		} catch (IllegalArgumentException | NoSuchElementException e) {
			return null;
		}

	}

	@Override
	public List<User> getBriefUsers(Set<String> userIds) {
		List<ObjectId> oIds = new ArrayList<ObjectId>();
		for (String uId : userIds) {
			if (ObjectId.isValid(uId))
				oIds.add(new ObjectId(uId));
		}
		List<User> users = userRepo.findBriefUsersByIds(oIds);
		return users;
	}

	@Override
	public String getUserAvatar(String userId) {

		try {
			ObjectId uId = new ObjectId(userId);
			User user = userRepo.findAvatarById(uId);
			if(user != null)
				return user.getAvatar();
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	@Override
	public List<User> getUserAvatars(Collection<String> userIds) {
		
		if( userIds != null && !userIds.isEmpty() ){
			List<ObjectId> oIds = new ArrayList<ObjectId>();
			for (String uId : userIds) {
				if (ObjectId.isValid(uId))
					oIds.add(new ObjectId(uId));
			}
			List<User> users = userRepo.findAvatarsById(oIds);
			return users;
		}
		
		return null;
	}
	

	@Override
	public List<User> getUserAvatarAndName(Collection<String> userIds) {
		if( userIds != null && !userIds.isEmpty() ){
			List<ObjectId> oIds = new ArrayList<ObjectId>();
			for (String uId : userIds) {
				if (ObjectId.isValid(uId))
					oIds.add(new ObjectId(uId));
			}
			List<User> users = userRepo.findAvatarAndNameById(oIds);
			return users;
		}
		
		return null;
	}

	@Override
	public String getUserName(String userId) {
		try {
			ObjectId uId = new ObjectId(userId);
			User user = userRepo.findNameById(uId);
			if(user != null)
				return user.getName();
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	@Override
	public int getIndex(List<User> users, String userId) {
		int index = binarySearchUser(users, userId);
		if(index < 0){
			index = -1;
		}
		return index;
	}

	private int binarySearchUser(List<User> users, String userId){

		int index = -1;
		if( ObjectId.isValid(userId) ){
			User user = new User();
			user.setId(new ObjectId(userId));
			index = Collections.binarySearch(users, user, new Comparator<User>() {
				@Override
				public int compare(User o1, User o2) {
					return o1.getId().compareTo(o2.getId());
				}
			});
		}
		return index;
		
	}

}
