package com.cartradevn.cartradevn.services.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cartradevn.cartradevn.administration.controller.UserResponseDTO;
import com.cartradevn.cartradevn.services.VehicleException;
import com.cartradevn.cartradevn.services.dto.VehicleDTO;
import com.cartradevn.cartradevn.services.service.ImageService;
import com.cartradevn.cartradevn.services.service.VehicleService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class VehicleController {
    private final VehicleService vehicleService;
    private final ImageService imageService;

    @Autowired
    public VehicleController(VehicleService vehicleService, ImageService imageService) {
        this.vehicleService = vehicleService;
        this.imageService = imageService;
    }

    // Endpoint /api/cars để trả về danh sách tất cả xe
    @GetMapping("/cars")
    public ResponseEntity<List<VehicleDTO>> getAllVehiclesForFrontend() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        // Additional validation for new fields
        if (vehicleDTO.getMileage() != null && vehicleDTO.getMileage() < 0) {
            throw new VehicleException("Mileage cannot be negative");
        }
        
        if (vehicleDTO.getTransmission() != null) {
            String trans = vehicleDTO.getTransmission().toLowerCase();
            if (!trans.matches("automatic|manual|cvt|semi-automatic")) {
                throw new VehicleException("Invalid transmission type");
            }
        }
        
        if (vehicleDTO.getBodyStyle() != null) {
            String style = vehicleDTO.getBodyStyle().toLowerCase();
            if (!style.matches("sedan|suv|hatchback|coupe|convertible|wagon|van|truck")) {
                throw new VehicleException("Invalid body style");
            }
        }

        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDTO));
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vehicles/all")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/vehicles/search")
    public ResponseEntity<List<VehicleDTO>> getVehicles(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Integer mileage,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String bodyStyle) {
        return ResponseEntity.ok(vehicleService.getVehicles(
                city, brand, name, year, model, minPrice, maxPrice, 
                condition, fuelType, mileage, transmission, bodyStyle));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDTO> createVehicle(
            @Valid @ModelAttribute VehicleDTO vehicleDTO,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            HttpSession session) {
        try {
            // Get logged in user
            UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");
            if (user == null) {
                throw new VehicleException("User must be logged in to add vehicle");
            }

            // Set user ID
            vehicleDTO.setUserId(user.getId());

            // Additional validation for new fields
            if (vehicleDTO.getMileage() != null && vehicleDTO.getMileage() < 0) {
                throw new VehicleException("Mileage cannot be negative");
            }

            if (vehicleDTO.getTransmission() != null) {
                String trans = vehicleDTO.getTransmission().toLowerCase();
                if (!trans.matches("automatic|manual|cvt|semi-automatic")) {
                    throw new VehicleException("Invalid transmission type");
                }
            }

            if (vehicleDTO.getBodyStyle() != null) {
                String style = vehicleDTO.getBodyStyle().toLowerCase();
                if (!style.matches("sedan|suv|hatchback|coupe|convertible|wagon|van|truck")) {
                    throw new VehicleException("Invalid body style");
                }
            }

            // Handle image uploads
            if (images != null && images.length > 0) {
                List<String> imageUrls = imageService.saveImages(images);
                vehicleDTO.setImageUrl(String.join(",", imageUrls));
            }

            VehicleDTO created = vehicleService.createVehicle(vehicleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new VehicleException("Error creating vehicle: " + e.getMessage());
        }
    }
}