package intership.intelligentsupportplatformbackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

}
