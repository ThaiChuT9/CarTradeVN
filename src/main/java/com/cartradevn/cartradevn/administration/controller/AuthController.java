package com.cartradevn.cartradevn.administration.controller;

import java.lang.ProcessBuilder.Redirect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cartradevn.cartradevn.administration.Enum.UserRole;
import com.cartradevn.cartradevn.administration.dto.LoginDTO;
import com.cartradevn.cartradevn.administration.dto.RegisterDTO;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.services.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showFormLogin() {
        return "login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO,
                           RedirectAttributes redirectAttributes) {
        try {
            authService.register(registerDTO);
            redirectAttributes.addFlashAttribute("S", "Đăng ký thành công");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // @PostMapping("/login")
    // public String login(@RequestParam String username,
    //                     @RequestParam String password, HttpSession session,
    //                     RedirectAttributes redirectAttributes) {
    //     try {
    //         UserResponseDTO userDto = authService.login(new LoginDTO(username, password));
    //         session.setAttribute("user", userDto);
    //         // Redirect base on role
    //         if (UserRole.valueOf(userDto.getRole()) == UserRole.ADMIN) {
    //             return "redirect:/dashboard";
    //         } else {
    //             return "redirect:/index-9";
    //         }
    //     } catch (RuntimeException e) {
    //         redirectAttributes.addFlashAttribute("E", e.getMessage());
    //         return "redirect:/login";
    //     }

    // }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session to log out the user
        return "redirect:/login"; // Redirect to the login page after logout
    }
}
