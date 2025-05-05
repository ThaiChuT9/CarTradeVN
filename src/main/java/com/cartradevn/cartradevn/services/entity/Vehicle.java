package com.cartradevn.cartradevn.services.entity;

import java.time.LocalDateTime;
import com.cartradevn.cartradevn.administration.entity.User;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Integer mileage;  // Add mileage field

    @Column(name = "transmission", nullable = false)
    private String transmission;  // Add transmission field (Auto/Manual)

    @Column(name = "body_style", nullable = false)
    private String bodyStyle;  // Add body style field (Sedan/SUV/etc)

    @Column(name = "vehicle_condition", nullable = false)
    private String condition;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType; // Petrol/Electric/...

    @Column(name = "engine_size", nullable = false)
    private Double engineSize; // Engine size in liters

    @Column(nullable = false)
    private Integer doors; // Number of doors

    @Column(nullable = false)
    private Integer cylinders; // Number of cylinders

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String city;

    @Column(length = 1000)
    private String description;

    private String imageUrl; // URL to the vehicle image

    @Column(nullable = false)
    private String status; // pending/approved/sold

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
