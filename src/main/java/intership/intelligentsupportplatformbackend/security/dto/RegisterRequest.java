package intership.intelligentsupportplatformbackend.security.dto;

import intership.intelligentsupportplatformbackend.model.Role;
import intership.intelligentsupportplatformbackend.util.ValidEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for user registration requests.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{8}$",
            message = "Phone number must contain exactly 8 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Role is required")
    @ValidEnum(enumClass = Role.class, message = "Invalid role")
    private String role;

}
