package sk.isk.domain.lending;

import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.lending.predicate.IsCreatedByLibrarianPredicate;
import sk.isk.domain.lending.predicate.IsOwnedByMemberPredicate;
import sk.isk.domain.membership.Member;
import sk.isk.domain.shared.DomainException;

import java.time.LocalDate;

public class Loan {
    private static final int LOAN_DURATION = 14;
    private static final int MAX_RENEWALS = 1;
    private long id;
    private Member loanedTo;
    private ISBN bookIsbn;
    private Member createdBy;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int renewalCount;
    private LoanStatus status;

    public Loan() {}

    public Loan(Member loanedTo, ISBN bookIsbn, Member createdBy, LocalDate loanDate, int durationDays) {
        this.loanedTo = loanedTo;
        this.bookIsbn = bookIsbn;
        this.createdBy = createdBy;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(durationDays);
        this.renewalCount = 0;
        this.status = LoanStatus.ACTIVE;
    }

    public Loan(Member loanedTo, ISBN bookIsbn, Member createdBy) {
        this(loanedTo, bookIsbn, createdBy, LocalDate.now(), LOAN_DURATION);
    }

    public void validateForCreation() {
        require(IsOwnedByMemberPredicate.INSTANCE.test(this, loanedTo),
                DomainException.Type.VALIDATION,
                "Výpožička musí patriť platnému členovi.");
        require(IsCreatedByLibrarianPredicate.INSTANCE.test(this),
                DomainException.Type.VALIDATION,
                "Výpožičku musí vytvoriť knihovník.");
        require(bookIsbn != null,
                DomainException.Type.VALIDATION,
                "ISBN knihy je povinný údaj.");
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    public Member getCreatedBy() {
        return createdBy;
    }

    public Member getLoanedTo(){
        return loanedTo;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public LoanStatus getStatus(){
        return status;
    }

    public boolean isOverdue() {
        if (status == LoanStatus.RETURNED) return false;
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        return checkDate.isAfter(dueDate);
    }
}
