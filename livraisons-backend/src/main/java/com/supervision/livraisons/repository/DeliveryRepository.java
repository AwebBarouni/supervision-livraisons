package com.supervision.livraisons.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.supervision.livraisons.model.Delivery;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {

    List<Delivery> findAllByOrderByScheduledTimeAsc();

    List<Delivery> findByAssignedLivreurIdOrderByScheduledTimeAsc(String assignedLivreurId);

    List<Delivery> findByAssignedLivreurIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(
            String assignedLivreurId,
            Date start,
            Date end
    );

    @Query("{ '$or': [ { 'clientName': { '$regex': ?0, '$options': 'i' } }, { 'clientPhone': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Delivery> findByClientNameOrClientPhoneLikeIgnoreCase(String query);
}
