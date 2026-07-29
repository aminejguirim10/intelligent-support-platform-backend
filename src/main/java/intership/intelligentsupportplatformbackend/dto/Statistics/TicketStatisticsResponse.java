package intership.intelligentsupportplatformbackend.dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsResponse {
    
    // Overall counts
    private Long totalTickets;
    private Long openTickets;
    private Long closedTickets;
    
    // Time-based counts
    private Long ticketsThisWeek;
    private Long ticketsThisMonth;
    private Long closedThisWeek;
    private Long closedThisMonth;
    
    // Tickets by status
    private Map<String, Long> ticketsByStatus;
    
    // Tickets by source
    private Map<String, Long> ticketsBySource;
    
    // Tickets by category (from AI analysis)
    private Map<String, Long> ticketsByCategory;
    
    // Tickets by priority (from AI analysis)
    private Map<String, Long> ticketsByPriority;

    // Tickets created per day (last 30 days)
    private Map<String, Long> ticketsCreatedPerDay;

    // Tickets closed per day (last 30 days)
    private Map<String, Long> ticketsClosedPerDay;
}
