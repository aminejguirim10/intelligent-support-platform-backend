package intership.intelligentsupportplatformbackend.repository;

import intership.intelligentsupportplatformbackend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByIdAndTicketIsNull(Long id);

    List<Attachment> findByIdInAndTicketIsNull(List<Long> ids);
}
