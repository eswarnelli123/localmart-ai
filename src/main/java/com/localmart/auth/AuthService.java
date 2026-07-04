package com.localmart.auth;

import com.localmart.admin.Admin;
import com.localmart.admin.AdminRepository;
import com.localmart.retailer.Retailer;
import com.localmart.retailer.RetailerRepository;
import com.localmart.security.JwtService;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RetailerRepository retailerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    public AuthResponse register(String name, String email, String phone, String password) {
        boolean emailExistsInRetailer = retailerRepository != null && Optional.ofNullable(retailerRepository.findByEmail(email)).orElse(Optional.empty()).isPresent();
        if (userRepository.findByEmail(email).isPresent() || emailExistsInRetailer) {
            return new AuthResponse(false, "Email already exists", null);
        }
        if (!isPhoneValid(phone)) {
            return new AuthResponse(false, "Please enter a valid mobile phone number.", null);
        }
        if (!isPasswordStrong(password)) {
            return new AuthResponse(false, "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.", null);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(false);
        userRepository.save(user);

        String otp = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUserType(OtpVerification.UserType.CUSTOMER);
        otpVerification.setUserId(user.getId());
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpVerification.setUsed(false);
        otpRepository.save(otpVerification);

        emailService.sendOtpEmail(email, otp);
        return new AuthResponse(true, "Please verify your email with the OTP sent to your inbox.", null);
    }

    public AuthResponse verifyOtp(String email, String otp) {
        Optional<OtpVerification> verification = otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        if (verification.isEmpty()) {
            return new AuthResponse(false, "No OTP found", null);
        }

        OtpVerification otpVerification = verification.get();
        if (!otpVerification.getOtp().equals(otp) || otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new AuthResponse(false, "Invalid or expired OTP", null);
        }

        otpVerification.setUsed(true);
        otpRepository.save(otpVerification);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);
            return new AuthResponse(true, "Email verified successfully", null);
        }

        if (retailerRepository != null) {
            Optional<Retailer> optionalRetailer = retailerRepository.findByEmail(email);
            if (optionalRetailer.isPresent()) {
                Retailer retailer = optionalRetailer.get();
                retailer.setVerified(true);
                retailerRepository.save(retailer);
                return new AuthResponse(true, "Retailer verified successfully", null);
            }
        }

        return new AuthResponse(false, "User not found", null);
    }

    public AuthResponse login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return new AuthResponse(false, "Invalid email or password", null);
            }
            if (!user.isVerified()) {
                return new AuthResponse(false, "Please verify your email first", null);
            }
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(true, "Login successful", token);
        }

        if (retailerRepository != null) {
            Optional<Retailer> optionalRetailer = retailerRepository.findByEmail(email);
            if (optionalRetailer.isPresent()) {
                Retailer retailer = optionalRetailer.get();
                if (!passwordEncoder.matches(password, retailer.getPassword())) {
                    return new AuthResponse(false, "Invalid email or password", null);
                }
                if (!retailer.isVerified()) {
                    return new AuthResponse(false, "Please verify your email first", null);
                }
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(retailer.getEmail())
                        .password(retailer.getPassword())
                        .roles("RETAILER")
                        .build();
                String token = jwtService.generateToken(userDetails);
                return new AuthResponse(true, "Login successful", token);
            }
        }

        if (adminRepository != null) {
            Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
            if (optionalAdmin.isPresent()) {
                Admin admin = optionalAdmin.get();
                if (!admin.isActive()) {
                    return new AuthResponse(false, "Admin account is inactive", null);
                }
                if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
                    return new AuthResponse(false, "Invalid email or password", null);
                }
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(admin.getEmail())
                        .password(admin.getPasswordHash())
                        .roles("ADMIN")
                        .build();
                String token = jwtService.generateToken(userDetails);
                return new AuthResponse(true, "Login successful", token);
            }
        }

        return new AuthResponse(false, "Invalid email or password", null);
    }

    public AuthResponse forgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        Optional<Retailer> optionalRetailer = retailerRepository != null ? retailerRepository.findByEmail(email) : Optional.empty();

        if (optionalUser.isEmpty() && optionalRetailer.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        String otp = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            otpVerification.setUserType(OtpVerification.UserType.CUSTOMER);
            otpVerification.setUserId(user.getId());
        } else {
            Retailer retailer = optionalRetailer.get();
            otpVerification.setUserType(OtpVerification.UserType.RETAILER);
            otpVerification.setUserId(retailer.getId());
        }
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpVerification.setUsed(false);
        otpRepository.save(otpVerification);

        emailService.sendPasswordResetEmail(email, otp);
        return new AuthResponse(true, "Password reset OTP sent to your email", null);
    }

    public AuthResponse resetPassword(String email, String otp, String newPassword) {
        Optional<OtpVerification> verification = otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        if (verification.isEmpty()) {
            return new AuthResponse(false, "No reset OTP found", null);
        }

        OtpVerification otpVerification = verification.get();
        if (!otpVerification.getOtp().equals(otp) || otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new AuthResponse(false, "Invalid or expired OTP", null);
        }

        otpVerification.setUsed(true);
        otpRepository.save(otpVerification);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return new AuthResponse(true, "Password reset successfully", null);
        }

        if (retailerRepository != null) {
            Optional<Retailer> optionalRetailer = retailerRepository.findByEmail(email);
            if (optionalRetailer.isPresent()) {
                Retailer retailer = optionalRetailer.get();
                retailer.setPassword(passwordEncoder.encode(newPassword));
                retailerRepository.save(retailer);
                return new AuthResponse(true, "Password reset successfully", null);
            }
        }

        return new AuthResponse(false, "User not found", null);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{10,15}$");
    }

    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()\\[\\]{};:'\",.<>/?\\\\|`~+=_-].*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
