package com.example.EventSphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.RSVP;
import com.example.EventSphere.model.User;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, Long> {
    
    Optional<RSVP> findByEventAndUser(Event event, User user);
    
    @Query("SELECT r FROM RSVP r WHERE r.event.eventId = :eventId AND r.user.userId = :userId")
    Optional<RSVP> findByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
    
    List<RSVP> findByUser(User user);
    
    List<RSVP> findByEvent(Event event);
    
    @Query("SELECT r FROM RSVP r JOIN FETCH r.user WHERE r.event = :event")
    List<RSVP> findByEventWithUser(@Param("event") Event event);
    
    List<RSVP> findByEventAndStatus(Event event, RSVP.Status status);
    
    @Query("SELECT r FROM RSVP r WHERE r.user = :user AND r.status = 'GOING'")
    List<RSVP> findUserGoingEvents(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM RSVP r WHERE r.event = :event AND r.status = 'GOING'")
    long countGoingParticipants(@Param("event") Event event);
    
    @Query("SELECT COUNT(r) FROM RSVP r WHERE r.event = :event AND r.status = 'INTERESTED'")
    long countInterestedParticipants(@Param("event") Event event);
    
    boolean existsByEventAndUser(Event event, User user);
}
