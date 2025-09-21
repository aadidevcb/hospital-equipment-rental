package com.hospital.equipment.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hospital.equipment.entity.Equipment;
import com.hospital.equipment.service.EquipmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {
    
    private final EquipmentService equipmentService;
    
    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }
    
    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        List<Equipment> equipment = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipment() {
        List<Equipment> equipment = equipmentService.getAvailableEquipment();
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id)
                .map(equipment -> ResponseEntity.ok(equipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-category")
    public ResponseEntity<Equipment> getEquipmentWithCategory(@PathVariable Long id) {
        return equipmentService.getEquipmentByIdWithCategory(id)
                .map(equipment -> ResponseEntity.ok(equipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Equipment>> getEquipmentByCategory(@PathVariable Long categoryId) {
        List<Equipment> equipment = equipmentService.getEquipmentByCategory(categoryId);
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/category/{categoryId}/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipmentByCategory(@PathVariable Long categoryId) {
        List<Equipment> equipment = equipmentService.getAvailableEquipmentByCategory(categoryId);
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Equipment>> searchEquipment(@RequestParam String keyword) {
        List<Equipment> equipment = equipmentService.searchEquipment(keyword);
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<Equipment>> getEquipmentByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<Equipment> equipment = equipmentService.getEquipmentByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(equipment);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Equipment>> getEquipmentByStatus(@PathVariable Equipment.EquipmentStatus status) {
        List<Equipment> equipment = equipmentService.getEquipmentByStatus(status);
        return ResponseEntity.ok(equipment);
    }
    
    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@Valid @RequestBody Equipment equipment) {
        try {
            Equipment createdEquipment = equipmentService.createEquipment(equipment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Image upload endpoint (simple file system storage)
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            Equipment updated = equipmentService.storeImage(id, file);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
            if (msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, 
                                                   @Valid @RequestBody Equipment equipmentDetails) {
        try {
            Equipment updatedEquipment = equipmentService.updateEquipment(id, equipmentDetails);
            return ResponseEntity.ok(updatedEquipment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        try {
            equipmentService.deleteEquipment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }
    
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable Long id, 
                                                    @RequestParam int quantity) {
        boolean available = equipmentService.isAvailable(id, quantity);
        return ResponseEntity.ok(available);
    }
}