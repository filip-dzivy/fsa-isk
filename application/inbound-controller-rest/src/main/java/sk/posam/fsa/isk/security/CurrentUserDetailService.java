package sk.posam.fsa.isk.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.posam.fsa.isk.application.MemberProvisioningService;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.rest.dto.MemberDto;

import java.util.List;

@Service
public class CurrentUserDetailService {

    private final MemberProvisioningService memberProvisioningService;
    private final MemberFacade memberFacade;

    public CurrentUserDetailService(MemberProvisioningService memberProvisioningService,
                                    MemberFacade memberFacade) {
        this.memberProvisioningService = memberProvisioningService;
        this.memberFacade = memberFacade;
    }

    public MemberDto getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MemberDto) {
            return (MemberDto) principal;
        }

        throw new DomainException(
                DomainException.Type.UNAUTHORIZED,
                "Authentication required",
                List.of("Current principal is not a valid API user"));
    }

    public String getUserEmail() {
        return getCurrentUser().getEmail();
    }

    public Member getFullCurrentMember() {
        MemberDto principal = getCurrentUser();
        Email email = new Email(principal.getEmail());
        // Zaisti ze member je v DB
        memberProvisioningService.findOrProvision(
                email,
                principal.getFirstName(),
                principal.getLastName()
        );
        // Re-fetch v current TX aby lazy loading fungovali.
        return memberFacade.find(email);
    }

    public boolean hasRole(String role){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean isPrivileged(){
        return hasRole("ADMIN") || hasRole("LIBRARIAN");
    }
}
