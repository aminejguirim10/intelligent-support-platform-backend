package intership.intelligentsupportplatformbackend.repository;

import intership.intelligentsupportplatformbackend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
