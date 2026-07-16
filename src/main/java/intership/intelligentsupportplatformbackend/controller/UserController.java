package intership.intelligentsupportplatformbackend.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.dto.PageResponse;
import intership.intelligentsupportplatformbackend.dto.User.UserResponse;
import intership.intelligentsupportplatformbackend.dto.User.UserUpdateRequest;
import intership.intelligentsupportplatformbackend.model.Role;
import intership.intelligentsupportplatformbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user management operations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users with filters and pagination", description = "Admin only. Retrieve all users with optional filters and pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        PageResponse<UserResponse> response = userService.getAllUsers(name, email, role, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", response));
    }

    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        UserResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(new ApiResponse<>("User profile retrieved successfully", response));
    }

    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(new ApiResponse<>("User profile updated successfully", response));
    }

    @Operation(summary = "Delete user", description = "Admin only. Delete a user by ID")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
    }
}
