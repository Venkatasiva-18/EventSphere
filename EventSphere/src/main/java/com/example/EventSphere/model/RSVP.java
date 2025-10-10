package com.example.EventSphere.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rsvps")
@Getter
@Setter
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
    
    // For group/team events
    @Column(name = "team_name")
    private String teamName;
    
    @Column(name = "team_size")
    private Integer teamSize;
    
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
