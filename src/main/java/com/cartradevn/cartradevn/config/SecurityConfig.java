package com.cartradevn.cartradevn.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cartradevn.cartradevn.administration.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index-9", "/faq", 
                               "/login", "/register", 
                               "../static/**", "../static/css/**", "/js/**", 
                               "../static/images/**", "/fonts/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/admin-dashboard/**", "/users-list/**",
                               "/admin-profile/**", "/users/edit/**",
                               "/users/update/**", "/view-listings/**")
                .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/add-listings/**", "/my-listings/**")
                .hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers("/dashboard/**", "/profile/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER", "ROLE_BUYER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/v1/auth/login")
                .successHandler((request, response, authentication) -> {
                    // Store user details in session
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    User user = userRepo.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                    
                    UserResponseDTO userDto = new UserResponseDTO();
                    userDto.setId(user.getId());
                    userDto.setUsername(user.getUsername());
                    userDto.setRole(user.getRole().name());
                    
                    HttpSession session = request.getSession();
                    session.setAttribute("user", userDto);
                    
                    // Debug logs
                    System.out.println("Login successful: " + userDto.getUsername());
                    System.out.println("Session ID: " + session.getId());
                    System.out.println("User role: " + userDto.getRole());
                    
                    // Redirect based on role
                    if (user.getRole() == UserRole.ADMIN) {
                        response.sendRedirect("/admin-dashboard");
                    } else {
                        response.sendRedirect("/dashboard");
                    }
                })
                .failureHandler((request, response, exception) -> {
                    System.out.println("Login failed: " + exception.getMessage());
                    response.sendRedirect("/login?error=true");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")  // URL for logout
                .logoutSuccessUrl("/login?logout=true")  // Redirect after logout
                .invalidateHttpSession(true)  // Invalidate session
                .deleteCookies("JSESSIONID")  // Clear cookies
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

=======
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/static/**",
                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                "/api/v1/auth/**", "/index-9", "/faq")
                        .permitAll()
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN, SELLER, BUYER")
                        .requestMatchers("/admin-dashboard/**", "/users-list/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/v1/auth/login")
                        .successHandler((request, response, authentication) -> {
                            // Lấy authorities của user
                            Set<String> roles = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toSet());
                            
                            // Chuyển hướng dựa trên role
                            if (roles.contains("ROLE_ADMIN")) {
                                response.sendRedirect("/admin-dashboard");
                            } else {
                                response.sendRedirect("/index-9");
                            }
                        }))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false));
>>>>>>> parent of 2f6f795 (Merge pull request #6 from ThaiChuT9/thai_dev)
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
