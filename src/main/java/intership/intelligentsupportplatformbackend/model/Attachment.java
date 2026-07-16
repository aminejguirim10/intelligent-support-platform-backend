package intership.intelligentsupportplatformbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", length = 255)
    private String filename;

    @Column(name = "media_type", length = 120)
    private String mediaType;

    @Column(name = "bucket_name", length = 120)
    private String bucketName;

    @Column(name = "storage_path", length = 700, nullable = false)
    private String storagePath;

    @Column(name = "public_url", columnDefinition = "TEXT", nullable = false)
    private String publicUrl;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="ticket_id")
    private Ticket ticket;
}
