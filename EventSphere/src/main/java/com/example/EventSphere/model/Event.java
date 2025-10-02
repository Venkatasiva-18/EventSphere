package com.example.EventSphere.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private LocalDateTime dateTime;
    
    @Column
    private LocalDateTime endDateTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Column
    private Integer maxParticipants;
    
    @Column
    private boolean requiresApproval = false;
    
    @Column
    private boolean isActive = true;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RSVP> rsvps;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Volunteer> volunteers;
    
    public enum Category {
        WORKSHOP, HACKATHON, DONATION_DRIVE, MEETUP, CONFERENCE, SEMINAR, OTHER
    }
    
    public int getCurrentParticipants() {
        return rsvps != null ? (int) rsvps.stream().filter(rsvp -> rsvp.getStatus() == RSVP.Status.GOING).count() : 0;
    }
    
    public boolean isFull() {
        return maxParticipants != null && getCurrentParticipants() >= maxParticipants;
    }
    
    public boolean isUpcoming() {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
