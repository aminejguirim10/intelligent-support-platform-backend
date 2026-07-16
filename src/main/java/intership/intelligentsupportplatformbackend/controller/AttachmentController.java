package intership.intelligentsupportplatformbackend.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentRequest;
import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentResponse;
import intership.intelligentsupportplatformbackend.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment Management", description = "Endpoints for attachment management operations")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "Create attachment", description = "Create a new attachment before linking it to a ticket")
    @PostMapping
    public ResponseEntity<ApiResponse<AttachmentResponse>> createAttachment(
            @Valid @RequestBody AttachmentRequest request
    ) {
        AttachmentResponse response = attachmentService.createAttachment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Attachment created successfully", response));
    }

    @Operation(summary = "Get attachment by ID", description = "Retrieve a specific attachment by its ID")
    @GetMapping("/{attachmentId}")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachmentById(@PathVariable Long attachmentId) {
        AttachmentResponse response = attachmentService.getAttachmentById(attachmentId);
        return ResponseEntity.ok(new ApiResponse<>("Attachment retrieved successfully", response));
    }

    @Operation(summary = "Delete attachment", description = "Delete an attachment only if it's not linked to a ticket")
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(new ApiResponse<>("Attachment deleted successfully", null));
    }
}
