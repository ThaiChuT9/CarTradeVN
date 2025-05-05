package com.cartradevn.cartradevn.services.service;

import com.cartradevn.cartradevn.administration.entity.User;
import com.cartradevn.cartradevn.administration.respository.UserRepo;
import com.cartradevn.cartradevn.services.VehicleException;
import com.cartradevn.cartradevn.services.dto.VehicleDTO;
import com.cartradevn.cartradevn.services.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
            String condition, String fuelType) {
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
}
