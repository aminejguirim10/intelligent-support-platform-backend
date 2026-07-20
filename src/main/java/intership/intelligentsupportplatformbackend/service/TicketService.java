package intership.intelligentsupportplatformbackend.service;

import intership.intelligentsupportplatformbackend.dto.*;
import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisResponse;
import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentResponse;
import intership.intelligentsupportplatformbackend.dto.Ticket.TicketRequest;
import intership.intelligentsupportplatformbackend.dto.Ticket.TicketResponse;
import intership.intelligentsupportplatformbackend.exception.TicketNotFoundException;
import intership.intelligentsupportplatformbackend.exception.UnauthorizedAccessException;
import intership.intelligentsupportplatformbackend.exception.UserNotFoundException;
import intership.intelligentsupportplatformbackend.model.*;
import intership.intelligentsupportplatformbackend.repository.AttachmentRepository;
import intership.intelligentsupportplatformbackend.repository.TicketRepository;
import intership.intelligentsupportplatformbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TicketStatus.OPEN)
                .source(request.getSource() != null ? TicketSource.valueOf(request.getSource().toUpperCase()) : null)
                .createdAt(java.time.LocalDateTime.now())
                .user(user)
                .build();

        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepository.findByIdInAndTicketIsNull(request.getAttachmentIds());
            if (attachments.size() != request.getAttachmentIds().size()) {
                throw new IllegalArgumentException("Some attachments not found or already linked to a ticket");
            }
            attachments.forEach(a -> a.setTicket(ticket));
            ticket.setAttachments(attachments);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    public PageResponse<TicketResponse> getCurrentUserTickets(
            String title,
            TicketStatus status,
            TicketSource source,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ticket> ticketsPage = ticketRepository.findByFilters(title, status, source, user.getId(), pageable);

        List<TicketResponse> ticketResponses = ticketsPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<TicketResponse>builder()
                .content(ticketResponses)
                .currentPage(ticketsPage.getNumber())
                .pageSize(ticketsPage.getSize())
                .totalElements(ticketsPage.getTotalElements())
                .totalPages(ticketsPage.getTotalPages())
                .hasNext(ticketsPage.hasNext())
                .hasPrevious(ticketsPage.hasPrevious())
                .build();
    }

    public PageResponse<TicketResponse> getAllTickets(
            String title,
            TicketStatus status,
            TicketSource source,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ticket> ticketsPage = ticketRepository.findByFilters(title, status, source, null, pageable);

        List<TicketResponse> ticketResponses = ticketsPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<TicketResponse>builder()
                .content(ticketResponses)
                .currentPage(ticketsPage.getNumber())
                .pageSize(ticketsPage.getSize())
                .totalElements(ticketsPage.getTotalElements())
                .totalPages(ticketsPage.getTotalPages())
                .hasNext(ticketsPage.hasNext())
                .hasPrevious(ticketsPage.hasPrevious())
                .build();
    }

    public TicketResponse getTicketById(Long ticketId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to access this ticket");
        }

        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateTicket(Long ticketId, TicketRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to update this ticket");
        }

        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(TicketStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getSource() != null) {
            ticket.setSource(TicketSource.valueOf(request.getSource().toUpperCase()));
        }

        // Update attachments
        if (request.getAttachmentIds() != null) {
            // Clear existing attachments
            if (ticket.getAttachments() != null) {
                ticket.getAttachments().forEach(a -> a.setTicket(null));
            }

            // Add new attachments
            List<Attachment> newAttachments = attachmentRepository.findByIdIn(request.getAttachmentIds());
            newAttachments.forEach(a -> a.setTicket(ticket));
            ticket.setAttachments(newAttachments);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to delete this ticket");
        }

        ticketRepository.delete(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        List<AttachmentResponse> attachmentResponses = ticket.getAttachments() != null
                ? ticket.getAttachments().stream()
                    .map(this::mapAttachmentToResponse)
                    .collect(Collectors.toList())
                : List.of();

        List<AnalysisResponse> analysisResponses = ticket.getAnalyses() != null
                ? ticket.getAnalyses().stream()
                    .map(this::mapAnalysisToResponse)
                    .collect(Collectors.toList())
                : List.of();

        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .source(ticket.getSource())
                .createdAt(ticket.getCreatedAt())
                .userId(ticket.getUser().getId())
                .userEmail(ticket.getUser().getEmail())
                .userName(ticket.getUser().getName())
                .attachments(attachmentResponses)
                .analyses(analysisResponses)
                .build();
    }

    private AttachmentResponse mapAttachmentToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .mediaType(attachment.getMediaType())
                .bucketName(attachment.getBucketName())
                .storagePath(attachment.getStoragePath())
                .publicUrl(attachment.getPublicUrl())
                .createdAt(attachment.getCreatedAt())
                .ticketId(attachment.getTicket() != null ? attachment.getTicket().getId() : null)
                .build();
    }

    private AnalysisResponse mapAnalysisToResponse(AIAnalysis analysis) {
        return AnalysisResponse.builder()
                .id(analysis.getId())
                .category(analysis.getCategory())
                .priority(analysis.getPriority())
                .sentiment(analysis.getSentiment())
                .keywords(analysis.getKeywords())
                .confidenceScore(analysis.getConfidenceScore())
                .createdAt(analysis.getCreatedAt())
                .ticketId(analysis.getTicket() != null ? analysis.getTicket().getId() : null)
                .build();
    }
}
