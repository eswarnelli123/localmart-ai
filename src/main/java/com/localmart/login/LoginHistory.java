package com.localmart.login;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Data
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_time")
    private LocalDateTime loginTime = LocalDateTime.now();

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false)
    private LoginStatus loginStatus = LoginStatus.success;

    @Column(name = "failure_reason")
    private String failureReason;

    public enum UserType {
        customer,
        retailer,
        admin
    }

    public enum LoginStatus {
        success,
        failure
    }
}
