package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.User;
import com.payrolltaxpro.repository.UserRepository;
import com.payrolltaxpro.security.CustomUserDetailsService;
import com.payrolltaxpro.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public Map<String, Object> authenticate(String username, String password) {
        log.info("Authentication attempt for user: {}", username);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", user.getTenant() != null ? user.getTenant().getId() : null);
        claims.put("tenantName", user.getTenant() != null ? user.getTenant().getName() : null);

        String accessToken = jwtService.generateToken(claims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("tenantId", user.getTenant() != null ? user.getTenant().getId() : null);
        response.put("tenantName", user.getTenant() != null ? user.getTenant().getName() : null);
        response.put("roles", user.getRoles());

        log.info("User authenticated successfully: {}", username);
        return response;
    }
}
