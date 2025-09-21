package com.hospital.equipment.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "equipment")
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Equipment name is required")
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @NotBlank(message = "Equipment model is required")
    private String model;
    
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;
    
    @NotNull(message = "Daily rental price is required")
    @PositiveOrZero(message = "Daily rental price must be positive")
    @Column(name = "daily_price", precision = 10, scale = 2)
    private BigDecimal dailyPrice;
    
    @PositiveOrZero(message = "Available quantity must be positive")
    private Integer availableQuantity = 1;
    
    @PositiveOrZero(message = "Total quantity must be positive")
    private Integer totalQuantity = 1;
    
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;
    
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("equipment-rentals")
    private List<Rental> rentals;
    
    // Enums
    public enum EquipmentStatus {
        AVAILABLE, RENTED, MAINTENANCE, RETIRED
    }
    
    // Constructors
    public Equipment() {}
    
    public Equipment(String name, String description, String model, String manufacturer, 
                    BigDecimal dailyPrice, Integer totalQuantity, Category category) {
        this.name = name;
        this.description = description;
        this.model = model;
        this.manufacturer = manufacturer;
        this.dailyPrice = dailyPrice;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }
    
    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public EquipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<Rental> getRentals() {
        return rentals;
    }
    
    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
}