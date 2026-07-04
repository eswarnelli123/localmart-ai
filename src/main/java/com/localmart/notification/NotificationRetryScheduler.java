package com.localmart.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationDeliveryStatusRepository deliveryStatusRepository;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    private static final int MAX_ATTEMPTS = 3;

    // Run every minute
    @Scheduled(fixedDelayString = "PT1M")
    public void retryFailedDeliveries() {
        List<NotificationDeliveryStatus> failed = deliveryStatusRepository.findByStatusAndAttemptCountLessThan(NotificationDeliveryStatus.DeliveryStatus.failed, MAX_ATTEMPTS);
        if (failed == null || failed.isEmpty()) {
            return;
        }
        for (NotificationDeliveryStatus status : failed) {
            try {
                Notification notification = status.getNotification();
                if (notification == null) {
                    continue;
                }
                status.setStatus(NotificationDeliveryStatus.DeliveryStatus.in_progress);
                status.setLastAttemptedAt(LocalDateTime.now());
                deliveryStatusRepository.save(status);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(status.getRecipientEmail());
                mailMessage.setSubject("LocalMart Alert: " + notification.getTitle());
                mailMessage.setText(buildEmailContent(notification.getTitle(), notification.getMessage()));
                mailMessage.setFrom("projectofmca@gmail.com");
                mailMessage.setReplyTo("projectofmca@gmail.com");

                mailSender.send(mailMessage);

                status.setStatus(NotificationDeliveryStatus.DeliveryStatus.delivered);
                status.setSentAt(LocalDateTime.now());
                deliveryStatusRepository.save(status);
                log.info("Retry delivered to {} for notification {}", status.getRecipientEmail(), notification.getId());
            } catch (Exception e) {
                status.setAttemptCount((status.getAttemptCount() == null ? 0 : status.getAttemptCount()) + 1);
                status.setLastAttemptedAt(LocalDateTime.now());
                status.setErrorMessage(e.getMessage());
                status.setStatus(NotificationDeliveryStatus.DeliveryStatus.failed);
                deliveryStatusRepository.save(status);
                log.warn("Retry failed for {} (notification {}) attempt {}", status.getRecipientEmail(), status.getNotification() != null ? status.getNotification().getId() : null, status.getAttemptCount());
            }
        }
    }

    private String buildEmailContent(String title, String message) {
        return "Hello,\n\n" +
                "You have received a new notification from LocalMart:\n\n" +
                "Title: " + title + "\n" +
                "Message: " + message + "\n\n" +
                "Log in to your account to view more details.\n\n" +
                "Best regards,\n" +
                "LocalMart Team";
    }
}
