package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementPhoto;
import sk.posam.fsa.isk.domain.announcement.service.AnnouncementFacade;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.mapper.AnnouncementMapper;
import sk.posam.fsa.isk.rest.api.AnnouncementsApi;
import sk.posam.fsa.isk.rest.dto.AnnouncementDto;
import sk.posam.fsa.isk.rest.dto.AnnouncementPhotoDto;
import sk.posam.fsa.isk.rest.dto.CreateAnnouncementRequestDto;
import sk.posam.fsa.isk.rest.dto.UpdateAnnouncementRequestDto;
import sk.posam.fsa.isk.security.CurrentUserDetailService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
public class AnnouncementRestController implements AnnouncementsApi {

    private static final Set<String> ALLOWED_PHOTO_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");
    private static final long MAX_PHOTO_SIZE_BYTES = 5L * 1024 * 1024;

    private final AnnouncementFacade announcementFacade;
    private final AnnouncementMapper announcementMapper;
    private final CurrentUserDetailService currentUserDetailService;

    public AnnouncementRestController(AnnouncementFacade announcementFacade,
                                      AnnouncementMapper announcementMapper,
                                      CurrentUserDetailService currentUserDetailService) {
        this.announcementFacade = announcementFacade;
        this.announcementMapper = announcementMapper;
        this.currentUserDetailService = currentUserDetailService;
    }

    @Override
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementMapper.toDto(announcementFacade.findAll()));
    }

    @Override
    public ResponseEntity<AnnouncementDto> getAnnouncementById(Long id) {
        return ResponseEntity.ok(announcementMapper.toDto(announcementFacade.find(id)));
    }

    @Override
    public ResponseEntity<AnnouncementDto> createAnnouncement(CreateAnnouncementRequestDto dto) {
        Member author = currentUserDetailService.getFullCurrentMember();
        Announcement created = announcementFacade.create(dto.getTitle(), dto.getContent(), author);
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementMapper.toDto(created));
    }

    @Override
    public ResponseEntity<AnnouncementDto> updateAnnouncement(Long id, UpdateAnnouncementRequestDto dto) {
        announcementFacade.update(id, dto.getTitle(), dto.getContent());
        return ResponseEntity.ok(announcementMapper.toDto(announcementFacade.find(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAnnouncement(Long id) {
        announcementFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AnnouncementPhotoDto> uploadAnnouncementPhoto(Long id, MultipartFile file, String caption) {
        if (file == null || file.isEmpty()) {
            throw new DomainException(DomainException.Type.VALIDATION, "Súbor je povinný.");
        }
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new DomainException(DomainException.Type.VALIDATION,
                    "Maximálna veľkosť fotky je 5 MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PHOTO_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new DomainException(DomainException.Type.VALIDATION,
                    "Povolené sú len JPG, PNG, WebP a GIF.");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new DomainException(DomainException.Type.VALIDATION, "Súbor sa nepodarilo prečítať.");
        }
        AnnouncementPhoto photo = announcementFacade.addPhoto(
                id, bytes, contentType, file.getOriginalFilename(), caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementMapper.toPhotoDto(photo));
    }

    @Override
    public ResponseEntity<Void> deleteAnnouncementPhoto(Long id, Long photoId) {
        announcementFacade.removePhoto(id, photoId);
        return ResponseEntity.noContent().build();
    }
}
