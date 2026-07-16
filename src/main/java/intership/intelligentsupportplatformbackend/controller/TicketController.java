package intership.intelligentsupportplatformbackend.controller;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import intership.intelligentsupportplatformbackend.dto.PageResponse;
import intership.intelligentsupportplatformbackend.dto.Ticket.TicketRequest;
import intership.intelligentsupportplatformbackend.dto.Ticket.TicketResponse;
import intership.intelligentsupportplatformbackend.model.TicketSource;
import intership.intelligentsupportplatformbackend.model.TicketStatus;
import intership.intelligentsupportplatformbackend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Management", description = "Endpoints for ticket management operations")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create ticket", description = "Create a new ticket with optional attachments")
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody TicketRequest request
    ) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Ticket created successfully", response));
    }

    @Operation(summary = "Get current user tickets", description = "Retrieve tickets for the currently authenticated user with filters and pagination")
    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> getCurrentUserTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketSource source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        PageResponse<TicketResponse> response = ticketService.getCurrentUserTickets(
                title, status, source, page, size, sortBy, sortDirection
        );
        return ResponseEntity.ok(new ApiResponse<>("Tickets retrieved successfully", response));
    }

    @Operation(summary = "Get all tickets", description = "Admin only. Retrieve all tickets with filters and pagination")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> getAllTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketSource source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        PageResponse<TicketResponse> response = ticketService.getAllTickets(
                title, status, source, page, size, sortBy, sortDirection
        );
        return ResponseEntity.ok(new ApiResponse<>("All tickets retrieved successfully", response));
    }

    @Operation(summary = "Get ticket by ID", description = "Retrieve a specific ticket by ID. Users can only access their own tickets, admins can access all.")
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable Long ticketId) {
        TicketResponse response = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(new ApiResponse<>("Ticket retrieved successfully", response));
    }

    @Operation(summary = "Update ticket", description = "Update a ticket. Users can only update their own tickets, admins can update all.")
    @PutMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketRequest request
    ) {
        TicketResponse response = ticketService.updateTicket(ticketId, request);
        return ResponseEntity.ok(new ApiResponse<>("Ticket updated successfully", response));
    }

    @Operation(summary = "Delete ticket", description = "Delete a ticket. Users can only delete their own tickets, admins can delete all.")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok(new ApiResponse<>("Ticket deleted successfully", null));
    }
}
