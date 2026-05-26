package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementPhoto;
import sk.posam.fsa.isk.rest.dto.AnnouncementDto;
import sk.posam.fsa.isk.rest.dto.AnnouncementPhotoDto;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

@Component
public class AnnouncementMapper {

    public AnnouncementDto toDto(Announcement entity) {
        if (entity == null) return null;
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setPublishedAt(entity.getPublishedAt() != null
                ? entity.getPublishedAt().atOffset(ZoneOffset.UTC)
                : null);
        dto.setAuthorName(entity.getAuthor() != null
                ? entity.getAuthor().getFullName()
                : null);
        dto.setPhotos(entity.getPhotos().stream().map(this::toPhotoDto).toList());
        return dto;
    }

    public AnnouncementPhotoDto toPhotoDto(AnnouncementPhoto photo) {
        if (photo == null) return null;
        AnnouncementPhotoDto dto = new AnnouncementPhotoDto();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());
        dto.setCaption(photo.getCaption());
        dto.setPosition(photo.getPosition());
        return dto;
    }

    public List<AnnouncementDto> toDto(Collection<Announcement> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}