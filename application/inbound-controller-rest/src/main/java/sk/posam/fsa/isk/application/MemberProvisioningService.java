package sk.posam.fsa.isk.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;

@Service
public class MemberProvisioningService {

    private final MemberFacade memberFacade;

    public MemberProvisioningService(MemberFacade memberFacade) {
        this.memberFacade = memberFacade;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Member findOrProvision(Email email, String firstName, String lastName) {
        return memberFacade.findOrProvision(email, firstName, lastName);
    }
}
