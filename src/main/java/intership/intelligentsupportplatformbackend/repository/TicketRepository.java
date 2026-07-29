package intership.intelligentsupportplatformbackend.repository;



import intership.intelligentsupportplatformbackend.model.Ticket;

import intership.intelligentsupportplatformbackend.model.TicketSource;

import intership.intelligentsupportplatformbackend.model.TicketStatus;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;



import java.time.LocalDateTime;

import java.util.List;

import java.util.Map;

import java.util.Optional;



public interface TicketRepository extends JpaRepository<Ticket, Long> {



    Optional<Ticket> findByIdAndUserId(Long ticketId, Long userId);



    @Query("SELECT t FROM Ticket t WHERE " +
           "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:source IS NULL OR t.source = :source) AND " +
           "(:userId IS NULL OR t.user.id = :userId)")
    Page<Ticket> findByFilters(
            @Param("title") String title,
            @Param("status") TicketStatus status,
            @Param("source") TicketSource source,
            @Param("userId") Long userId,
            Pageable pageable
    );

    // Statistics queries
    @Query("SELECT COUNT(t) FROM Ticket t WHERE (:userId IS NULL OR t.user.id = :userId)")
    Long countAllTickets(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND (:userId IS NULL OR t.user.id = :userId)")
    Long countByStatus(@Param("status") TicketStatus status, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdAt >= :startDate AND (:userId IS NULL OR t.user.id = :userId)")
    Long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'CLOSED' AND t.createdAt >= :startDate AND (:userId IS NULL OR t.user.id = :userId)")
    Long countClosedByCreatedAtAfter(@Param("startDate") LocalDateTime startDate, @Param("userId") Long userId);

    @Query("SELECT t.status, COUNT(t) FROM Ticket t WHERE (:userId IS NULL OR t.user.id = :userId) GROUP BY t.status")
    List<Object[]> countByStatusGrouped(@Param("userId") Long userId);

    @Query("SELECT t.source, COUNT(t) FROM Ticket t WHERE (:userId IS NULL OR t.user.id = :userId) GROUP BY t.source")
    List<Object[]> countBySourceGrouped(@Param("userId") Long userId);

    @Query("SELECT a.category, COUNT(t) FROM Ticket t LEFT JOIN t.analyses a WHERE (:userId IS NULL OR t.user.id = :userId) GROUP BY a.category")
    List<Object[]> countByCategoryGrouped(@Param("userId") Long userId);

    @Query("SELECT a.priority, COUNT(t) FROM Ticket t LEFT JOIN t.analyses a WHERE (:userId IS NULL OR t.user.id = :userId) GROUP BY a.priority")
    List<Object[]> countByPriorityGrouped(@Param("userId") Long userId);

    @Query(value = "SELECT DATE(t.created_at), COUNT(*) FROM tickets t WHERE t.created_at >= :startDate AND (:userId IS NULL OR t.user_id = :userId) GROUP BY DATE(t.created_at) ORDER BY DATE(t.created_at)", nativeQuery = true)
    List<Object[]> countByDateGrouped(@Param("startDate") LocalDateTime startDate, @Param("userId") Long userId);

    @Query(value = "SELECT DATE(t.created_at), COUNT(*) FROM tickets t WHERE t.status = 'CLOSED' AND t.created_at >= :startDate AND (:userId IS NULL OR t.user_id = :userId) GROUP BY DATE(t.created_at) ORDER BY DATE(t.created_at)", nativeQuery = true)
    List<Object[]> countClosedByDateGrouped(@Param("startDate") LocalDateTime startDate, @Param("userId") Long userId);

}

