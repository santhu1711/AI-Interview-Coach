package com.aiinterviewcoach.service.auth;

import com.aiinterviewcoach.dto.request.LoginRequest;
import com.aiinterviewcoach.dto.request.RegisterRequest;
import com.aiinterviewcoach.dto.response.AuthResponse;
import com.aiinterviewcoach.dto.response.UserResponse;
import com.aiinterviewcoach.entity.User;
import com.aiinterviewcoach.exception.DuplicateEmailException;
import com.aiinterviewcoach.exception.InvalidCredentialsException;
import com.aiinterviewcoach.exception.PasswordMismatchException;
import com.aiinterviewcoach.exception.ResourceNotFoundException;
import com.aiinterviewcoach.repository.UserRepository;
import com.aiinterviewcoach.security.AuthenticatedUser;
import com.aiinterviewcoach.security.JwtService;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordMismatchException();
        }
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }

        User user = new User(
                request.fullName().trim(), email, passwordEncoder.encode(request.password()));
        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
        log.info("Registered user id={}", user.getId());
        return authenticatedResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
            AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
            User user = userRepository.findById(principal.getId())
                    .orElseThrow(InvalidCredentialsException::new);
            log.info("Login succeeded for user id={}", user.getId());
            return authenticatedResponse(user);
        } catch (AuthenticationException exception) {
            log.warn("Login failed");
            throw new InvalidCredentialsException();
        }
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(AuthenticatedUser principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists."));
        return toResponse(user);
    }

    private AuthResponse authenticatedResponse(User user) {
        String token = jwtService.generateToken(AuthenticatedUser.from(user));
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), toResponse(user));
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
