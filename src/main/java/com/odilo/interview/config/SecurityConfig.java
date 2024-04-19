package com.odilo.interview.config;

import com.odilo.interview.auth.UserAuthDetails;
import com.odilo.interview.filter.JwtAuthenticationFilter;
import com.odilo.interview.filter.UriMatcherUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private UserAuthDetails userAuthDetails;
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(UriMatcherUtil.URI_MATCHER).permitAll();
            request.requestMatchers("/odilo/api/users/**").hasRole("USER");
            request.requestMatchers("/odilo/api/admin/**").hasRole("ADMIN");
            request.anyRequest().authenticated();
        });

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPointHandler));
        http.exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler));
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(this.userAuthDetails).passwordEncoder(passwordEncoder);
    }
}
