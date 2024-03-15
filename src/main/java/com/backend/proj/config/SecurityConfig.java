package com.backend.proj.config;

import com.backend.proj.filters.JwtAuthFilter;
import com.backend.proj.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .cors()
                .and()
                .exceptionHandling()
                /**
                 * @description error for unauthorized access
                 */
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .authorizeRequests()
                /**
                 * @description methods to access with authorization
                 */
                .antMatchers(
                        "/api/v1/admin/**",
                        "/api/v1/auth/**",
                        "/api/v1/users/register",
                        "/api/v1/users/account/verify",
                        "/api/v1/suggestions/send_idea",
                        "/api/v1/problems/create",
                        "/ap/v1/leaders/**",
                        "/ap/v1/leaders/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/h2-console/**",
                        "/swagger-ui/index.html"
                )
                .permitAll()
                /**
                 * @description authorize all others
                 */
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
