package com.example.EventSphere.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.EventSphere.model.Event;
import com.example.EventSphere.model.User;

@Service
public class EmailService {
    
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendEventNotification(Event event, List<User> users, String subject, String message) {
        for (User user : users) {
            sendEmail(user.getEmail(), subject, message);
        }
    }
    
    public void sendEventReminder(Event event, User user) {
        String subject = "Event Reminder: " + event.getTitle();
        String message = String.format(
            "Hello %s,\n\n" +
            "This is a reminder about the upcoming event:\n\n" +
            "Event: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Description: %s\n\n" +
            "We look forward to seeing you there!\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            event.getLocation(),
            event.getDescription()
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendRSVPConfirmation(Event event, User user) {
        String subject = "RSVP Confirmation for: " + event.getTitle();
        String message = String.format(
            "Hello %s,\n\n" +
            "Thank you for RSVPing to the event:\n\n" +
            "Event: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "We're excited to see you there!\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            event.getLocation()
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendVolunteerConfirmation(Event event, User user) {
        String subject = "Volunteer Registration Confirmation for: " + event.getTitle();
        String message = String.format(
            "Hello %s,\n\n" +
            "Thank you for volunteering for the event:\n\n" +
            "Event: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Your volunteer registration is pending approval. You will be notified once it's approved.\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            event.getLocation()
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendVolunteerApproval(Event event, User user) {
        String subject = "Volunteer Application Approved for: " + event.getTitle();
        String message = String.format(
            "Hello %s,\n\n" +
            "Great news! Your volunteer application for the following event has been approved:\n\n" +
            "Event: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Thank you for your willingness to help make this event a success!\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            event.getLocation()
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendNewEventNotification(Event event, List<User> users) {
        String subject = "New Event Available: " + event.getTitle();
        String message = String.format(
            "Hello,\n\n" +
            "A new event has been posted on EventSphere:\n\n" +
            "Event: %s\n" +
            "Category: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Location: %s\n\n" +
            "Description: %s\n\n" +
            "Visit EventSphere to RSVP or volunteer!\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            event.getTitle(),
            event.getCategory().toString().replace("_", " "),
            event.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            event.getLocation(),
            event.getDescription()
        );
        
        sendEventNotification(event, users, subject, message);
    }
    
    public void sendPasswordResetEmail(User user, String resetLink) {
        String subject = "Password Reset Request";
        String message = String.format(
            "Hello %s,\n\n" +
            "We received a request to reset your password. Please click the link below to set a new password.\n\n" +
            "%s\n\n" +
            "This link is valid for 10 minutes. If you did not request a password reset, you can safely ignore this email.\n\n" +
            "Best regards,\n" +
            "EventSphere Team",
            user.getName(),
            resetLink
        );

        sendEmail(user.getEmail(), subject, message);
    }

    private void sendEmail(String to, String subject, String message) {
        try {
            if (mailSender != null) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(to);
                mailMessage.setSubject(subject);
                mailMessage.setText(message);
                mailMessage.setFrom("noreply@eventsphere.com");
                
                mailSender.send(mailMessage);
                System.out.println("Email sent successfully to: " + to);
            } else {
                System.out.println("Email service not configured. Would send email to " + to + 
                                 " with subject: " + subject + " and message: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}
