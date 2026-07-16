package intership.intelligentsupportplatformbackend.dto.AIAnalysis;

import intership.intelligentsupportplatformbackend.model.TicketCategory;
import intership.intelligentsupportplatformbackend.model.TicketPriority;
import intership.intelligentsupportplatformbackend.util.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRequest {

    @NotBlank(message = "Category is required")
    @ValidEnum(enumClass = TicketCategory.class,message = "Invalid category")
    private String category;

    @NotBlank(message = "Priority is required")
    @ValidEnum(enumClass = TicketPriority.class,message = "Invalid priority")
    private String priority;

    @NotBlank(message = "Sentiment is required")
    @Size(max = 50, message = "Sentiment must not exceed 50 characters")
    private String sentiment;

    @NotBlank(message = "Keywords are required")
    @Size(max = 1000, message = "Keywords must not exceed 1000 characters")
    private String keywords;

    @NotNull(message = "Confidence Score is required")
    private Double confidenceScore;
}
