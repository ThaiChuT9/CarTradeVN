package com.cartradevn.cartradevn.services.repository;

import org.springframework.stereotype.Repository;

import com.cartradevn.cartradevn.services.entity.Vehicle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
    // Tìm kiếm xe theo ID
    // Sử dụng Optional để tránh NullPointerException
    // Thay đổi findById để trả về Optional
    Optional<Vehicle> findById(Long id);
    
    // Thêm phương thức tìm kiếm với nhiều tiêu chí
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:city IS NULL OR v.city = :city) AND " +
           "(:brand IS NULL OR v.brand = :brand) AND " +
           "(:model IS NULL OR v.model = :model) AND " +
           "(:name IS NULL OR v.name = :name) AND " +
           "(:year IS NULL OR v.year = :year) AND " +
           "(:minPrice IS NULL OR v.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.price <= :maxPrice) AND " +
           "(:condition IS NULL OR v.condition = :condition) AND " +
           "(:fuelType IS NULL OR v.fuelType = :fuelType)")
    Page<Vehicle> findVehiclesByFilters(
        @Param("city") String city,
        @Param("brand") String brand,
        @Param("model") String model,
        @Param("name") String name,
        @Param("year") Integer year,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("condition") String condition,
        @Param("fuelType") String fuelType,
        Pageable pageable
    );
    
    // Các phương thức tìm kiếm cơ bản
    List<Vehicle> findByCity(String city);
    List<Vehicle> findByName(String name);
    List<Vehicle> findByNameContainingIgnoreCase(String name);
    List<Vehicle> findByBrandAndNameContainingIgnoreCase(String brand, String name);
    List<Vehicle> findByYear(Integer year);
    List<Vehicle> findByBrandAndModel(String brand, String model);
    List<Vehicle> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Vehicle> findByCondition(String condition);
    List<Vehicle> findByFuelType(String fuelType);
    
    // Thêm các phương thức tìm kiếm mới
    Page<Vehicle> findAll(Pageable pageable);
    List<Vehicle> findByStatus(String status);
    
    // Tìm kiếm theo nhiều tiêu chí kết hợp
    List<Vehicle> findByBrandAndPriceBetween(String brand, Double minPrice, Double maxPrice);
    List<Vehicle> findByCityAndCondition(String city, String condition);
}
