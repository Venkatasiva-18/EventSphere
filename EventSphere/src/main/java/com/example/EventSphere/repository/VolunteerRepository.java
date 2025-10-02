package com.example.EventSphere.repository;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;
import com.example.EventSphere.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    
    Optional<Volunteer> findByEventAndUser(Event event, User user);
    
    List<Volunteer> findByUser(User user);
    
    List<Volunteer> findByEvent(Event event);
    
    List<Volunteer> findByEventAndStatus(Event event, Volunteer.Status status);
    
    @Query("SELECT v FROM Volunteer v WHERE v.user = :user AND v.status = 'APPROVED'")
    List<Volunteer> findUserApprovedVolunteerRoles(@Param("user") User user);
    
    @Query("SELECT COUNT(v) FROM Volunteer v WHERE v.event = :event AND v.status = 'APPROVED'")
    long countApprovedVolunteers(@Param("event") Event event);
    
    @Query("SELECT COUNT(v) FROM Volunteer v WHERE v.event = :event AND v.status = 'PENDING'")
    long countPendingVolunteers(@Param("event") Event event);
    
    boolean existsByEventAndUser(Event event, User user);
}
