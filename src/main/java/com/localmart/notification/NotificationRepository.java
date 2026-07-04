package com.localmart.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerIdOrderBySentAtDesc(Long customerId);
    List<Notification> findByCustomerIdOrCustomerIdIsNullOrderBySentAtDesc(Long customerId);
    List<Notification> findByRetailerIdOrderBySentAtDesc(Long retailerId);
    List<Notification> findByRetailerIdOrRetailerIdIsNullOrderBySentAtDesc(Long retailerId);
}
