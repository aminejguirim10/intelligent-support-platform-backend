package intership.intelligentsupportplatformbackend.repository;

import intership.intelligentsupportplatformbackend.model.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Long> {

    Optional<AIAnalysis> findByIdAndTicketId(Long analysisId, Long ticketId);
}
