package com.cartradevn.cartradevn.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cartradevn.cartradevn.administration.Enum.UserRole;
import com.cartradevn.cartradevn.administration.controller.UserResponseDTO;
import com.cartradevn.cartradevn.administration.dto.RegisterDTO;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;
import com.cartradevn.cartradevn.administration.services.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index-9", "/faq", 
                               "/login", "/register", 
                               "/static/**", "/css/**", "/js/**", 
                               "/images/**", "/fonts/**","/edit-listings/**").permitAll()
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

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Add registration handler
    public String handleRegistration(RegisterDTO registerDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            // Check if username or email already exists
            if (userRepo.existsByUsername(registerDTO.getUsername())) {
                response.sendRedirect("/register?error=Username đã tồn tại");
                return null;
            }
            if (userRepo.existsByEmail(registerDTO.getEmail())) {
                response.sendRedirect("/register?error=Email đã tồn tại");
                return null;
            }

            // Create new user
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setEmail(registerDTO.getEmail());
            user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
            user.setRole(registerDTO.getRole());
            user.setCreatedAt(System.currentTimeMillis());

            // Save user
            userRepo.save(user);

            // Redirect to login page with success message
            response.sendRedirect("/login?registered=true");
            return null;
        } catch (Exception e) {
            try {
                response.sendRedirect("/register?error=" + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
