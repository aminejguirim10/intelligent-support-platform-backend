package intership.intelligentsupportplatformbackend.security.service;

import intership.intelligentsupportplatformbackend.model.Role;
import intership.intelligentsupportplatformbackend.model.User;
import intership.intelligentsupportplatformbackend.repository.UserRepository;
import intership.intelligentsupportplatformbackend.security.config.JwtService;
import intership.intelligentsupportplatformbackend.security.dto.AuthenticationRequest;
import intership.intelligentsupportplatformbackend.security.dto.AuthenticationResponse;
import intership.intelligentsupportplatformbackend.security.dto.ForgotPasswordRequest;
import intership.intelligentsupportplatformbackend.security.dto.RegisterRequest;
import intership.intelligentsupportplatformbackend.security.dto.ResetPasswordRequest;
import intership.intelligentsupportplatformbackend.security.exception.InvalidPasswordResetTokenException;
import intership.intelligentsupportplatformbackend.security.exception.PasswordResetPasswordMismatchException;
import intership.intelligentsupportplatformbackend.security.exception.PasswordResetTokenExpiredException;
import intership.intelligentsupportplatformbackend.security.exception.PasswordResetUserNotFoundException;
import intership.intelligentsupportplatformbackend.security.service.email.EmailTemplate;
import intership.intelligentsupportplatformbackend.security.service.email.TemplateEmailService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/*
 * Service class handling authentication operations.
 * Provides user registration, login, and token refresh functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetEmailService passwordResetEmailService;
    private final TemplateEmailService templateEmailService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final java.security.SecureRandom random = new java.security.SecureRandom();

    @Value("${application.security.password-reset.frontend-url}")
    private String passwordResetFrontendUrl;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    /*
     * Register a new user with USER role.
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        return registerWithRole(request, Role.valueOf(request.getRole()));
    }

    /*
     * Register a new admin user with ADMIN role.
     * This should be protected or used only for initial admin setup.
     */
    @Transactional
    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        return registerWithRole(request, Role.ADMIN);
    }

    /*
     * Internal method to register a user with a specific role.
     */
    private AuthenticationResponse registerWithRole(RegisterRequest request, Role role) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Use the password provided in the request instead of generating a random one
        // TODO: Change it to later to the generated password
        String password = request.getPassword();
        
        User user = buildUserByRole(request, role, password);

        userRepository.save(user);

        // Send welcome email without including the password for security
        sendWelcomeEmail(user);

        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthenticationResponse(user, accessToken, refreshToken);
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> variables = Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "frontendUrl", frontendUrl
            );

            templateEmailService.sendHtmlEmail(
                    user.getEmail(),
                    EmailTemplate.WELCOME,
                    variables,
                    "Welcome to AuditManagement. Your account has been created successfully."
            );
            log.info("Welcome email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending welcome email", e);
        }
    }

    /*
     * Authenticate a user and return tokens.
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Authenticate using Spring Security's AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Fetch user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthenticationResponse(user, accessToken, refreshToken);
    }

    /*
     * Refresh the access token using a valid refresh token.
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse refreshToken(String refreshToken) {
        // Extract username from refresh token
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        // Generate new access token
        String newAccessToken = generateAccessToken(user);

        return buildAuthenticationResponse(user, newAccessToken, refreshToken);
    }

    private String generateAccessToken(User user) {
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        return jwtService.generateToken(claims, user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail() == null ? null : request.getEmail().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetUserNotFoundException("No user found with email: " + email));

        String tokenValue = jwtService.generatePasswordResetToken(user);

        String resetLink = buildResetLink(tokenValue);
        passwordResetEmailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new PasswordResetPasswordMismatchException("The new password and confirmation do not match");
        }

        User user = validatePasswordResetTokenAndGetUser(request.getToken());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /*
     * Build the authentication response DTO.
     */
    private AuthenticationResponse buildAuthenticationResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration() / 1000) // Convert to seconds
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private User validatePasswordResetTokenAndGetUser(String token) {
        String tokenValue = token == null ? null : token.trim();

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new InvalidPasswordResetTokenException("Invalid password reset token");
        }

        final String email;
        try {
            email = jwtService.extractUsername(tokenValue);
        } catch (ExpiredJwtException ex) {
            throw new PasswordResetTokenExpiredException("The password reset token has expired");
        } catch (Exception ex) {
            throw new InvalidPasswordResetTokenException("Invalid password reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetUserNotFoundException("No user found with email: " + email));

        try {
            if (!jwtService.isPasswordResetTokenValid(tokenValue, user)) {
                throw new InvalidPasswordResetTokenException("Invalid password reset token");
            }
        } catch (ExpiredJwtException ex) {
            throw new PasswordResetTokenExpiredException("The password reset token has expired");
        } catch (InvalidPasswordResetTokenException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidPasswordResetTokenException("Invalid password reset token");
        }

        return user;
    }

    private String buildResetLink(String token) {
        String separator = passwordResetFrontendUrl.contains("?") ? "&" : "?";
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return passwordResetFrontendUrl + separator + "token=" + encodedToken;
    }

    private User buildUserByRole(RegisterRequest request, Role role, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(role)
                .phoneNumber(request.getPhoneNumber())
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}