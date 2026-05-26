package sk.posam.fsa.isk.domain.announcement.service;

import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementPhoto;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.List;

public interface AnnouncementFacade {

    Announcement create(String title, String content, Member author);

    Announcement update(long id, String title, String content);

    Announcement find(long id);

    List<Announcement> findAll();

    void delete(long id);

    AnnouncementPhoto addPhoto(long announcementId, byte[] bytes, String contentType, String originalFilename, String caption);

    void removePhoto(long announcementId, long photoId);
}