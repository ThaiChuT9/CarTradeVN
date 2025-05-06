package com.cartradevn.cartradevn.services.service;

import com.cartradevn.cartradevn.administration.Enum.UserRole;
import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;
import com.cartradevn.cartradevn.services.VehicleException;
import com.cartradevn.cartradevn.services.dto.VehicleDTO;
import com.cartradevn.cartradevn.services.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.cartradevn.cartradevn.services.repository.VehicleRepo;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final VehicleRepo vehicleRepo;
    private final UserRepo userRepo;

    @Autowired
    private VehicleService(VehicleRepo vehicleRepo, UserRepo userRepo) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    // Lấy danh sách tất cả xe
    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        // chuyển đổi danh sách xe thành danh sách DTO
        return vehicles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy danh sách xe với bộ lọc
    public List<VehicleDTO> getVehicles(String city, String brand, String name, Integer year, String model, Double minPrice, Double maxPrice,
            String condition, String fuelType, Integer mileage, String transmission, String bodyStyle) {
        List<Vehicle> vehicles = vehicleRepo.findAll();

        // áp dụng bộ lọc
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
        // chuyển đổi danh sách xe thành danh sách DTO
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
            vehicle.setImageUrl(vehicleDTO.getImageUrl()); // Set image URL if provided
            vehicle.setMileage(vehicleDTO.getMileage());
            vehicle.setTransmission(vehicleDTO.getTransmission());
            vehicle.setBodyStyle(vehicleDTO.getBodyStyle());
            vehicle.setEngineSize(vehicleDTO.getEngineSize());
            vehicle.setDoors(vehicleDTO.getDoors());
            vehicle.setCylinders(vehicleDTO.getCylinders());
            vehicle.setStatus("pending"); // Trạng thái mặc định là 'pending'
            vehicle.setCreatedAt(LocalDateTime.now()); // Set createdAt to current time
            vehicle.setUser(user); // Set user
            // Lưu xe vào cơ sở dữ liệu
            Vehicle savedVehicle = vehicleRepo.save(vehicle);
            // Chuyển đổi xe đã lưu thành DTO
            return convertToDTO(savedVehicle);
        } catch (DataIntegrityViolationException e) {
            throw new VehicleException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new VehicleException("Lỗi khi tạo xe: " + e.getMessage());
        }
    }

    // Chuyển đổi từ Entity sang DTO
    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(vehicle.getId());
        vehicleDTO.setUserId(vehicle.getUser().getId()); // Lấy ID của người dùng từ đối tượng Vehicle
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
        vehicleDTO.setImageUrl(vehicle.getImageUrl()); // Lấy URL hình ảnh từ đối tượng Vehicle
        vehicleDTO.setMileage(vehicle.getMileage());
        vehicleDTO.setTransmission(vehicle.getTransmission());
        vehicleDTO.setBodyStyle(vehicle.getBodyStyle());
        vehicleDTO.setEngineSize(vehicle.getEngineSize());
        vehicleDTO.setDoors(vehicle.getDoors());
        vehicleDTO.setCylinders(vehicle.getCylinders());
        vehicleDTO.setStatus(vehicle.getStatus());
        if (vehicle.getCreatedAt() != null) {
            vehicleDTO.setCreatedAt(vehicle.getCreatedAt().toString()); // Chuyển đổi LocalDateTime thành String
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

            // Cập nhật thông tin xe
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
            existingVehicle.setImageUrl(vehicleDTO.getImageUrl()); // Cập nhật URL hình ảnh nếu có
            existingVehicle.setMileage(vehicleDTO.getMileage());
            existingVehicle.setTransmission(vehicleDTO.getTransmission());
            existingVehicle.setBodyStyle(vehicleDTO.getBodyStyle());
            existingVehicle.setEngineSize(vehicleDTO.getEngineSize());
            existingVehicle.setDoors(vehicleDTO.getDoors());
            existingVehicle.setCylinders(vehicleDTO.getCylinders());
            // Không cập nhật status và createdAt vì đây là thông tin hệ thống

            Vehicle updatedVehicle = vehicleRepo.save(existingVehicle);
            return convertToDTO(updatedVehicle);
        } catch (DataIntegrityViolationException e) {
            throw new VehicleException("Lỗi dữ liệu khi cập nhật xe: " + e.getMessage());
        } catch (VehicleException e) {
            throw e;
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
        } catch (DataIntegrityViolationException e) {
            throw new VehicleException("Không thể xóa xe do ràng buộc dữ liệu");
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

    public Page<VehicleDTO> getVehiclesByCondition(String condition, Pageable pageable) {
        return vehicleRepo.findByCondition(condition, pageable)
                .map(this::convertToDTO);
    }

    public List<VehicleDTO> getVehiclesByBodyStyle(String bodyStyle) {
        return vehicleRepo.findByBodyStyle(bodyStyle).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long countTotalVehicles() {
        return vehicleRepo.count();
    }

    public List<VehicleDTO> getRelatedVehicles(String brand, String bodyStyle, Long excludeId, int limit) {
        List<Vehicle> vehicles = vehicleRepo.findRelatedVehicles(brand, bodyStyle, excludeId, PageRequest.of(0, limit));
        return vehicles.stream()
                      .map(this::convertToDTO)
                      .collect(Collectors.toList());
    }
}
