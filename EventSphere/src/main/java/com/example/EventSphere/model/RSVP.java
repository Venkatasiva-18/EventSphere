package com.example.EventSphere.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rsvps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rsvpId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column
    private LocalDateTime rsvpDate = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public enum Status {
        GOING, INTERESTED, NOT_GOING
    }
    
    public RSVP(Event event, User user, Status status) {
        this.event = event;
        this.user = user;
        this.status = status;
        this.rsvpDate = LocalDateTime.now();
    }
}
