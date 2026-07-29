package intership.intelligentsupportplatformbackend.service;

import intership.intelligentsupportplatformbackend.dto.Statistics.TicketStatisticsResponse;
import intership.intelligentsupportplatformbackend.exception.UserNotFoundException;
import intership.intelligentsupportplatformbackend.model.Role;
import intership.intelligentsupportplatformbackend.repository.TicketRepository;
import intership.intelligentsupportplatformbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email))
                .getId();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email))
                .getRole() == Role.ADMIN;
    }

    public TicketStatisticsResponse getTicketStatistics() {
        Long userId = isAdmin() ? null : getCurrentUserId();
        
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusWeeks(1);
        LocalDateTime monthAgo = now.minusMonths(1);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // Overall counts
        Long totalTickets = ticketRepository.countAllTickets(userId);
        Long openTickets = ticketRepository.countByStatus(intership.intelligentsupportplatformbackend.model.TicketStatus.OPEN, userId);
        Long closedTickets = ticketRepository.countByStatus(intership.intelligentsupportplatformbackend.model.TicketStatus.CLOSED, userId);

        // Time-based counts
        Long ticketsThisWeek = ticketRepository.countByCreatedAtAfter(weekAgo, userId);
        Long ticketsThisMonth = ticketRepository.countByCreatedAtAfter(monthAgo, userId);
        Long closedThisWeek = ticketRepository.countClosedByCreatedAtAfter(weekAgo, userId);
        Long closedThisMonth = ticketRepository.countClosedByCreatedAtAfter(monthAgo, userId);

        // Grouped counts
        Map<String, Long> ticketsByStatus = convertToMap(ticketRepository.countByStatusGrouped(userId));
        Map<String, Long> ticketsBySource = convertToMap(ticketRepository.countBySourceGrouped(userId));
        Map<String, Long> ticketsByCategory = convertToMap(ticketRepository.countByCategoryGrouped(userId));
        Map<String, Long> ticketsByPriority = convertToMap(ticketRepository.countByPriorityGrouped(userId));

        // Daily counts for last 30 days
        Map<String, Long> ticketsCreatedPerDay = convertDateMap(ticketRepository.countByDateGrouped(thirtyDaysAgo, userId));
        Map<String, Long> ticketsClosedPerDay = convertDateMap(ticketRepository.countClosedByDateGrouped(thirtyDaysAgo, userId));

        return TicketStatisticsResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .closedTickets(closedTickets)
                .ticketsThisWeek(ticketsThisWeek)
                .ticketsThisMonth(ticketsThisMonth)
                .closedThisWeek(closedThisWeek)
                .closedThisMonth(closedThisMonth)
                .ticketsByStatus(ticketsByStatus)
                .ticketsBySource(ticketsBySource)
                .ticketsByCategory(ticketsByCategory)
                .ticketsByPriority(ticketsByPriority)
                .ticketsCreatedPerDay(ticketsCreatedPerDay)
                .ticketsClosedPerDay(ticketsClosedPerDay)
                .build();
    }

    private Map<String, Long> convertToMap(List<Object[]> results) {
        return results.stream()
                .filter(row -> row[0] != null) 
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1],
                        (existing, replacement) -> existing
                ));
    }

    private Map<String, Long> convertDateMap(List<Object[]> results) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return results.stream()
                .filter(row -> row[0] != null) 
                .collect(Collectors.toMap(
                        row -> {
                            if (row[0] instanceof java.sql.Date) {
                                return ((java.sql.Date) row[0]).toLocalDate().format(formatter);
                            } else if (row[0] instanceof LocalDateTime) {
                                return ((LocalDateTime) row[0]).toLocalDate().format(formatter);
                            }
                            return row[0].toString();
                        },
                        row -> (Long) row[1],
                        (existing, replacement) -> existing
                ));
    }
}
