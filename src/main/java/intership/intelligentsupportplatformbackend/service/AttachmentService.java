package intership.intelligentsupportplatformbackend.service;

import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentRequest;
import intership.intelligentsupportplatformbackend.dto.Attachment.AttachmentResponse;
import intership.intelligentsupportplatformbackend.exception.AttachmentNotFoundException;
import intership.intelligentsupportplatformbackend.model.Attachment;
import intership.intelligentsupportplatformbackend.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Transactional
    public AttachmentResponse createAttachment(AttachmentRequest request) {
        Attachment attachment = Attachment.builder()
                .filename(request.getFilename())
                .mediaType(request.getMediaType())
                .bucketName(request.getBucketName())
                .storagePath(request.getStoragePath())
                .publicUrl(request.getPublicUrl())
                .createdAt(LocalDateTime.now())
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return mapToResponse(savedAttachment);
    }

    public AttachmentResponse getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found with id: " + id));
        return mapToResponse(attachment);
    }

    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findByIdAndTicketIsNull(id)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found or already linked to a ticket: " + id));
        attachmentRepository.delete(attachment);
    }

    public List<Attachment> getAttachmentsByIds(List<Long> ids) {
        return attachmentRepository.findByIdInAndTicketIsNull(ids);
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .mediaType(attachment.getMediaType())
                .bucketName(attachment.getBucketName())
                .storagePath(attachment.getStoragePath())
                .publicUrl(attachment.getPublicUrl())
                .createdAt(attachment.getCreatedAt())
                .ticketId(attachment.getTicket() != null ? attachment.getTicket().getId() : null)
                .build();
    }
}
