package com.localmart.admin;

import com.localmart.category.CategoryRepository;
import com.localmart.coupon.CouponRepository;
import com.localmart.notification.NotificationRepository;
import com.localmart.offer.OfferRepository;
import com.localmart.product.ProductRepository;
import com.localmart.report.Report;
import com.localmart.report.ReportRepository;
import com.localmart.retailer.RetailerRepository;
import com.localmart.shop.ShopRepository;
import com.localmart.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private UserRepository userRepository;
    @Mock private RetailerRepository retailerRepository;
    @Mock private ShopRepository shopRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private OfferRepository offerRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private AdminController adminController;

    @Test
    void updateReportStatusShouldPersistAndReturnUpdatedStatus() {
        Report report = new Report();
        report.setId(7L);
        report.setStatus(Report.Status.open);

        when(reportRepository.findById(7L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Map<String, Object>> response = adminController.updateReportStatus(7L, Map.of("status", "in_review"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "in_review");
        assertThat(report.getStatus()).isEqualTo(Report.Status.in_review);
    }
}
