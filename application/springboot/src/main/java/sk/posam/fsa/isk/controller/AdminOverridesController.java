package sk.posam.fsa.isk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.lending.service.LoanFacade;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/admin/overrides")
public class AdminOverridesController {

    private static final Logger log = LoggerFactory.getLogger(AdminOverridesController.class);

    private final LoanFacade loanFacade;
    private final MemberFacade memberFacade;
    private final ReservationFacade reservationFacade;

    public AdminOverridesController(LoanFacade loanFacade,
                                    MemberFacade memberFacade,
                                    ReservationFacade reservationFacade) {
        this.loanFacade = loanFacade;
        this.memberFacade = memberFacade;
        this.reservationFacade = reservationFacade;
    }

    public record DateOverrideRequest(LocalDate date) {}

    @PostMapping("/loans/{id}/due-date")
    public Map<String, Object> changeLoanDueDate(@PathVariable long id,
                                                 @RequestBody DateOverrideRequest body) {
        log.info("Admin override: loan {} dueDate -> {}", id, body.date());
        loanFacade.changeDueDate(id, body.date());
        return Map.of("loanId", id, "dueDate", body.date().toString());
    }

    @PostMapping("/members/{id}/membership-expiry")
    public Map<String, Object> changeMembershipExpiry(@PathVariable long id,
                                                      @RequestBody DateOverrideRequest body) {
        log.info("Admin override: member {} membership expiry -> {}", id, body.date());
        memberFacade.changeMembershipExpiry(id, body.date());
        return Map.of("memberId", id, "expiryDate", body.date().toString());
    }

    @PostMapping("/reservations/{id}/created-on")
    public Map<String, Object> changeReservationCreatedOn(@PathVariable long id,
                                                          @RequestBody DateOverrideRequest body) {
        log.info("Admin override: reservation {} createdOn -> {}", id, body.date());
        reservationFacade.changeCreatedOn(id, body.date());
        return Map.of("reservationId", id, "createdOn", body.date().toString());
    }
}