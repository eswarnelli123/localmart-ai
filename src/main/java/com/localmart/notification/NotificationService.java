package com.localmart.notification;

import com.localmart.coupon.Coupon;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import com.localmart.retailer.Retailer;
import com.localmart.retailer.RetailerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RetailerRepository retailerRepository;
    private final java.util.Optional<JavaMailSender> mailSender;
    private final NotificationDeliveryStatusRepository deliveryStatusRepository;

    /**
     * Send notification to specific customer or all customers
     */
    public Notification sendCustomerNotification(Long customerId, String title, String message) {
        Notification notification = new Notification();
        notification.setCustomerId(customerId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(Notification.NotificationType.offer);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        if (customerId != null) {
            Optional<User> user = userRepository.findById(customerId);
            if (user.isPresent()) {
                String recipientEmail = user.get().getEmail();
                if (recipientEmail != null && !recipientEmail.isBlank()) {
                    sendEmailNotificationAsync(saved.getId(), recipientEmail, title, message);
                    log.info("Notification created for customer {} and email delivery queued", customerId);
                } else {
                    log.warn("Customer {} has no email address; notification saved but delivery skipped", customerId);
                }
            } else {
                log.warn("Customer {} not found; notification saved but delivery skipped", customerId);
            }
        }

        log.info("Customer notification {} created and queued for delivery", saved.getId());
        return saved;
    }

    /**
     * Send notification to specific retailer or all retailers
     */
    public Notification sendRetailerNotification(Long retailerId, String title, String message) {
        Notification notification = new Notification();
        notification.setRetailerId(retailerId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(Notification.NotificationType.offer);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        if (retailerId != null) {
            Optional<Retailer> retailer = retailerRepository.findById(retailerId);
            if (retailer.isPresent()) {
                String recipientEmail = retailer.get().getEmail();
                if (recipientEmail != null && !recipientEmail.isBlank()) {
                    sendEmailNotificationAsync(saved.getId(), recipientEmail, title, message);
                    log.info("Notification created for retailer {} and email delivery queued", retailerId);
                } else {
                    log.warn("Retailer {} has no email address; notification saved but delivery skipped", retailerId);
                }
            } else {
                log.warn("Retailer {} not found; notification saved but delivery skipped", retailerId);
            }
        }

        log.info("Retailer notification {} created and queued for delivery", saved.getId());
        return saved;
    }

    /**
     * Send broadcast notification to all users
     */
    public Notification sendBroadcastNotification(String title, String message) {
        Notification notification = new Notification();
        // Both customerId and retailerId are null for broadcast
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(Notification.NotificationType.system);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                sendEmailNotificationAsync(saved.getId(), user.getEmail(), title, message);
            }
        }

        List<Retailer> allRetailers = retailerRepository.findAll();
        for (Retailer retailer : allRetailers) {
            if (retailer.getEmail() != null && !retailer.getEmail().isBlank()) {
                sendEmailNotificationAsync(saved.getId(), retailer.getEmail(), title, message);
            }
        }

        log.info("Broadcast notification {} sent to {} users and {} retailers", 
                 saved.getId(), allUsers.size(), allRetailers.size());
        return saved;
    }

    public void sendCouponCreatedNotification(Coupon coupon) {
        if (coupon == null) {
            return;
        }

        String title = "New coupon available";
        String message = buildCouponMessage(coupon);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user == null || user.getId() == null) {
                continue;
            }
            Notification notification = new Notification();
            notification.setCustomerId(user.getId());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setNotificationType(Notification.NotificationType.offer);
            notification.setSentAt(LocalDateTime.now());
            Notification saved = notificationRepository.save(notification);
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                sendEmailNotificationAsync(saved.getId(), user.getEmail(), title, message);
            }
        }

        List<Retailer> retailers = retailerRepository.findAll();
        for (Retailer retailer : retailers) {
            if (retailer == null || retailer.getId() == null) {
                continue;
            }
            Notification notification = new Notification();
            notification.setRetailerId(retailer.getId());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setNotificationType(Notification.NotificationType.offer);
            notification.setSentAt(LocalDateTime.now());
            Notification saved = notificationRepository.save(notification);
            if (retailer.getEmail() != null && !retailer.getEmail().isBlank()) {
                sendEmailNotificationAsync(saved.getId(), retailer.getEmail(), title, message);
            }
        }
    }

    private String buildCouponMessage(Coupon coupon) {
        StringBuilder message = new StringBuilder();
        message.append("A new coupon is now available: ").append(coupon.getCode());
        if (coupon.getDiscountType() != null) {
            switch (coupon.getDiscountType()) {
                case percentage -> message.append(" for ").append(coupon.getDiscountValue()).append("% off");
                case fixed_amount -> message.append(" for ₹").append(coupon.getDiscountValue()).append(" off");
                case free_shipping -> message.append(" for free shipping");
            }
        }
        if (coupon.getValidUntil() != null) {
            message.append(" until ").append(coupon.getValidUntil().toLocalDate());
        }
        return message.toString();
    }

    /**
     * Send email notification
     */
    @Async("taskExecutor")
    public void sendEmailNotificationAsync(Long notificationId, String email, String title, String message) {
        NotificationDeliveryStatus status = new NotificationDeliveryStatus();
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        status.setNotification(notification);
        status.setRecipientEmail(email);
        status.setStatus(NotificationDeliveryStatus.DeliveryStatus.in_progress);
        status.setAttemptCount(0);
        status.setLastAttemptedAt(LocalDateTime.now());
        deliveryStatusRepository.save(status);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("LocalMart Alert: " + title);
            mailMessage.setText(buildEmailContent(title, message));
            mailMessage.setFrom("projectofmca@gmail.com");
            mailMessage.setReplyTo("projectofmca@gmail.com");

            mailSender.ifPresent(s -> s.send(mailMessage));

            status.setStatus(NotificationDeliveryStatus.DeliveryStatus.delivered);
            status.setSentAt(LocalDateTime.now());
            status.setLastAttemptedAt(LocalDateTime.now());
            deliveryStatusRepository.save(status);

            log.info("Email delivered to {} for notification {}", email, notificationId);
        } catch (Exception e) {
            status.setStatus(NotificationDeliveryStatus.DeliveryStatus.failed);
            status.setErrorMessage(e.getMessage());
            status.setLastAttemptedAt(LocalDateTime.now());
            status.setAttemptCount((status.getAttemptCount() == null ? 0 : status.getAttemptCount()) + 1);
            deliveryStatusRepository.save(status);

            log.error("Failed to deliver email to {} for notification {}: {}", email, notificationId, e.getMessage());
        }
    }

    /**
     * Build email content
     */
    private String buildEmailContent(String title, String message) {
        return "Hello,\n\n" +
                "You have received a new notification from LocalMart:\n\n" +
                "Title: " + title + "\n" +
                "Message: " + message + "\n\n" +
                "Log in to your account to view more details.\n\n" +
                "Best regards,\n" +
                "LocalMart Team";
    }

    /**
     * Get notifications for customer
     */
    public List<Notification> getCustomerNotifications(Long customerId) {
        return notificationRepository.findByCustomerIdOrCustomerIdIsNullOrderBySentAtDesc(customerId);
    }

    /**
     * Get notifications for retailer
     */
    public List<Notification> getRetailerNotifications(Long retailerId) {
        return notificationRepository.findByRetailerIdOrRetailerIdIsNullOrderBySentAtDesc(retailerId);
    }

    /**
     * Mark notification as read
     */
    public Notification markAsRead(Long notificationId) {
        Optional<Notification> optional = notificationRepository.findById(notificationId);
        if (optional.isPresent()) {
            Notification notification = optional.get();
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        return null;
    }

    /**
     * Get delivery status
     */
    public List<NotificationDeliveryStatus> getDeliveryStatus(Long notificationId) {
        return deliveryStatusRepository.findByNotificationId(notificationId);
    }

}
