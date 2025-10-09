package com.example.EventSphere.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
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
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column
    private LocalDateTime endDateTime;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Column
    private Integer maxParticipants;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "participation_type")
    private ParticipationType participationType = ParticipationType.INDIVIDUAL;
    
    @Column(name = "group_size")
    private Integer groupSize;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;
    
    @Column(name = "is_active")
    private Boolean active = true;
    
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
        if (rsvps == null) return 0;
        
        // For GROUP events, count teams (RSVPs), not individual participants
        if (participationType == ParticipationType.GROUP) {
            return (int) rsvps.stream().filter(rsvp -> rsvp.getStatus() == RSVP.Status.GOING).count();
        }
        
        // For INDIVIDUAL events, count individual participants
        return (int) rsvps.stream().filter(rsvp -> rsvp.getStatus() == RSVP.Status.GOING).count();
    }
    
    public boolean isFull() {
        return maxParticipants != null && getCurrentParticipants() >= maxParticipants;
    }
    
    public String getParticipationLabel() {
        return participationType == ParticipationType.GROUP ? "Teams" : "Participants";
    }
    
    public boolean isUpcoming() {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
