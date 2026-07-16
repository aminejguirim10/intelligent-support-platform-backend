package intership.intelligentsupportplatformbackend.dto.Ticket;

import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisResponse;
import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentResponse;
import intership.intelligentsupportplatformbackend.model.TicketSource;
import intership.intelligentsupportplatformbackend.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketSource source;
    private LocalDateTime createdAt;
    private Long userId;
    private String userEmail;
    private String userName;
    private List<AttachmentResponse> attachments;
    private List<AnalysisResponse> analyses;
}
