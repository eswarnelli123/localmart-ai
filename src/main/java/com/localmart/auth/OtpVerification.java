package com.localmart.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otp")
@Data
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;

    @Convert(converter = UserTypeConverter.class)
    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "otp_code")
    private String otp;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_used")
    private boolean used;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum UserType {
        CUSTOMER,
        RETAILER
    }
}
