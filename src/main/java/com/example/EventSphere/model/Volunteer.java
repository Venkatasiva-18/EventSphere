package com.example.EventSphere.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String roleDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    
    @Column
    private LocalDateTime registrationDate = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public enum Status {
        PENDING, APPROVED, REJECTED
    }
    
    public Volunteer(Event event, User user, String roleDescription) {
        this.event = event;
        this.user = user;
        this.roleDescription = roleDescription;
        this.registrationDate = LocalDateTime.now();
        this.status = Status.PENDING;
    }
}
