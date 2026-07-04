package com.localmart.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName = "";

    @NotBlank
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "phone")
    private String phone;

    @NotBlank
    @Column(name = "password_hash")
    private String password;

    @Column(name = "is_verified")
    private boolean verified = false;

    @Transient
    private Role role = Role.USER;

    public String getName() {
        return firstName + (lastName != null && !lastName.isBlank() ? " " + lastName : "");
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.firstName = null;
            this.lastName = "";
            return;
        }
        String[] parts = name.trim().split("\\s+", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
    }

    public enum Role {
        USER,
        RETAILER,
        ADMIN
    }
}
