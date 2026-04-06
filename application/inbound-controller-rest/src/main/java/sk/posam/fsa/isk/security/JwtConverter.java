package sk.posam.fsa.isk.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.rest.dto.MemberDto;
import sk.posam.fsa.isk.rest.dto.MemberRoleDto;

import java.util.*;

class JwtConverter extends AbstractAuthenticationToken {
    private final Jwt source;

    public JwtConverter(Jwt source) {
        super(toAuthorities(source));
        this.source = Objects.requireNonNull(source);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return Collections.emptyList();
    }

    @Override
    public Object getPrincipal() {
        MemberDto dto = new MemberDto();
        dto.setEmail(source.getClaimAsString("email"));
        dto.setFirstName(source.getClaimAsString("given_name"));
        dto.setMemberRole(getRole());
        return dto;
    }

    private MemberRoleDto getRole() {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) return null;

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .filter(role -> Arrays.stream(MemberRoleDto.values())
                        .anyMatch(e -> e.name().equals(role)))
                .map(MemberRoleDto::valueOf)
                .findFirst()
                .orElse(null);
    }

    private static Collection<? extends GrantedAuthority> toAuthorities(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
