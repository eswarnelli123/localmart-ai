package com.localmart.security;

import com.localmart.retailer.Retailer;
import com.localmart.admin.Admin;
import com.localmart.admin.AdminRepository;
import com.localmart.retailer.Retailer;
import com.localmart.retailer.RetailerRepository;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RetailerRepository retailerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }

        Optional<Retailer> optionalRetailer = retailerRepository.findByEmail(username);
        if (optionalRetailer.isPresent()) {
            Retailer retailer = optionalRetailer.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(retailer.getEmail())
                    .password(retailer.getPassword())
                    .roles("RETAILER")
                    .build();
        }

        Optional<Admin> optionalAdmin = adminRepository.findByEmail(username);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPasswordHash())
                    .roles("ADMIN")
                    .build();
        }

        throw new UsernameNotFoundException("User not found");
    }
}
