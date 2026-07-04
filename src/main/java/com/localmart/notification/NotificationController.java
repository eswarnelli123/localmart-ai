package com.localmart.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    /**
     * Get notifications for customer or retailer
     */
    @GetMapping
    public List<Notification> getNotifications(@RequestParam(required = false) Long customerId,
                                               @RequestParam(required = false) Long retailerId) {
        if (customerId != null) {
            return notificationService.getCustomerNotifications(customerId);
        }
        if (retailerId != null) {
            return notificationService.getRetailerNotifications(retailerId);
        }
        return notificationRepository.findAll();
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/mark-read")
    public ResponseEntity<Notification> markAsRead(@RequestBody MarkReadRequest request) {
        Notification notification = notificationService.markAsRead(request.notificationId());
        if (notification != null) {
            return ResponseEntity.ok(notification);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get delivery status of notification
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getNotificationStatus(@PathVariable Long id) {
        List<NotificationDeliveryStatus> statuses = notificationService.getDeliveryStatus(id);
        if (statuses != null && !statuses.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "notificationId", id,
                    "deliveries", statuses
            ));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get all unread notifications count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam(required = false) Long customerId,
                                                               @RequestParam(required = false) Long retailerId) {
        List<Notification> notifications = customerId != null 
            ? notificationService.getCustomerNotifications(customerId)
            : retailerId != null 
            ? notificationService.getRetailerNotifications(retailerId)
            : notificationRepository.findAll();
        
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount, "totalCount", notifications.size()));
    }
}

record MarkReadRequest(Long notificationId) {
}
