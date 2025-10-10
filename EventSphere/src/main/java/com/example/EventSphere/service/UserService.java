package com.example.EventSphere.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EventSphere.model.PasswordResetToken;
import com.example.EventSphere.model.RSVP;
import com.example.EventSphere.model.User;
import com.example.EventSphere.model.Volunteer;
import com.example.EventSphere.repository.PasswordResetTokenRepository;
import com.example.EventSphere.repository.RSVPRepository;
import com.example.EventSphere.repository.UserRepository;
import com.example.EventSphere.repository.VolunteerRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RSVPRepository rsvpRepository;
    private final VolunteerRepository volunteerRepository;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       RSVPRepository rsvpRepository, VolunteerRepository volunteerRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.rsvpRepository = rsvpRepository;
        this.volunteerRepository = volunteerRepository;
    }
    
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        System.out.println("User registered successfully: " + savedUser.getEmail() + " with ID: " + savedUser.getUserId());
        return savedUser;
    }
    
    @Transactional
    public User registerOrganizer(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ORGANIZER);
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        System.out.println("Organizer registered successfully: " + savedUser.getEmail() + " with ID: " + savedUser.getUserId());
        return savedUser;
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> searchUsers(String name, String email) {
        return userRepository.findByNameOrEmailContaining(name, email);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user has organized events
        if (user.getOrganizedEvents() != null && !user.getOrganizedEvents().isEmpty()) {
            throw new RuntimeException("Cannot delete user who has organized events. Please deactivate or reassign events first.");
        }
        
        // Delete user's RSVPs first
        List<RSVP> userRsvps = rsvpRepository.findByUser(user);
        if (!userRsvps.isEmpty()) {
            rsvpRepository.deleteAll(userRsvps);
        }
        
        // Delete user's volunteer registrations
        List<Volunteer> userVolunteers = volunteerRepository.findByUser(user);
        if (!userVolunteers.isEmpty()) {
            volunteerRepository.deleteAll(userVolunteers);
        }
        
        // Delete password reset tokens
        passwordResetTokenRepository.deleteByUser_UserId(userId);
        
        // Now delete the user
        userRepository.deleteById(userId);
    }
    
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }
    
    public void changeUserRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public PasswordResetToken createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        passwordResetTokenRepository.deleteByUser_UserId(user.getUserId());

        PasswordResetToken passwordResetToken =
            new PasswordResetToken(token, user, expiresAt);
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public Optional<PasswordResetToken> findToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }
}
