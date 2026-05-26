package sk.posam.fsa.isk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.announcement.AnnouncementRepository;
import sk.posam.fsa.isk.domain.announcement.service.AnnouncementFacade;
import sk.posam.fsa.isk.domain.announcement.service.AnnouncementService;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

@Configuration
public class AnnouncementBeanConfiguration {

    @Bean
    public AnnouncementFacade announcementFacade(AnnouncementRepository announcementRepository,
                                                 PhotoStoragePort photoStoragePort) {
        return new AnnouncementService(announcementRepository, photoStoragePort);
    }
}