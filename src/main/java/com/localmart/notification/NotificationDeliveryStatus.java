package com.localmart.notification;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_delivery_status")
@Data
public class NotificationDeliveryStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus status = DeliveryStatus.pending;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "last_attempted_at")
    private LocalDateTime lastAttemptedAt;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum DeliveryStatus {
        pending,
        in_progress,
        delivered,
        failed,
        bounced
    }
}
