package com.example.EventSphere.repository;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByOrganizer(User organizer);
    
    List<Event> findByCategory(Event.Category category);
    
    List<Event> findByIsActiveTrue();
    
    List<Event> findByIsActiveTrueAndDateTimeAfter(LocalDateTime dateTime);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.dateTime > :now ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.dateTime BETWEEN :start AND :end ORDER BY e.dateTime ASC")
    List<Event> findEventsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchEvents(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.location LIKE %:location%")
    List<Event> findByLocationContaining(@Param("location") String location);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.requiresApproval = true")
    List<Event> findEventsRequiringApproval();
}
