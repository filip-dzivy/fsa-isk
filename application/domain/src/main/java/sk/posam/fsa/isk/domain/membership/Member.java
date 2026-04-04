package sk.posam.fsa.isk.domain.membership;


import sk.posam.fsa.isk.domain.membership.predicate.*;
import sk.posam.fsa.isk.domain.membership.predicate.*;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Member {
    private long id;
    private Email email;
    private String firstName;
    private String lastName;
    private Membership membership;
    private MemberRole memberRole;
    private List<Fine> fines;

    public Member() {}

    public Member(long id, Email email, String firstName, String lastName, MemberRole role){
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.memberRole = role;
        this.fines = new ArrayList<>();
        validateForCreation();
    }

    public void addFine(Fine fine){
        require(fine != null,
                DomainException.Type.VALIDATION,
                "Pokuta nesmie byť null.");
        fines.add(fine);
    }

    public void payFine(Fine fine) {
        int idx = fines.indexOf(fine);
        require(idx != -1,
                DomainException.Type.NOT_FOUND,
                "Pokuta sa nenašla alebo je už uhradená.");
        fines.set(idx, fine.pay());
    }

    public void assignMembership(Membership membership) {
        require(membership != null,
                DomainException.Type.VALIDATION,
                "Členstvo nesmie byť null.");
        this.membership = membership;
    }

    public void renewMembership() {
        require(membership != null,
                DomainException.Type.CONFLICT,
                "Čitateľ nemá priradené členstvo.");
        this.membership = membership.renew();
    }

    public boolean hasUnpaidFines() {return fines.stream().anyMatch(f -> !f.isPaid());}

    public boolean canBorrow(){
        return HasActiveMembershipPredicate.INSTANCE.test(membership)
                && HasNoUnpaidFinesPredicate.INSTANCE.test(this);
    }

    public long getId() {
        return id;
    }

    public List<Fine> getFines() {
        return fines;
    }

    public Membership getMembership(){
        return membership;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public String getFirstName () {return firstName;}

    public String getLastName () {return lastName;}

    public Email getEmail () {return email;}

    public String getFullName() {return firstName + " " + lastName;}

    public void validateForCreation() {
        require(HasCorrectEmailFormatPredicate.INSTANCE.test(email.toString()),
                DomainException.Type.VALIDATION,
                "Email je v nesprávnom formáte alebo chýba");
        require(HasFirstNamePredicate.INSTANCE.test(firstName),
                DomainException.Type.VALIDATION,
                "Krstné meno je povinný údaj");
        require(HasLastNamePredicate.INSTANCE.test(lastName),
                DomainException.Type.VALIDATION,
                "Priezvisko je povinný údaj");
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        return Objects.equals(id, ((Member) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + getFullName() + "', email=" + email + "}";
    }

}