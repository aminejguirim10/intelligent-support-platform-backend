package intership.intelligentsupportplatformbackend.dto.Attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse {

    private Long id;
    private String filename;
    private String mediaType;
    private String bucketName;
    private String storagePath;
    private String publicUrl;
    private LocalDateTime createdAt;
    private Long ticketId;
}
