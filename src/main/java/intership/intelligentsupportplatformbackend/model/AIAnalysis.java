package intership.intelligentsupportplatformbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_analysis")
public class AIAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private String sentiment;

    @Column(columnDefinition="TEXT")
    private String keywords;

    private Double confidenceScore;

    @Column(columnDefinition="TEXT")
    private String advice;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

}
