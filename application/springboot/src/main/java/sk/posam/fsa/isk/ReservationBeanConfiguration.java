package sk.posam.fsa.isk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.reservation.NotificationPort;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;

@Configuration
public class ReservationBeanConfiguration {

    @Bean
    public ReservationService reservationService(ReservationRepository reservationRepository,
                                                 NotificationPort notificationPort) {
        return new ReservationService(reservationRepository, notificationPort);
    }

    @Bean
    public ReservationFacade reservationFacade(ReservationService reservationService) {
        return reservationService;
    }
}
