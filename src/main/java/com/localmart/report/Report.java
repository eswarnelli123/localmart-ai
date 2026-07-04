package com.localmart.report;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reporter_type", nullable = false)
    private ReporterType reporterType;

    @Column(name = "reporter_id")
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.open;

    @Column(name = "assigned_to_admin_id")
    private Long assignedToAdminId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ReporterType {
        customer,
        retailer,
        admin,
        anonymous
    }

    public enum ReportType {
        product_issue,
        order_issue,
        fraud,
        feedback,
        technical,
        other
    }

    public enum Status {
        open,
        in_review,
        resolved,
        closed
    }
}
