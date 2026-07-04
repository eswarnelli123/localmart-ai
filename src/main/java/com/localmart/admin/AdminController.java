package com.localmart.admin;

import com.localmart.category.Category;
import com.localmart.category.CategoryRepository;
import com.localmart.coupon.Coupon;
import com.localmart.coupon.CouponRepository;
import com.localmart.offer.Offer;
import com.localmart.offer.OfferRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductRepository;
import com.localmart.report.Report;
import com.localmart.report.ReportRepository;
import com.localmart.retailer.Retailer;
import com.localmart.retailer.RetailerRepository;
import com.localmart.shop.Shop;
import com.localmart.shop.ShopRepository;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import com.localmart.notification.Notification;
import com.localmart.notification.NotificationRepository;
import com.localmart.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RetailerRepository retailerRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ReportRepository reportRepository;
    private final CouponRepository couponRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/users")
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/retailers")
    public List<Retailer> listRetailers() {
        return retailerRepository.findAll();
    }

    @PostMapping("/retailers/{id}/approve")
    public ResponseEntity<Retailer> approveRetailer(@PathVariable Long id) {
        return retailerRepository.findById(id)
                .map(retailer -> {
                    retailer.setVerified(true);
                    retailer.setStatus(Retailer.Status.approved);
                    return ResponseEntity.ok(retailerRepository.save(retailer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/retailers/{id}/reject")
    public ResponseEntity<Retailer> rejectRetailer(@PathVariable Long id) {
        return retailerRepository.findById(id)
                .map(retailer -> {
                    retailer.setVerified(false);
                    retailer.setStatus(Retailer.Status.rejected);
                    return ResponseEntity.ok(retailerRepository.save(retailer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stores")
    public List<Shop> listStores() {
        return shopRepository.findAll();
    }

    @GetMapping("/products")
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/categories")
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/offers")
    public List<Offer> listOffers() {
        return offerRepository.findAll();
    }

    @GetMapping("/reports")
    public List<Report> listReports() {
        return reportRepository.findAll();
    }

    @GetMapping("/analytics")
    public Map<String, Object> analyticsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalUsers", userRepository.count());
        summary.put("totalRetailers", retailerRepository.count());
        summary.put("totalStores", shopRepository.count());
        summary.put("totalProducts", productRepository.count());
        summary.put("totalOffers", offerRepository.count());
        summary.put("totalReports", reportRepository.count());
        summary.put("totalCoupons", couponRepository.count());
        return summary;
    }

    @PostMapping("/notifications")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody AdminNotificationRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Notification payload is required."));
        }

        String normalizedTargetType = request.targetType() == null ? "all" : request.targetType().trim().toLowerCase();
        String title = request.title() == null ? "" : request.title().trim();
        String message = request.message() == null ? "" : request.message().trim();

        if (title.isBlank() || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Title and message are required."));
        }

        Notification notification;
        switch (normalizedTargetType) {
            case "customer" -> notification = notificationService.sendCustomerNotification(request.targetId(), title, message);
            case "retailer" -> notification = notificationService.sendRetailerNotification(request.targetId(), title, message);
            default -> notification = notificationService.sendBroadcastNotification(title, message);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "notification sent and queued for delivery");
        response.put("notificationId", notification.getId());
        response.put("targetType", normalizedTargetType);
        response.put("targetId", request.targetId());
        response.put("deliveryStatus", "Email delivery initiated");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reports/{id}/status")
    public ResponseEntity<Map<String, Object>> updateReportStatus(@PathVariable Long id,
                                                                  @RequestBody Map<String, Object> payload) {
        Optional<Report> optionalReport = reportRepository.findById(id);
        if (optionalReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Report report = optionalReport.get();
        String statusValue = payload.getOrDefault("status", "").toString().trim();
        try {
            report.setStatus(Report.Status.valueOf(statusValue));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Unsupported report status."));
        }

        report.setUpdatedAt(java.time.LocalDateTime.now());
        reportRepository.save(report);

        Map<String, Object> response = new HashMap<>();
        response.put("id", report.getId());
        response.put("status", report.getStatus().name());
        response.put("message", "Report status updated.");
        return ResponseEntity.ok(response);
    }
}

record AdminNotificationRequest(String targetType, Long targetId, String title, String message) {
}
