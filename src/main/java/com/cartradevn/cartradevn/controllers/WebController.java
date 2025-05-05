package com.cartradevn.cartradevn.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cartradevn.cartradevn.administration.Enum.UserRole;
import com.cartradevn.cartradevn.administration.controller.UserResponseDTO;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping({"/", "/index-9"})
    public String index(HttpSession session) {
        return "index-9";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        // Nếu đã đăng nhập thì redirect về trang chủ
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        return "dashboard";
    }
    @GetMapping("/admin-dashboard")
    public String admindashboard(HttpSession session) {
        return "admin-dashboard";
    }
    
    @GetMapping("/users-list")
    public String listUsers(Model model, 
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {
    
    PageRequest pageable = PageRequest.of(page, size);
    Page<User> userPage;
    
    if (search != null && !search.isEmpty()) {
        userPage = userRepo.findByUsernameOrEmail(search, search, pageable);
    } else {
        userPage = userRepo.findAll(pageable);
    }
    
    model.addAttribute("users", userPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userPage.getTotalPages());
    model.addAttribute("totalItems", userPage.getTotalElements());
    model.addAttribute("search", search);
    
    return "users-list";
    }
}
