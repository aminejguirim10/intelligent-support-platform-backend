package intership.intelligentsupportplatformbackend.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.dto.Statistics.TicketStatisticsResponse;
import intership.intelligentsupportplatformbackend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Endpoints for ticket statistics and analytics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "Get ticket statistics", description = "Retrieve comprehensive ticket statistics including counts by status, source, category, priority, and time-based metrics")
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<TicketStatisticsResponse>> getTicketStatistics() {
        TicketStatisticsResponse response = statisticsService.getTicketStatistics();
        return ResponseEntity.ok(new ApiResponse<>("Statistics retrieved successfully", response));
    }
}
