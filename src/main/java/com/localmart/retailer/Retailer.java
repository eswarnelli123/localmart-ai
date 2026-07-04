package com.localmart.retailer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "retailer")
@Data
public class Retailer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retailer_id")
    private Long id;

    @NotBlank
    @Column(name = "company_name")
    private String companyName;

    @NotBlank
    @Column(name = "contact_name")
    private String contactName;

    @NotBlank
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "password_hash")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "business_license")
    private String businessLicense;

    @Column(name = "verified")
    private boolean verified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.pending;

    public enum Status {
        pending,
        approved,
        rejected,
        inactive
    }
}
