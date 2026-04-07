package sk.posam.fsa.isk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.reservation.NotificationPort;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;

@Configuration
public class ReservationBeanConfiguration {

    @Bean
    public ReservationService reservationService(ReservationRepository reservationRepository,
                                                 MemberRepository memberRepository,
                                                 NotificationPort notificationPort) {
        return new ReservationService(reservationRepository, memberRepository,notificationPort);
    }

    @Bean
    public ReservationFacade reservationFacade(ReservationService reservationService) {
        return reservationService;
    }
}
