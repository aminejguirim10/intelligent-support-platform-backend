package intership.intelligentsupportplatformbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition="TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketSource source;

    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(
            mappedBy="ticket",
            cascade=CascadeType.ALL
    )
    private
    List<AIAnalysis> analyses;

    @OneToMany(
            mappedBy="ticket",
            cascade=CascadeType.ALL
    )
    private List<Attachment> attachments;
}
