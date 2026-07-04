package com.localmart.auth;

import com.localmart.retailer.RetailerRepository;
import com.localmart.security.JwtService;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RetailerRepository retailerRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateUnverifiedUserAndSendOtp() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(retailerRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register("Test User", "user@example.com", "+911234567890", "Password123!");

        assertTrue(response.isSuccess());
        assertEquals("Please verify your email with the OTP sent to your inbox.", response.getMessage());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendOtpEmail(eq("user@example.com"), anyString());
    }
}
