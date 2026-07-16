package intership.intelligentsupportplatformbackend.dto.AIAnalysis;

import intership.intelligentsupportplatformbackend.model.TicketCategory;
import intership.intelligentsupportplatformbackend.model.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponse {

    private Long id;
    private TicketCategory category;
    private TicketPriority priority;
    private String sentiment;
    private String keywords;
    private Double confidenceScore;
    private LocalDateTime createdAt;
    private Long ticketId;
}
