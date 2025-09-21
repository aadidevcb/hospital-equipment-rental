package com.hospital.equipment.service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hospital.equipment.entity.Equipment;
import com.hospital.equipment.repository.EquipmentRepository;

@Service
@Transactional
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepository;
    
    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }
    
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }
    
    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }
    
    public Optional<Equipment> getEquipmentByIdWithCategory(Long id) {
        return equipmentRepository.findByIdWithCategory(id);
    }
    
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findAvailableEquipment();
    }
    
    public List<Equipment> getEquipmentByCategory(Long categoryId) {
        return equipmentRepository.findByCategoryId(categoryId);
    }
    
    public List<Equipment> getAvailableEquipmentByCategory(Long categoryId) {
        return equipmentRepository.findAvailableByCategoryId(categoryId);
    }
    
    public List<Equipment> searchEquipment(String keyword) {
        return equipmentRepository.searchByKeyword(keyword);
    }
    
    public List<Equipment> getEquipmentByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return equipmentRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public List<Equipment> getEquipmentByStatus(Equipment.EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }
    
    public Equipment createEquipment(Equipment equipment) {
        // Set default values if not provided
        if (equipment.getAvailableQuantity() == null) {
            equipment.setAvailableQuantity(equipment.getTotalQuantity());
        }
        if (equipment.getStatus() == null) {
            equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        }
        
        return equipmentRepository.save(equipment);
    }
    
    public Equipment updateEquipment(Long id, Equipment equipmentDetails) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        
        equipment.setName(equipmentDetails.getName());
        equipment.setDescription(equipmentDetails.getDescription());
        equipment.setModel(equipmentDetails.getModel());
        equipment.setManufacturer(equipmentDetails.getManufacturer());
        equipment.setDailyPrice(equipmentDetails.getDailyPrice());
        equipment.setTotalQuantity(equipmentDetails.getTotalQuantity());
        equipment.setAvailableQuantity(equipmentDetails.getAvailableQuantity());
        equipment.setStatus(equipmentDetails.getStatus());
        equipment.setImageUrl(equipmentDetails.getImageUrl());
        equipment.setCategory(equipmentDetails.getCategory());
        
        return equipmentRepository.save(equipment);
    }
    
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        
        // Check if equipment has active rentals
        if (equipment.getRentals() != null && 
            equipment.getRentals().stream().anyMatch(rental -> 
                rental.getStatus() == com.hospital.equipment.entity.Rental.RentalStatus.ACTIVE ||
                rental.getStatus() == com.hospital.equipment.entity.Rental.RentalStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot delete equipment with active rentals");
        }
        
        equipmentRepository.delete(equipment);
    }
    
    public boolean isAvailable(Long equipmentId, int requestedQuantity) {
        Optional<Equipment> equipment = equipmentRepository.findById(equipmentId);
        return equipment.isPresent() && 
               equipment.get().getStatus() == Equipment.EquipmentStatus.AVAILABLE &&
               equipment.get().getAvailableQuantity() >= requestedQuantity;
    }
    
    public void updateAvailableQuantity(Long equipmentId, int quantityChange) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
        
        int newQuantity = equipment.getAvailableQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }
        if (newQuantity > equipment.getTotalQuantity()) {
            throw new IllegalArgumentException("Available quantity cannot exceed total quantity");
        }
        
        equipment.setAvailableQuantity(newQuantity);
        
        // Update status based on availability
        if (newQuantity == 0) {
            equipment.setStatus(Equipment.EquipmentStatus.RENTED);
        } else if (equipment.getStatus() == Equipment.EquipmentStatus.RENTED) {
            equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        }
        
        equipmentRepository.save(equipment);
    }

    public Equipment storeImage(Long id, MultipartFile file) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        try {
            Path uploadDir = Paths.get("uploads/equipment");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String original = file.getOriginalFilename();
            String filename = "eq-" + id + "-" + System.currentTimeMillis() + (original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : "");
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            equipment.setImageUrl("/uploads/equipment/" + filename);
            return equipmentRepository.save(equipment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }
}