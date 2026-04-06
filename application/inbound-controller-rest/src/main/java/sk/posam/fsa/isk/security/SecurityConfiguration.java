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
                        // Books
                        .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/books/**").authenticated()
                        // Members
                        .requestMatchers(HttpMethod.POST, "/members").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/members/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/membership/renew").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/fines/*/pay").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/members/*/fines/*/waive").hasRole("ADMIN")
                        // Loans
                        .requestMatchers(HttpMethod.POST, "/loans").hasRole("LIBRARIAN")
                        .requestMatchers("/loans/*/return").hasRole("LIBRARIAN")
                        .requestMatchers("/loans/*/renew").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers(HttpMethod.GET, "/loans/**").authenticated()
                        // Reservations
                        .requestMatchers(HttpMethod.POST, "/reservations").hasAnyRole("MEMBER", "LIBRARIAN")
                        .requestMatchers("/reservations/*/cancel").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reservations").authenticated()
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