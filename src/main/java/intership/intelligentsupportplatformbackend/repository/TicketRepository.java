package intership.intelligentsupportplatformbackend.repository;

import intership.intelligentsupportplatformbackend.model.Ticket;
import intership.intelligentsupportplatformbackend.model.TicketSource;
import intership.intelligentsupportplatformbackend.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
