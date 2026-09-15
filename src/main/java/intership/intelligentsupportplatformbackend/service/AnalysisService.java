package intership.intelligentsupportplatformbackend.service;

import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisRequest;
import intership.intelligentsupportplatformbackend.dto.AIAnalysis.AnalysisResponse;
import intership.intelligentsupportplatformbackend.exception.AnalysisNotFoundException;
import intership.intelligentsupportplatformbackend.exception.TicketNotFoundException;
import intership.intelligentsupportplatformbackend.exception.UnauthorizedAccessException;
import intership.intelligentsupportplatformbackend.exception.UserNotFoundException;
import intership.intelligentsupportplatformbackend.model.AIAnalysis;
import intership.intelligentsupportplatformbackend.model.Role;
import intership.intelligentsupportplatformbackend.model.Ticket;
import intership.intelligentsupportplatformbackend.model.TicketCategory;
import intership.intelligentsupportplatformbackend.model.TicketPriority;
import intership.intelligentsupportplatformbackend.model.User;
import intership.intelligentsupportplatformbackend.repository.AIAnalysisRepository;
import intership.intelligentsupportplatformbackend.repository.TicketRepository;
import intership.intelligentsupportplatformbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AIAnalysisRepository analysisRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnalysisResponse createAnalysis(Long ticketId, AnalysisRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to create analysis for this ticket");
        }

        AIAnalysis analysis = AIAnalysis.builder()
                .category(request.getCategory() != null ? TicketCategory.valueOf(request.getCategory().toUpperCase()) : null)
                .priority(request.getPriority() != null ? TicketPriority.valueOf(request.getPriority().toUpperCase()) : null)
                .sentiment(request.getSentiment())
                .keywords(request.getKeywords())
                .confidenceScore(request.getConfidenceScore())
                .advice(request.getAdvice())
                .createdAt(LocalDateTime.now())
                .ticket(ticket)
                .build();

        AIAnalysis savedAnalysis = analysisRepository.save(analysis);
        return mapToResponse(savedAnalysis);
    }

    @Transactional
    public AnalysisResponse updateAnalysis(Long ticketId, Long analysisId, AnalysisRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to update analysis for this ticket");
        }

        AIAnalysis analysis = analysisRepository.findByIdAndTicketId(analysisId, ticketId)
                .orElseThrow(() -> new AnalysisNotFoundException("Analysis not found with id: " + analysisId + " for ticket: " + ticketId));

        if (request.getCategory() != null) {
            analysis.setCategory(TicketCategory.valueOf(request.getCategory().toUpperCase()));
        }
        if (request.getPriority() != null) {
            analysis.setPriority(TicketPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getSentiment() != null) {
            analysis.setSentiment(request.getSentiment());
        }
        if (request.getKeywords() != null) {
            analysis.setKeywords(request.getKeywords());
        }
        if (request.getConfidenceScore() != null) {
            analysis.setConfidenceScore(request.getConfidenceScore());
        }
        if (request.getAdvice() != null) {
            analysis.setAdvice(request.getAdvice());
        }

        AIAnalysis updatedAnalysis = analysisRepository.save(analysis);
        return mapToResponse(updatedAnalysis);
    }

    @Transactional
    public void deleteAnalysis(Long ticketId, Long analysisId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to delete analysis for this ticket");
        }

        AIAnalysis analysis = analysisRepository.findByIdAndTicketId(analysisId, ticketId)
                .orElseThrow(() -> new AnalysisNotFoundException("Analysis not found with id: " + analysisId + " for ticket: " + ticketId));

        analysisRepository.delete(analysis);
    }

    private AnalysisResponse mapToResponse(AIAnalysis analysis) {
        return AnalysisResponse.builder()
                .id(analysis.getId())
                .category(analysis.getCategory())
                .priority(analysis.getPriority())
                .sentiment(analysis.getSentiment())
                .keywords(analysis.getKeywords())
                .confidenceScore(analysis.getConfidenceScore())
                .advice(analysis.getAdvice())
                .createdAt(analysis.getCreatedAt())
                .ticketId(analysis.getTicket() != null ? analysis.getTicket().getId() : null)
                .build();
    }
}
