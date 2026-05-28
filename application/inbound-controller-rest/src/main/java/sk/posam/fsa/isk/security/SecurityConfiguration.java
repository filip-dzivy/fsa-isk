package sk.posam.fsa.isk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    private final RestSecurityExceptionHandler restSecurityExceptionHandler;

    SecurityConfiguration(RestSecurityExceptionHandler restSecurityExceptionHandler) {
        this.restSecurityExceptionHandler = restSecurityExceptionHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Admin operational endpoints — manuálny trigger jobov + override dátumov pre debugovanie
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Books — verejné čítanie, ADMIN/LIBRARIAN write
                        .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/books/*/copies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/*/description").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/books/*/photos").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/books/*/photos/*").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/books", "/books/**").permitAll()
                        // Members
                        .requestMatchers(HttpMethod.POST, "/members").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/members/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/members/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/membership/renew").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/fines/*/pay").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/fines/*/waive").hasRole("ADMIN")
                        // Loans
                        .requestMatchers(HttpMethod.POST, "/loans").hasRole("LIBRARIAN")
                        .requestMatchers("/loans/*/return").hasRole("LIBRARIAN")
                        .requestMatchers("/loans/*/renew").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers(HttpMethod.GET, "/loans/overdue").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/loans/**").authenticated()
                        // Reservations
                        .requestMatchers(HttpMethod.POST, "/reservations").hasAnyRole("MEMBER", "LIBRARIAN")
                        .requestMatchers("/reservations/*/cancel").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reservations").authenticated()
                        // Announcements — verejne čítanie, ADMIN/LIBRARIAN write
                        .requestMatchers(HttpMethod.GET, "/announcements", "/announcements/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/announcements").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/announcements/*").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/announcements/*").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/announcements/*/photos").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/announcements/*/photos/*").hasAnyRole("ADMIN", "LIBRARIAN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restSecurityExceptionHandler)
                        .accessDeniedHandler(restSecurityExceptionHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            jwt.decoder(jwtDecoder);
                            jwt.jwtAuthenticationConverter(JwtConverter::new);
                        }))
                .build();
    }
}