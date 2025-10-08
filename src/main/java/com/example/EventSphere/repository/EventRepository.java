package com.example.EventSphere.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByOrganizer(User organizer);
    
    List<Event> findByCategory(Event.Category category);
    
    List<Event> findByActiveTrue();
    
    List<Event> findByActiveTrueAndDateTimeAfter(LocalDateTime dateTime);
    
    @Query("SELECT e FROM Event e WHERE e.active = true AND e.dateTime > :now ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);
    
    @Query("SELECT e FROM Event e WHERE e.active = true AND e.dateTime BETWEEN :start AND :end ORDER BY e.dateTime ASC")
    List<Event> findEventsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM Event e WHERE e.active = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchEvents(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Event e WHERE e.active = true AND LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Event> findByLocationContaining(@Param("location") String location);
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.organizer WHERE e.eventId = :id")
    Optional<Event> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps")
    List<Event> findAllWithDetails();

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps WHERE e.active = true")
    List<Event> findAllActiveWithDetails();
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps WHERE e.active = true AND e.dateTime > :now ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEventsWithDetails(@Param("now") LocalDateTime now);
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps WHERE e.active = true AND e.category = :category")
    List<Event> findByCategoryWithDetails(@Param("category") Event.Category category);
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps WHERE e.active = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchEventsWithDetails(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.rsvps WHERE e.active = true AND LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Event> findByLocationContainingWithDetails(@Param("location") String location);

    @Query("SELECT e FROM Event e WHERE e.endDateTime IS NOT NULL AND e.endDateTime < :cutoff")
    List<Event> findCompletedEventsBefore(@Param("cutoff") LocalDateTime cutoff);
}
