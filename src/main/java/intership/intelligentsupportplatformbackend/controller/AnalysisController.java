package intership.intelligentsupportplatformbackend.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisRequest;
import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisResponse;
import intership.intelligentsupportplatformbackend.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets/{ticketId}/analyses")
@RequiredArgsConstructor
@Tag(name = "AI Analysis Management", description = "Endpoints for AI analysis operations on tickets")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "Create analysis", description = "Create a new AI analysis for a specific ticket")
    @PostMapping
    public ResponseEntity<ApiResponse<AnalysisResponse>> createAnalysis(
            @PathVariable Long ticketId,
            @Valid @RequestBody AnalysisRequest request
    ) {
        AnalysisResponse response = analysisService.createAnalysis(ticketId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Analysis created successfully", response));
    }

    @Operation(summary = "Update analysis", description = "Update an existing AI analysis for a specific ticket")
    @PutMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<AnalysisResponse>> updateAnalysis(
            @PathVariable Long ticketId,
            @PathVariable Long analysisId,
            @Valid @RequestBody AnalysisRequest request
    ) {
        AnalysisResponse response = analysisService.updateAnalysis(ticketId, analysisId, request);
        return ResponseEntity.ok(new ApiResponse<>("Analysis updated successfully", response));
    }

    @Operation(summary = "Delete analysis", description = "Delete an AI analysis for a specific ticket")
    @DeleteMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(
            @PathVariable Long ticketId,
            @PathVariable Long analysisId
    ) {
        analysisService.deleteAnalysis(ticketId, analysisId);
        return ResponseEntity.ok(new ApiResponse<>("Analysis deleted successfully", null));
    }
}
