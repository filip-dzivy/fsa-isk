package sk.posam.fsa.isk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.jobs.FineAccrualJob;
import sk.posam.fsa.isk.jobs.LoanDueNotificationJob;
import sk.posam.fsa.isk.jobs.LoanStatusJob;
import sk.posam.fsa.isk.jobs.MembershipExpirationJob;
import sk.posam.fsa.isk.jobs.MembershipExpiryNotificationJob;
import sk.posam.fsa.isk.jobs.ReservationExpirationJob;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin/jobs")
public class AdminJobsController {

    private static final Logger log = LoggerFactory.getLogger(AdminJobsController.class);

    private final FineAccrualJob fineAccrualJob;
    private final ReservationExpirationJob reservationExpirationJob;
    private final LoanDueNotificationJob loanDueNotificationJob;
    private final LoanStatusJob loanStatusJob;
    private final MembershipExpiryNotificationJob membershipExpiryNotificationJob;
    private final MembershipExpirationJob membershipExpirationJob;

    public AdminJobsController(FineAccrualJob fineAccrualJob,
                               ReservationExpirationJob reservationExpirationJob,
                               LoanDueNotificationJob loanDueNotificationJob,
                               LoanStatusJob loanStatusJob,
                               MembershipExpiryNotificationJob membershipExpiryNotificationJob,
                               MembershipExpirationJob membershipExpirationJob) {
        this.fineAccrualJob = fineAccrualJob;
        this.reservationExpirationJob = reservationExpirationJob;
        this.loanDueNotificationJob = loanDueNotificationJob;
        this.loanStatusJob = loanStatusJob;
        this.membershipExpiryNotificationJob = membershipExpiryNotificationJob;
        this.membershipExpirationJob = membershipExpirationJob;
    }

    @PostMapping("/fine-accrual")
    public Map<String, Object> triggerFineAccrual() {
        log.info("Manual trigger: fine-accrual");
        Integer processed = fineAccrualJob.accrueOverdueFines();
        return response("fine-accrual", processed);
    }

    @PostMapping("/reservation-expiration")
    public Map<String, Object> triggerReservationExpiration() {
        log.info("Manual trigger: reservation-expiration");
        reservationExpirationJob.expireReadyReservations();
        return response("reservation-expiration", null);
    }

    @PostMapping("/loan-due-notifications")
    public Map<String, Object> triggerLoanDueNotifications() {
        log.info("Manual trigger: loan-due-notifications");
        Integer notified = loanDueNotificationJob.notifyLoansDueSoon();
        return response("loan-due-notifications", notified);
    }

    @PostMapping("/membership-expiry-notifications")
    public Map<String, Object> triggerMembershipExpiryNotifications() {
        log.info("Manual trigger: membership-expiry-notifications");
        Integer notified = membershipExpiryNotificationJob.notifyExpiringMemberships();
        return response("membership-expiry-notifications", notified);
    }

    @PostMapping("/loan-status")
    public Map<String, Object> triggerLoanStatus() {
        log.info("Manual trigger: loan-status");
        Integer processed = loanStatusJob.updateLoanStatuses();
        return response("loan-status", processed);
    }

    @PostMapping("/membership-expiration")
    public Map<String, Object> triggerMembershipExpiration() {
        log.info("Manual trigger: membership-expiration");
        Integer expired = membershipExpirationJob.expireOverdueMemberships();
        return response("membership-expiration", expired);
    }

    private Map<String, Object> response(String job, Integer processed) {
        return processed == null
                ? Map.of("job", job, "executedAt", OffsetDateTime.now().toString())
                : Map.of("job", job, "executedAt", OffsetDateTime.now().toString(), "processed", processed);
    }
}
