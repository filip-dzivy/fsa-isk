package sk.posam.fsa.isk.domain.member.access;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberVisibilityResolverTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberVisibilityResolver resolver;

    private Member librarian;
    private Member member;
    private Member otherMember;

    @BeforeEach
    void setUp() {
        resolver = new MemberVisibilityResolver(memberRepository);
        librarian = new Member(10L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
        member = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        otherMember = new Member(2L, new Email("eva@example.sk"), "Eva", "K", MemberRole.MEMBER);
    }

    @Test
    void privilegedWithNullTargetReturnsEmpty() {
        Optional<Member> result = resolver.resolve(librarian, null);
        assertTrue(result.isEmpty());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void privilegedWithValidTargetReturnsTarget() {
        when(memberRepository.find(member.getId())).thenReturn(Optional.of(member));

        Optional<Member> result = resolver.resolve(librarian, member.getId());

        assertTrue(result.isPresent());
        assertEquals(member, result.get());
    }

    @Test
    void privilegedWithUnknownTargetThrowsNotFound() {
        when(memberRepository.find(999L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> resolver.resolve(librarian, 999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void nonPrivilegedWithNullTargetReturnsSelf() {
        Optional<Member> result = resolver.resolve(member, null);
        assertTrue(result.isPresent());
        assertEquals(member, result.get());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void nonPrivilegedWithOwnIdReturnsSelf() {
        Optional<Member> result = resolver.resolve(member, member.getId());
        assertTrue(result.isPresent());
        assertEquals(member, result.get());
    }

    @Test
    void nonPrivilegedWithForeignIdThrowsForbidden() {
        DomainException ex = assertThrows(DomainException.class,
                () -> resolver.resolve(member, otherMember.getId()));
        assertEquals(DomainException.Type.FORBIDDEN, ex.getType());
    }

    @Test
    void adminBehavesAsPrivileged() {
        Member admin = new Member(100L, new Email("admin@example.sk"), "Ad", "Min", MemberRole.ADMIN);
        when(memberRepository.find(member.getId())).thenReturn(Optional.of(member));

        Optional<Member> result = resolver.resolve(admin, member.getId());
        assertTrue(result.isPresent());
        assertEquals(member, result.get());
    }
}
