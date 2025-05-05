package com.cartradevn.cartradevn.services.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private Long userId; 
    private String brand;
    private String model;
    private String name;
    private Integer year;
    private String color;
    private Integer mileage;
    private String transmission;
    private String bodyStyle;
    private Double engineSize;
    private Integer doors;
    private Integer cylinders;
    private String condition; // Corrected spelling
    private String fuelType; // Petrol/Electric/... 
    private Double price;
    private String city;
    private String description;
    private String imageUrl; // URL to the vehicle image
    private String status; // pending/approved/sold
    private String createdAt; // LocalDateTime as String for simplicity in DTO

    //Validation
    @AssertTrue(message = "Brand không được để trống")
    public boolean isBrandValid() {
        return brand != null && !brand.trim().isEmpty();
    }

    @AssertTrue(message = "Model không được để trống")
    public boolean isModelValid() {
        return model != null && !model.trim().isEmpty();
    }

    @AssertTrue(message = "Name không được để trống")
    public boolean isNameValid() {
        return name != null && !name.trim().isEmpty();
    }

    @AssertTrue(message = "Giá xe phải lớn hơn 0")
    public boolean isPriceValid() {
        return price != null && price > 0;
    }

    @AssertTrue(message = "City không được để trống")
    public boolean isCityValid() {
        return city != null && !city.trim().isEmpty();
    }

    @AssertTrue(message = "Condition chỉ được là 'new' hoặc 'used'")
    public boolean isConditionValid() {
        if (condition == null) return true; // optional field
        return condition.equalsIgnoreCase("new") || 
               condition.equalsIgnoreCase("used");
    }

    @AssertTrue(message = "FuelType không hợp lệ")
    public boolean isFuelTypeValid() {
        if (fuelType == null) return true; // optional field
        String fuel = fuelType.toLowerCase();
        return fuel.equals("petrol") || 
               fuel.equals("diesel") || 
               fuel.equals("electric") || 
               fuel.equals("hybrid");
    }

    @AssertTrue(message = "Số km đã đi phải lớn hơn hoặc bằng 0")
    public boolean isMileageValid() {
        return mileage != null && mileage >= 0;
    }

    @AssertTrue(message = "Transmission chỉ được là 'automatic', 'manual', 'cvt' hoặc 'semi-automatic'")
    public boolean isTransmissionValid() {
        if (transmission == null) return true; // optional field
        String trans = transmission.toLowerCase();
        return trans.equals("automatic") || 
               trans.equals("manual") || 
               trans.equals("cvt") || 
               trans.equals("semi-automatic");
    }

    @AssertTrue(message = "Body Style không hợp lệ")
    public boolean isBodyStyleValid() {
        if (bodyStyle == null) return true; // optional field
        String style = bodyStyle.toLowerCase();
        return style.equals("sedan") || 
               style.equals("suv") || 
               style.equals("hatchback") || 
               style.equals("coupe") || 
               style.equals("convertible") || 
               style.equals("wagon") || 
               style.equals("van") || 
               style.equals("truck");
    }

    @AssertTrue(message = "Engine size phải lớn hơn 0")
    public boolean isEngineSizeValid() {
        return engineSize != null && engineSize > 0;
    }

    @AssertTrue(message = "Số cửa phải từ 2 đến 5")
    public boolean isDoorsValid() {
        return doors != null && doors >= 2 && doors <= 5;
    }

    @AssertTrue(message = "Số xi-lanh phải từ 3 đến 12")
    public boolean isCylindersValid() {
        return cylinders != null && cylinders >= 3 && cylinders <= 12;
    }
}

