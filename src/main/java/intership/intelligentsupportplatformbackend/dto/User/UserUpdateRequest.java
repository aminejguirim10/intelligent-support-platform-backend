package intership.intelligentsupportplatformbackend.dto.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(
            regexp = "^[0-9]{8}$",
            message = "Phone number must contain exactly 8 digits"
    )
    private String phoneNumber;

    private String profilePhotoUrl;
}
