package intership.intelligentsupportplatformbackend.dto.User;

import intership.intelligentsupportplatformbackend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePhotoUrl;
    private Role role;
    private LocalDateTime createdAt;
}
