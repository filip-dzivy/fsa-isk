package sk.posam.fsa.isk.domain.member.access;

import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Optional;

public class MemberVisibilityResolver {

    private final MemberRepository memberRepository;

    public MemberVisibilityResolver(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Optional<Member> resolve(Member requesting, Long targetMemberId) {
        if (requesting.isPrivileged()) {
            if (targetMemberId == null) return Optional.empty();
            Member target = memberRepository.find(targetMemberId)
                    .orElseThrow(() -> new DomainException(
                            DomainException.Type.NOT_FOUND,
                            "Člen s ID " + targetMemberId + " neexistuje."));
            return Optional.of(target);
        }
        if (targetMemberId != null && !targetMemberId.equals(requesting.getId())) {
            throw new DomainException(
                    DomainException.Type.FORBIDDEN,
                    "Nemáte oprávnenie zobraziť dáta iného člena.");
        }
        return Optional.of(requesting);
    }
}
