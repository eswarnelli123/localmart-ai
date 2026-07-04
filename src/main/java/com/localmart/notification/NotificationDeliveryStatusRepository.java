package com.localmart.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDeliveryStatusRepository extends JpaRepository<NotificationDeliveryStatus, Long> {
    List<NotificationDeliveryStatus> findByNotificationId(Long notificationId);
    
    Optional<NotificationDeliveryStatus> findByNotificationIdAndRecipientEmail(Long notificationId, String recipientEmail);
    
    List<NotificationDeliveryStatus> findByStatus(NotificationDeliveryStatus.DeliveryStatus status);
    
    List<NotificationDeliveryStatus> findByStatusAndAttemptCountLessThan(
            NotificationDeliveryStatus.DeliveryStatus status, Integer maxAttempts);
}
