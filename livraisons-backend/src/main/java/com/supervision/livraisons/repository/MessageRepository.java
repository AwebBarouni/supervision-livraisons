package com.supervision.livraisons.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.supervision.livraisons.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{ '$or': [ {'senderId': ?0}, {'receiverId': ?0} ] }")
    List<Message> findDirectMessagesForUser(String userId, Sort sort);

    List<Message> findByReceiverIdIsNullOrderByTimestampDesc();

    List<Message> findBySenderIdAndReceiverIdIsNullOrderByTimestampAsc(String senderId);

    @Query("{ '$or': [ { '$and': [ {'senderId': ?0}, {'receiverId': ?1} ] }, { '$and': [ {'senderId': ?1}, {'receiverId': ?0} ] } ] }")
    List<Message> findThreadMessages(String userA, String userB, Sort sort);

    @Query("{ 'isEmergency': true, '$or': [ { 'receiverId': null }, { 'receiverId': ?0 }, { 'senderId': ?0 } ] }")
    List<Message> findEmergencyMessagesForUser(String userId);
}
