package com.seblong.wp.entities.mongo;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.seblong.wp.enums.UserGender;

@Document(collection = "users")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4748515838737616006L;

	@Id
	protected ObjectId id;

	private String name;

	private String avatar;

	private long birth;

	private UserGender gender;

	private String region;

	private int height;

	private int weight;

	@Field(value = "login_id")
	private String loginId;

	private String phone;

	@Indexed
	private long uId;

	public User() {
	}

	@PersistenceConstructor
	public User(String name, String avatar, Long birth, UserGender gender, String region, Long uId, Integer height, Integer weight, @Value("#root.login_id")String loginId, String phone) {
		this.name = name;
		this.avatar = avatar;
		if (birth != null){
			this.birth = birth;
		}
		this.gender = gender;
		this.region = region;
		if (uId != null){
			this.uId = uId;
		}
		if(height == null){
			this.height = 0;
		}else {
			this.height = height;
		}
		if(weight == null){
			this.weight = 0;
		}else {
			this.weight = weight;
		}
		this.loginId = loginId;
		this.phone = phone;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public long getBirth() {
		return birth;
	}

	public void setBirth(long birth) {
		this.birth = birth;
	}

	public UserGender getGender() {
		return gender;
	}

	public void setGender(UserGender gender) {
		this.gender = gender;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
        return weight;
    }

	public void setWeight(int weight) {
        this.weight = weight;
    }

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
