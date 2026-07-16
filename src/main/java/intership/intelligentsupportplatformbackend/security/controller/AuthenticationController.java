package intership.intelligentsupportplatformbackend.security.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.security.dto.AuthenticationRequest;
import intership.intelligentsupportplatformbackend.security.dto.AuthenticationResponse;
import intership.intelligentsupportplatformbackend.security.dto.ForgotPasswordRequest;
import intership.intelligentsupportplatformbackend.security.dto.RegisterRequest;
import intership.intelligentsupportplatformbackend.security.dto.ResetPasswordRequest;
import intership.intelligentsupportplatformbackend.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/*
 * REST Controller for authentication operations.
 * Provides endpoints for user registration, login, and token refresh.
 * All endpoints are publicly accessible (whitelisted in SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, token refresh, and password reset")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /*
     * Register a new user.
     */
    @Operation(summary = "Register a user", description = "Creates a new user with the role provided in the request and returns the generated JWT tokens.")
    @PostMapping("/register")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Uncomment this later
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User registered successfully", response));
    }

    /*
     * Register a new admin user.
     * Note: In production, this should be protected or removed.
     */
    @Operation(summary = "Register an administrator", description = "Creates an admin account and returns the generated JWT tokens. Endpoint reserved for initial administration.")
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> registerAdmin(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.registerAdmin(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Admin registered successfully", response));
    }

    /*
     * Authenticate user and return JWT tokens.
     */
    @Operation(summary = "Login", description = "Authenticates a user with their email and password, then returns an access token and a refresh token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", response));
    }

    /*
     * Refresh access token using refresh token.
     */
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access token, without modifying the refresh token.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken
    ) {
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>("Token refreshed successfully", response));
    }

    /*
     * Request a password reset.
     */
    @Operation(summary = "Request password reset", description = "Generates a reset token and sends a secure link to the user's email address.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse<>("Password reset link sent successfully", null));
    }

    /*
     * Reset the password.
     */
    @Operation(summary = "Reset password", description = "Verifies the reset token and updates the password after validating the confirmation.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse<>("Password reset successfully", null));
    }
}