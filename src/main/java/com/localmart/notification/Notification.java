package com.localmart.notification;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "retailer_id")
    private Long retailerId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "title")
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType = NotificationType.system;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "sent_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    public void prePersist() {
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }

    public enum NotificationType {
        system,
        offer,
        order,
        security,
        reminder
    }
}
