package intership.intelligentsupportplatformbackend.dto.Attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequest {

    @NotBlank(message = "Filename is required")
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    @NotBlank(message = "Media type is required")
    @Size(max = 120, message = "Media type must not exceed 120 characters")
    private String mediaType;

    @NotBlank(message = "Bucket name is required")
    @Size(max = 120, message = "Bucket name must not exceed 120 characters")
    private String bucketName;

    @NotBlank(message = "Storage path is required")
    @Size(max = 700, message = "Storage path must not exceed 700 characters")
    private String storagePath;

    @NotBlank(message = "Public URL is required")
    private String publicUrl;
}
