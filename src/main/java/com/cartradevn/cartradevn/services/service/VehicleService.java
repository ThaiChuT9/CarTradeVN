package com.cartradevn.cartradevn.services.service;

import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;
import com.cartradevn.cartradevn.services.VehicleException;
import com.cartradevn.cartradevn.services.dto.VehicleDTO;
import com.cartradevn.cartradevn.services.entity.Vehicle;
import com.cartradevn.cartradevn.services.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final VehicleRepo vehicleRepo;
    private final UserRepo userRepo;

    @Autowired
    public VehicleService(VehicleRepo vehicleRepo, UserRepo userRepo) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    // Lấy danh sách tất cả xe
    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        return vehicles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy danh sách xe với bộ lọc
    public List<VehicleDTO> getVehicles(String city, String brand, String name, Integer year, String model, Double minPrice, Double maxPrice,
            String condition, String fuelType, Integer mileage, String transmission, String bodyStyle) {
        List<Vehicle> vehicles = vehicleRepo.findAll();

        // Áp dụng bộ lọc
        if (name != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (year != null) {
            vehicles = vehicles.stream().filter(v -> v.getYear().equals(year)).collect(Collectors.toList());
        }
        if (city != null) {
            vehicles = vehicles.stream().filter(v -> v.getCity().equalsIgnoreCase(city)).collect(Collectors.toList());
        }
        if (brand != null && model != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getBrand().equalsIgnoreCase(brand) && v.getModel().equalsIgnoreCase(model))
                    .collect(Collectors.toList());
        }
        if (minPrice != null && maxPrice != null) {
            vehicles = vehicles.stream().filter(v -> v.getPrice() >= minPrice && v.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }
        if (condition != null) {
            vehicles = vehicles.stream().filter(v -> v.getCondition().equalsIgnoreCase(condition))
                    .collect(Collectors.toList());
        }
        if (fuelType != null) {
            vehicles = vehicles.stream().filter(v -> v.getFuelType().equalsIgnoreCase(fuelType))
                    .collect(Collectors.toList());
        }
        if (mileage != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getMileage() <= mileage)
                    .collect(Collectors.toList());
        }
        if (transmission != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getTransmission().equalsIgnoreCase(transmission))
                    .collect(Collectors.toList());
        }
        if (bodyStyle != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getBodyStyle().equalsIgnoreCase(bodyStyle))
                    .collect(Collectors.toList());
        }
        return vehicles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Tạo bài đăng bán xe
    public VehicleDTO createVehicle(@Valid VehicleDTO vehicleDTO) {
        try {
            if (vehicleDTO == null) {
                throw new VehicleException.ValidationException("Vehicle data không được null");
            }
            // Tìm user
            User user = userRepo.findById(vehicleDTO.getUserId())
                .orElseThrow(() -> new VehicleException("User not found with id: " + vehicleDTO.getUserId()));
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(vehicleDTO.getBrand());
            vehicle.setModel(vehicleDTO.getModel());
            vehicle.setName(vehicleDTO.getName()); 
            vehicle.setYear(vehicleDTO.getYear());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setCondition(vehicleDTO.getCondition());
            vehicle.setFuelType(vehicleDTO.getFuelType());
            vehicle.setPrice(vehicleDTO.getPrice());
            vehicle.setCity(vehicleDTO.getCity());
            vehicle.setDescription(vehicleDTO.getDescription());
            vehicle.setImageUrl(vehicleDTO.getImageUrl());
            vehicle.setMileage(vehicleDTO.getMileage());
            vehicle.setTransmission(vehicleDTO.getTransmission());
            vehicle.setBodyStyle(vehicleDTO.getBodyStyle());
            vehicle.setEngineSize(vehicleDTO.getEngineSize());
            vehicle.setDoors(vehicleDTO.getDoors());
            vehicle.setCylinders(vehicleDTO.getCylinders());
            vehicle.setStatus("pending");
            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUser(user);
            Vehicle savedVehicle = vehicleRepo.save(vehicle);
            return convertToDTO(savedVehicle);
        } catch (Exception e) {
            throw new VehicleException("Lỗi khi tạo xe: " + e.getMessage());
        }
    }

    // Chuyển đổi từ Entity sang DTO
    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(vehicle.getId());
        vehicleDTO.setUserId(vehicle.getUser().getId());
        vehicleDTO.setBrand(vehicle.getBrand());
        vehicleDTO.setModel(vehicle.getModel());
        vehicleDTO.setName(vehicle.getName()); 
        vehicleDTO.setYear(vehicle.getYear());
        vehicleDTO.setColor(vehicle.getColor());
        vehicleDTO.setCondition(vehicle.getCondition());
        vehicleDTO.setFuelType(vehicle.getFuelType());
        vehicleDTO.setPrice(vehicle.getPrice());
        vehicleDTO.setCity(vehicle.getCity());
        vehicleDTO.setDescription(vehicle.getDescription());
        vehicleDTO.setImageUrl(vehicle.getImageUrl());
        vehicleDTO.setMileage(vehicle.getMileage());
        vehicleDTO.setTransmission(vehicle.getTransmission());
        vehicleDTO.setBodyStyle(vehicle.getBodyStyle());
        vehicleDTO.setEngineSize(vehicle.getEngineSize());
        vehicleDTO.setDoors(vehicle.getDoors());
        vehicleDTO.setCylinders(vehicle.getCylinders());
        vehicleDTO.setStatus(vehicle.getStatus());
        if (vehicle.getCreatedAt() != null) {
            vehicleDTO.setCreatedAt(vehicle.getCreatedAt().toString());
        } else {
            vehicleDTO.setCreatedAt(null);
        }
        return vehicleDTO;
    }

    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow(() -> new VehicleException("Không tìm thấy xe với id: " + id));
        return convertToDTO(vehicle);
    }

    public VehicleDTO updateVehicle(Long id, @Valid VehicleDTO vehicleDTO) {
        try {
            Vehicle existingVehicle = vehicleRepo.findById(id)
                    .orElseThrow(() -> new VehicleException("Không tìm thấy xe với id: " + id));

            existingVehicle.setBrand(vehicleDTO.getBrand());
            existingVehicle.setModel(vehicleDTO.getModel());
            existingVehicle.setName(vehicleDTO.getName());
            existingVehicle.setYear(vehicleDTO.getYear());
            existingVehicle.setColor(vehicleDTO.getColor());
            existingVehicle.setCondition(vehicleDTO.getCondition());
            existingVehicle.setFuelType(vehicleDTO.getFuelType());
            existingVehicle.setPrice(vehicleDTO.getPrice());
            existingVehicle.setCity(vehicleDTO.getCity());
            existingVehicle.setDescription(vehicleDTO.getDescription());
            existingVehicle.setImageUrl(vehicleDTO.getImageUrl());
            existingVehicle.setMileage(vehicleDTO.getMileage());
            existingVehicle.setTransmission(vehicleDTO.getTransmission());
            existingVehicle.setBodyStyle(vehicleDTO.getBodyStyle());
            existingVehicle.setEngineSize(vehicleDTO.getEngineSize());
            existingVehicle.setDoors(vehicleDTO.getDoors());
            existingVehicle.setCylinders(vehicleDTO.getCylinders());

            Vehicle updatedVehicle = vehicleRepo.save(existingVehicle);
            return convertToDTO(updatedVehicle);
        } catch (Exception e) {
            throw new VehicleException("Lỗi khi cập nhật xe: " + e.getMessage());
        }
    }

    public void deleteVehicle(Long id) {
        try {
            if (!vehicleRepo.existsById(id)) {
                throw new VehicleException("Không tìm thấy xe với id: " + id);
            }
            vehicleRepo.deleteById(id);
        } catch (Exception e) {
            throw new VehicleException("Lỗi khi xóa xe: " + e.getMessage());
        }
    }

    public Page<VehicleDTO> getVehiclesByUserId(Long id, PageRequest pageRequest) {
        try {
            // Check if user exists
            User user = userRepo.findById(id)
                .orElseThrow(() -> new VehicleException("User not found with id: " + id));
    
            // Get paginated vehicles for user
            Page<Vehicle> vehiclePage = vehicleRepo.findByUser(user, pageRequest);
    
            // Convert to DTOs
            return vehiclePage.map(this::convertToDTO);
        } catch (Exception e) {
            throw new VehicleException("Error getting vehicles for user: " + e.getMessage());
        }
    }

    public Page<VehicleDTO> getAllVehiclesPaged(Pageable pageable) {
        Page<Vehicle> vehiclePage = vehicleRepo.findAll(pageable);
        return vehiclePage.map(this::convertToDTO);
    }

    public Page<VehicleDTO> searchVehicles(String searchTerm, Pageable pageable) {
        try {
            Page<Vehicle> vehicles = vehicleRepo
                .findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                    searchTerm, 
                    searchTerm, 
                    pageable
                );
            return vehicles.map(this::convertToDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error searching vehicles: " + e.getMessage());
        }
    }
}