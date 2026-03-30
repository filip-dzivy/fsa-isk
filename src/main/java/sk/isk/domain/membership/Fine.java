package sk.isk.domain.membership;

import sk.isk.domain.shared.DomainException;

import java.util.Objects;

public final class Fine {

    private final Money     amount;
    private final String    reason;
    private final FineStatus status;

    public Fine(Money amount, String reason) {
        Objects.requireNonNull(amount, "Suma pokuty je povinná.");
        if (!amount.isGreaterThanZero()) throw new DomainException("Pokuta musí byť väčšia ako nula.");
        if (reason == null || reason.isBlank()) throw new DomainException("Dôvod pokuty nesmie byť prázdny.");
        this.amount = amount;
        this.reason = reason;
        this.status = FineStatus.PENDING;
    }

    private Fine(Money amount, String reason, FineStatus status) {
        this.amount = amount;
        this.reason = reason;
        this.status = status;
    }

    public Fine pay() {
        if (status == FineStatus.PAID) throw new DomainException("Pokuta je už uhradená.");
        return new Fine(amount, reason, FineStatus.PAID);
    }

    public Fine waive() {
        if (status == FineStatus.PAID) throw new DomainException("Uhradenú pokutu nemožno odpustiť.");
        return new Fine(amount, reason, FineStatus.WAIVED);
    }

    public boolean isPaid() {
        return status == FineStatus.PAID || status == FineStatus.WAIVED;
    }

    public Money     getAmount() { return amount; }
    public String    getReason() { return reason; }
    public FineStatus getStatus(){ return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fine)) return false;
        Fine f = (Fine) o;
        return amount.equals(f.amount) && reason.equals(f.reason) && status == f.status;
    }

    @Override
    public int hashCode() { return Objects.hash(amount, reason, status); }

    @Override
    public String toString() { return "Fine{amount=" + amount + ", reason='" + reason + "', status=" + status + "}"; }
}
