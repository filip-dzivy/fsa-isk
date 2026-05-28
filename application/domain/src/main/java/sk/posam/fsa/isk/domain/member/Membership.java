package sk.posam.fsa.isk.domain.member;

import java.time.LocalDate;
import java.util.Objects;

public final class Membership {
    private static final int RENEWAL_MONTHS = 12;
    private static final int EXPIRY_WARNING_DAYS = 30;

    private LocalDate expiryDate;
    private MembershipStatus status;

    public Membership() {};

    public Membership(LocalDate expiryDate){
        Objects.requireNonNull(expiryDate);
        this.expiryDate = expiryDate;
        this.status = expiryDate.isBefore(LocalDate.now()) ? MembershipStatus.EXPIRED : MembershipStatus.ACTIVE;
    }

    private Membership(LocalDate expiryDate, MembershipStatus status){
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public static Membership createNew(){
        return new Membership(LocalDate.now().plusMonths(RENEWAL_MONTHS));
    }

    public boolean isExpiringSoon(){
        LocalDate today = LocalDate.now();
        return isActive()
                && !expiryDate.isAfter(today.plusDays(EXPIRY_WARNING_DAYS));
    }

    public Membership renew(){
        LocalDate base = isActive() ? expiryDate : LocalDate.now();
        return new Membership(base.plusMonths(RENEWAL_MONTHS), MembershipStatus.ACTIVE);
    }

    public Membership withExpiry(LocalDate newExpiry) {
        Objects.requireNonNull(newExpiry);
        MembershipStatus newStatus = newExpiry.isBefore(LocalDate.now())
                ? MembershipStatus.EXPIRED
                : MembershipStatus.ACTIVE;
        return new Membership(newExpiry, newStatus);
    }

    public Membership expired() {
        return new Membership(expiryDate, MembershipStatus.EXPIRED);
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }
    public LocalDate getExpiryDate () {return expiryDate;}
    public MembershipStatus getStatus() {return status;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membership)) return false;
        Membership m = (Membership) o;
        return expiryDate.equals(m.expiryDate) && status == m.status;
    }

    @Override
    public int hashCode() { return Objects.hash(expiryDate, status); }

    @Override
    public String toString() {
        return "Membership{expiry=" + expiryDate + ", status=" + status + "}";
    }

}