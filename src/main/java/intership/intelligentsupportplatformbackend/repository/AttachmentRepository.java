package intership.intelligentsupportplatformbackend.repository;

import intership.intelligentsupportplatformbackend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
