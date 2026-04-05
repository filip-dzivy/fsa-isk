package sk.posam.fsa.isk.domain.finance;

import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Objects;

public class Fine {

    private long id;
    private Money amount;
    private String reason;
    private FineStatus status;
    private Loan loan;
    Member member;

    Fine() {
        // for JPA
    }

    public Fine(Money amount, String reason) {
        Objects.requireNonNull(amount);
        if (!amount.isGreaterThanZero())
            throw new DomainException("Pokuta musí byť väčšia ako nula.");
        if (reason == null || reason.isBlank())
            throw new DomainException("Dôvod nesmie byť prázdny.");
        this.amount = amount;
        this.reason = reason;
        this.status = FineStatus.PENDING;
        this.loan = null;
    }

    public Fine(Money amount, String reason, Loan loan) {
        Objects.requireNonNull(amount, "Suma pokuty je povinná.");
        Objects.requireNonNull(loan, "Vypôžička je povinná.");
        if(!amount.isGreaterThanZero())
            throw new DomainException("Pokuta musí byť väčšia ako nula.");
        this.amount = amount;
        this.reason = reason;
        this.loan = loan;
        this.status = FineStatus.PENDING;
    }

    public void pay() {
        if (status == FineStatus.PAID) throw new DomainException("Pokuta je už uhradená.");
        this.status = FineStatus.PAID;
    }

    public void waive() {
        if (status == FineStatus.PAID) throw new DomainException("Uhradenú pokutu nemožno odpustiť.");
        this.status = FineStatus.WAIVED;
    }

    public void updateAmount(Money newAmount) {
        if (status != FineStatus.PENDING)
            throw new DomainException("Sumu možno meniť len pri PENDING pokute.");
        Objects.requireNonNull(newAmount);
        if (!newAmount.isGreaterThanZero())
            throw new DomainException("Suma musí byť kladná.");
        this.amount = newAmount;
    }

    public boolean isPaid() {
        return status == FineStatus.PAID || status == FineStatus.WAIVED;
    }

    public long getId() { return id; }
    public Money getAmount() { return amount; }
    public String getReason() { return reason; }
    public FineStatus getStatus() { return status; }
    public Loan getLoan() { return loan; }
    public Member getMember() { return member; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fine)) return false;
        return Objects.equals(id, ((Fine) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Fine{id=" + id + ", amount=" + amount
                + ", reason='" + reason + "', status=" + status + "}";
    }
}
