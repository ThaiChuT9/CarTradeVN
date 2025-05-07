package com.cartradevn.cartradevn.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cartradevn.cartradevn.administration.Enum.UserRole;
import com.cartradevn.cartradevn.administration.controller.UserResponseDTO;
import com.cartradevn.cartradevn.administration.dto.UserUpdateDTO;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;
import com.cartradevn.cartradevn.services.dto.VehicleDTO;
import com.cartradevn.cartradevn.services.repository.VehicleRepo;
import com.cartradevn.cartradevn.services.service.VehicleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final VehicleRepo vehicleRepo;
    private final VehicleService vehicleService;

    @Autowired
    public WebController(UserRepo userRepo, PasswordEncoder passwordEncoder, VehicleRepo vehicleRepo, VehicleService vehicleService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.vehicleRepo = vehicleRepo;
        this.vehicleService = vehicleService;
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
    public String admindashboard(Model model, HttpSession session) {
        // Đếm số lượng users
        long userCount = userRepo.count();
        model.addAttribute("userCount", userCount);
        // Đếm số lượng xe
        long vehicleCount = vehicleRepo.count();
        model.addAttribute("vehicleCount", vehicleCount);
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

    @GetMapping("/admin-profile")
    public String profile(HttpSession session, Model model) {
        return "redirect:/login";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("editUser", user);
            return "admin-profile";
        } catch (Exception e) {
            return "redirect:/users-list?error=" + e.getMessage();
        }
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User updateUser,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            user.setUsername(updateUser.getUsername());
            user.setEmail(updateUser.getEmail());
            user.setRole(updateUser.getRole());
            userRepo.save(user);
            redirectAttributes.addFlashAttribute("S", "Cập nhật thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("E", e.getMessage());
        }
        return "redirect:/users-list";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        try {
            UserResponseDTO userSession = (UserResponseDTO) session.getAttribute("user");

            User user = userRepo.findById(userSession.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Add debug logging
            System.out.println("Found user: " + user.getUsername());

            model.addAttribute("user", user);
            return "profile";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserUpdateDTO userUpdateDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            UserResponseDTO userSession = (UserResponseDTO) session.getAttribute("user");
            User user = userRepo.findById(userSession.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate password if it's being changed
            if (!userUpdateDTO.getNewPassword().isEmpty()) {
                if (!passwordEncoder.matches(userUpdateDTO.getCurrentPassword(), user.getPasswordHash())) {
                    throw new RuntimeException("Current password is incorrect");
                }
                if (!userUpdateDTO.getNewPassword().equals(userUpdateDTO.getConfirmPassword())) {
                    throw new RuntimeException("New passwords don't match");
                }
                user.setPasswordHash(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
            }

            user.setUsername(userUpdateDTO.getUsername());
            user.setEmail(userUpdateDTO.getEmail());

            userRepo.save(user);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping({"/", "/index-9"})
    public String index(Model model) {
        try {
            // Lấy danh sách xe mới
            Page<VehicleDTO> newVehicles = vehicleService.getVehiclesByCondition(
                "new", 
                PageRequest.of(0, 8)
            );
            model.addAttribute("newVehicles", newVehicles.getContent());

            // Lấy danh sách xe đã qua sử dụng
            Page<VehicleDTO> usedVehicles = vehicleService.getVehiclesByCondition(
                "used", 
                PageRequest.of(0, 8)
            );
            model.addAttribute("usedVehicles", usedVehicles.getContent());

            // Lấy danh sách xe theo từng loại body style
            Map<String, List<VehicleDTO>> vehiclesByStyle = new HashMap<>();
            String[] styles = {"TRUCK", "SEDAN", "COUPE", "CONVERTIBLE", "SUV", 
                               "VAN", "HATCHBACK", "WAGON"};
            
            for (String style : styles) {
                List<VehicleDTO> vehicles = vehicleService.getVehiclesByBodyStyle(style);
                vehiclesByStyle.put(style.toLowerCase(), vehicles);
            }
            model.addAttribute("vehiclesByStyle", vehiclesByStyle);

            // Thống kê
            long totalVehicles = vehicleService.countTotalVehicles();
            model.addAttribute("totalVehicles", totalVehicles);

            return "index-9";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading vehicles: " + e.getMessage());
            return "index-9";
        }
    }

    @GetMapping("/add-listings")
    public String showAddListingForm(Model model, HttpSession session) {
        UserResponseDTO userSession = (UserResponseDTO) session.getAttribute("user");
        return "add-listings";
    }

    @PostMapping("/add-listings")
    public String addListing(
            @ModelAttribute VehicleDTO vehicleDTO,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            // Get logged in user
            UserResponseDTO userSession = (UserResponseDTO) session.getAttribute("user");

            // Set user ID
            vehicleDTO.setUserId(userSession.getId());

            // Create vehicle
            VehicleDTO created = vehicleService.createVehicle(vehicleDTO);
            redirectAttributes.addFlashAttribute("success", "Vehicle added successfully");
            return "redirect:/my-listings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/add-listings";
        }
    }

    @GetMapping("/my-listings")
    public String myListings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            UserResponseDTO userSession = (UserResponseDTO) session.getAttribute("user");
            if (userSession == null) {
                redirectAttributes.addFlashAttribute("error", "Please login to view your listings");
                return "redirect:/login";
            }

            Page<VehicleDTO> vehiclePage = vehicleService.getVehiclesByUserId(
                userSession.getId(), 
                PageRequest.of(0, 10)
            );

            if (vehiclePage.isEmpty()) {
                model.addAttribute("info", "You don't have any listings yet");
            }

            model.addAttribute("vehicles", vehiclePage.getContent());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", vehiclePage.getTotalPages());
            model.addAttribute("totalItems", vehiclePage.getTotalElements());

            return "my-listings";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid request: " + e.getMessage());
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/view-listings")
    public String viewListings(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        try {
            Page<VehicleDTO> vehiclePage;
            if (search != null && !search.trim().isEmpty()) {
                vehiclePage = vehicleService.searchVehicles(search, PageRequest.of(page, size));
            } else {
                vehiclePage = vehicleService.getAllVehiclesPaged(PageRequest.of(page, size));
            }

            model.addAttribute("vehicles", vehiclePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", vehiclePage.getTotalPages());
            model.addAttribute("totalItems", vehiclePage.getTotalElements());
            model.addAttribute("search", search);

            return "view-listings";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "view-listings";
        }
    }

    @GetMapping("/vehicles/edit/{id}")
    public String editVehicle(@PathVariable Long id, Model model) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleById(id);
            model.addAttribute("vehicle", vehicle);
            return "edit-listings";
        } catch (Exception e) {
            return "redirect:/view-listings?error=" + e.getMessage();
        }
    }

    @PostMapping("/vehicles/update/{id}")
    public String updateVehicle(@PathVariable Long id, 
                              @ModelAttribute VehicleDTO vehicleDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            vehicleService.updateVehicle(id, vehicleDTO);
            redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully");
            return "redirect:/view-listings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vehicles/edit/" + id;
        }
    }

    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("success", "Vehicle deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/view-listings";
    }

    @GetMapping("/vehicles/{id}")
    public String viewVehicleDetails(@PathVariable Long id, Model model) {
        try {
            VehicleDTO vehicle = vehicleService.getVehicleById(id);
            model.addAttribute("vehicle", vehicle);

            // Get seller information
            User seller = userRepo.findById(vehicle.getUserId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
            model.addAttribute("seller", seller);

            // Get related vehicles (same brand or category)
            List<VehicleDTO> relatedVehicles = vehicleService.getRelatedVehicles(
                vehicle.getBrand(), 
                vehicle.getBodyStyle(),
                id,  // exclude current vehicle
                4    // limit
            );
            model.addAttribute("relatedVehicles", relatedVehicles);

            return "inventory-page-single-v2";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }
}