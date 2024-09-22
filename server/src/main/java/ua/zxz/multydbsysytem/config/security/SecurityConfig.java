package ua.zxz.multydbsysytem.config.security;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.zxz.multydbsysytem.config.JwtUserProvider;
import ua.zxz.multydbsysytem.repository.UserRepository;
import ua.zxz.multydbsysytem.web.filter.DbTokenRequestFilter;
import ua.zxz.multydbsysytem.web.filter.JwtUserRequestFilter;

import java.util.Collections;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {};

    private final UserRepository userRepository;
    private final JwtUserRequestFilter jwtUserRequestFilter;
    private final DbTokenRequestFilter dbTokenRequestFilter;
    private final DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint;

    public SecurityConfig(UserRepository userRepository,
                          JwtUserRequestFilter jwtUserRequestFilter,
                          DbTokenRequestFilter dbTokenRequestFilter,
                          @Qualifier("delegatedAuthenticationEntryPoint")
                          DelegatedAuthenticationEntryPoint entryPoint) {
        this.userRepository = userRepository;
        this.jwtUserRequestFilter = jwtUserRequestFilter;
        this.dbTokenRequestFilter = dbTokenRequestFilter;
        this.delegatedAuthenticationEntryPoint = entryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUserProvider jwtUserProvider() {
        return new JwtUserProvider(userRepository, passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(jwtUserProvider()));
    }

    @Bean
    @Order(1)
    public SecurityFilterChain queriesFilterChain(final HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/queries/**")
                .authorizeHttpRequests(r -> r
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(dbTokenRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> c.authenticationEntryPoint(delegatedAuthenticationEntryPoint))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(final HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authorizeHttpRequests(r -> r
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(POST, "/auth/login", "/auth/register", "/auth/restore-password").permitAll()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(GET,
                                "/constraints",
                                "/fieldTypes",
                                "/dbTokenLifeTimes",
                                "/auth/validate-token").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtUserRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> c.authenticationEntryPoint(delegatedAuthenticationEntryPoint))
                .build();
    }
}
