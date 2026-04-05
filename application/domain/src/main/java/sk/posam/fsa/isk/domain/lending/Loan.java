package sk.posam.fsa.isk.domain.lending;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.lending.predicate.*;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Loan {
    private static final int LOAN_DURATION = 14;
    private static final int MAX_RENEWALS = 1;
    private long id;
    private Member loanedTo;
    private Book book;
    private Member createdBy;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int renewalCount;
    private LoanStatus status;

    public Loan() {}

    public Loan(Member loanedTo, Book book, Member createdBy, LocalDate loanDate, int durationDays) {
        this.loanedTo = loanedTo;
        this.book = book;
        this.createdBy = createdBy;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(durationDays);
        this.renewalCount = 0;
        this.status = LoanStatus.ACTIVE;
    }

    public Loan(Member loanedTo, Book book, Member createdBy) {
        this(loanedTo, book, createdBy, LocalDate.now(), LOAN_DURATION);
    }

    public void validateForCreation() {
        require(IsOwnedByMemberPredicate.INSTANCE.test(this, loanedTo),
                DomainException.Type.VALIDATION,
                "Výpožička musí patriť platnému členovi.");
        require(IsCreatedByLibrarianPredicate.INSTANCE.test(this),
                DomainException.Type.VALIDATION,
                "Výpožičku musí vytvoriť knihovník.");
        require(book != null,
                DomainException.Type.VALIDATION,
                "Kniha je povinný údaj.");
    }

    public void returnBook(){
        require(status != LoanStatus.RETURNED,
                DomainException.Type.CONFLICT,
                "Vypožička " + id + "je už vrátená.");
        this.returnDate = LocalDate.now();
        this.status = LoanStatus.RETURNED;
    }

    public void renew(){
        require(IsActiveLoanPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Predlžiť je možné len aktívnu výpožičku.");
        require(IsNotOverdueLoanPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Vypožičku po termíne vrátenia nie je možné predĺžiť.");
        require(HasRenewalCapacityPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Vypožičku viackrát už nie je možné predĺžiť.");
        this.dueDate = dueDate.plusDays(LOAN_DURATION);
        this.renewalCount++;
    }

    public boolean isOverdue() {
        if (status == LoanStatus.RETURNED) return false;
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        return checkDate.isAfter(dueDate);
    }

    public void markOverdue(){
        if(status == LoanStatus.ACTIVE && isOverdue()){
            this.status = LoanStatus.OVERDUE;
        }
    }

    public long daysOverdue() {
        if (!isOverdue() && returnDate == null) return 0;
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, checkDate));
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

    public LocalDate getDueDate(){return dueDate;}

    public LocalDate getReturnDate() {return returnDate;}

    public LocalDate getLoanDate() {return loanDate;}

    public long getId() {return id;}

    public Book getBook() {return book;}

    public void setLoanedTo(Member loanedTo) { this.loanedTo = loanedTo; }

    public void setCreatedBy(Member createdBy) { this.createdBy = createdBy; }

    public void setBook(Book book) { this.book = book; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan)) return false;
        return Objects.equals(id, ((Loan) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Loan{id=" + id + ", member=" + loanedTo.getId()
                + ", isbn=" + book.getIsbn() + ", due=" + dueDate + ", status=" + status + "}";
    }

}
