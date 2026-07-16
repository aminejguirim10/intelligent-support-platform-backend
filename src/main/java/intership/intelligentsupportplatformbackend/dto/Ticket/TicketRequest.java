package intership.intelligentsupportplatformbackend.dto.Ticket;

import intership.intelligentsupportplatformbackend.model.TicketSource;
import intership.intelligentsupportplatformbackend.model.TicketStatus;
import intership.intelligentsupportplatformbackend.util.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotBlank(message = "Source is required")
    @ValidEnum(enumClass = TicketSource.class, message = "Invalid source")
    private String source;

    @ValidEnum(enumClass = TicketStatus.class, message = "Invalid status")
    private String status;

    private List<Long> attachmentIds;
}
