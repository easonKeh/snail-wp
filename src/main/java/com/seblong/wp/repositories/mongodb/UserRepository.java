package com.seblong.wp.repositories.mongodb;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.seblong.wp.entities.mongo.User;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId>{
	
	@Query(value = "{ '_id' : ?0 }", fields = "{ 'name' : 1, 'avatar' : 1, 'birth' : 1, 'gender' : 1, 'region'  : 1, 'uId' : 1 , 'height' : 1 , 'weight' : 1, 'login_id' : 1}")
	User findBriefUserById(ObjectId id);
	
	@Query(value = "{ '_id' : { $in : ?0 } }", fields = "{ 'name' : 1, 'avatar' : 1, 'birth' : 1, 'gender' : 1, 'region'  : 1, 'uId' : 1}")
	List<User> findBriefUsersByIds(Collection<ObjectId> ids);
	
	@Query(value = "{ '_id' : ?0 }", fields = "{ 'avatar' : 1  }")
	User findAvatarById(ObjectId id);
	
	@Query(value = "{ '_id' : { $in : ?0 }  }", fields = "{ 'avatar' : 1  }")
	List<User> findAvatarsById(Collection<ObjectId> ids);

	@Query(value = "{ '_id' : { $in : ?0 } }", fields = "{ 'name' : 1, 'avatar' : 1, 'gender' : 1 }")
	List<User> findAvatarAndNameById(Collection<ObjectId> ids);
	
	@Query(value = "{ '_id' : ?0 }", fields = "{ 'name' : 1 }")
	User findNameById(ObjectId id);

	@Query(value = "{ 'phone' : ?0 }", fields = "{ 'name' : 1, 'avatar' : 1, 'birth' : 1, 'gender' : 1, 'region'  : 1, 'uId' : 1 , 'height' : 1 , 'weight' : 1}")
    List<User> findBriefUserByPhone(String keyword);

	@Query(value = "{ 'uId' : ?0 }", fields = "{ 'name' : 1, 'avatar' : 1, 'birth' : 1, 'gender' : 1, 'region'  : 1, 'uId' : 1 , 'height' : 1 , 'weight' : 1}")
	List<User> findBriefUserByUid(Long keyword);
}
