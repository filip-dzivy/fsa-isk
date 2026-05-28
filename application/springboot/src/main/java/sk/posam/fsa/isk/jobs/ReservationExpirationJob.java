package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;

@Component
public class ReservationExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpirationJob.class);

    private final ReservationFacade reservationFacade;

    public ReservationExpirationJob(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "expireReadyReservations", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public void expireReadyReservations() {
        log.info("ReservationExpirationJob: starting");
        reservationFacade.expireReadyReservations();
        log.info("ReservationExpirationJob: finished");
    }
}
