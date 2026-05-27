package sk.posam.fsa.isk.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineStatus;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.member.Membership;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService service;

    private Email email;
    private Member member;

    @BeforeEach
    void setUp() {
        email = new Email("jan.novak@example.sk");
        member = new Member(1L, email, "Jan", "Novak", MemberRole.MEMBER);
    }

    @Test
    void createAssignsMembershipAndSaves() {
        when(memberRepository.find(email)).thenReturn(Optional.empty());

        service.create(member);

        assertNotNull(member.getMembership());
        assertTrue(member.getMembership().isActive());
        verify(memberRepository).save(member);
    }

    @Test
    void createWithDuplicateEmailThrowsConflict() {
        when(memberRepository.find(email)).thenReturn(Optional.of(member));

        DomainException ex = assertThrows(DomainException.class, () -> service.create(member));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void findByIdReturnsMember() {
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));
        assertEquals(member, service.find(1L));
    }

    @Test
    void findByIdNotFoundThrows() {
        when(memberRepository.findWithFines(999L)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.find(999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void findByEmailNotFoundThrows() {
        when(memberRepository.find(email)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.find(email));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void findOrProvisionReturnsExistingWithoutCreating() {
        when(memberRepository.find(email)).thenReturn(Optional.of(member));

        Member found = service.findOrProvision(email, "Jan", "Novak");

        assertEquals(member, found);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void findOrProvisionCreatesNewMemberWhenMissing() {
        when(memberRepository.find(email))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(member));

        Member result = service.findOrProvision(email, "Jan", "Novak");

        verify(memberRepository).save(argThat(m ->
                m.getEmail().equals(email)
                        && m.getMembership() != null
                        && m.getMembership().isActive()
                        && m.getMemberRole() == MemberRole.MEMBER));
        assertEquals(member, result);
    }

    @Test
    void renewMembershipAssignsNewWhenMissing() {
        Member m = new Member(2L, new Email("y@y.sk"), "Y", "Y", MemberRole.MEMBER);
        when(memberRepository.findWithFines(2L)).thenReturn(Optional.of(m));

        service.renewMembership(2L);

        assertNotNull(m.getMembership());
        assertTrue(m.getMembership().isActive());
        verify(memberRepository).save(m);
    }

    @Test
    void renewMembershipExtendsExisting() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        member.assignMembership(new Membership(expiry));
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));

        service.renewMembership(1L);

        assertEquals(expiry.plusMonths(12), member.getMembership().getExpiryDate());
        verify(memberRepository).save(member);
    }

    @Test
    void payFineDelegatesToFine() {
        Fine fine = new Fine(Money.of(1.00, "EUR"), "Oneskorenie");
        member.addFine(fine);
        // We can't control the fine ID (0L), so look it up by what's available
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));

        service.payFine(1L, fine.getId());

        assertEquals(FineStatus.PAID, fine.getStatus());
        verify(memberRepository).save(member);
    }

    @Test
    void payFineNotFoundThrows() {
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));
        DomainException ex = assertThrows(DomainException.class, () -> service.payFine(1L, 999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void waiveFineDelegatesToFine() {
        Fine fine = new Fine(Money.of(1.00, "EUR"), "Oneskorenie");
        member.addFine(fine);
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));

        service.waiveFine(1L, fine.getId());

        assertEquals(FineStatus.WAIVED, fine.getStatus());
        verify(memberRepository).save(member);
    }

    @Test
    void waiveFineNotFoundThrows() {
        when(memberRepository.findWithFines(1L)).thenReturn(Optional.of(member));
        DomainException ex = assertThrows(DomainException.class, () -> service.waiveFine(1L, 999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }
}
