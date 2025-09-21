package com.hospital.equipment.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.equipment.entity.Customer;
import com.hospital.equipment.entity.Equipment;
import com.hospital.equipment.entity.Rental;
import com.hospital.equipment.repository.CustomerRepository;
import com.hospital.equipment.repository.EquipmentRepository;
import com.hospital.equipment.service.RentalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalController {
    
    private final RentalService rentalService;
    private final CustomerRepository customerRepository;
    private final EquipmentRepository equipmentRepository;
    
    @Autowired
    public RentalController(RentalService rentalService, CustomerRepository customerRepository, EquipmentRepository equipmentRepository) {
        this.rentalService = rentalService;
        this.customerRepository = customerRepository;
        this.equipmentRepository = equipmentRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(rental -> ResponseEntity.ok(rental))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<Rental> getRentalWithDetails(@PathVariable Long id) {
        Rental rental = rentalService.getRentalByIdWithDetails(id);
        return rental != null ? ResponseEntity.ok(rental) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Rental>> getRentalsByCustomer(@PathVariable Long customerId) {
        List<Rental> rentals = rentalService.getRentalsByCustomer(customerId);
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<Rental>> getRentalsByEquipment(@PathVariable Long equipmentId) {
        List<Rental> rentals = rentalService.getRentalsByEquipment(equipmentId);
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Rental>> getRentalsByStatus(@PathVariable Rental.RentalStatus status) {
        List<Rental> rentals = rentalService.getRentalsByStatus(status);
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<Rental>> getOverdueRentals() {
        List<Rental> rentals = rentalService.getOverdueRentals();
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Rental>> getActiveRentalsOnDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Rental> rentals = rentalService.getActiveRentalsOnDate(date);
        return ResponseEntity.ok(rentals);
    }
    
    public static class RentalRequestDto {
        public Long customerId;
        public Long equipmentId;
        public java.time.LocalDate startDate;
        public java.time.LocalDate endDate;
        public Integer quantity;
        public String notes;
    }

    @PostMapping
    public ResponseEntity<?> createRental(@Valid @RequestBody RentalRequestDto request) {
        System.out.println("DEBUG: Received rental request: customerId=" + request.customerId + ", equipmentId=" + request.equipmentId + ", startDate=" + request.startDate + ", endDate=" + request.endDate + ", quantity=" + request.quantity + ", notes=" + request.notes);
        try {
            Customer customer = customerRepository.findById(request.customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Equipment equipment = equipmentRepository.findById(request.equipmentId)
                    .orElseThrow(() -> new RuntimeException("Equipment not found"));
            Rental rental = new Rental();
            rental.setCustomer(customer);
            rental.setEquipment(equipment);
            rental.setStartDate(request.startDate);
            rental.setEndDate(request.endDate);
            rental.setQuantity(request.quantity);
            rental.setNotes(request.notes);
            Rental createdRental = rentalService.createRental(rental);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Rental> updateRental(@PathVariable Long id, 
                                             @Valid @RequestBody Rental rentalDetails) {
        try {
            Rental updatedRental = rentalService.updateRental(id, rentalDetails);
            return ResponseEntity.ok(updatedRental);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Rental> updateRentalStatus(@PathVariable Long id, 
                                                   @RequestParam Rental.RentalStatus status) {
        try {
            Rental updatedRental = rentalService.updateRentalStatus(id, status);
            return ResponseEntity.ok(updatedRental);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        try {
            rentalService.deleteRental(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }
    
    @GetMapping("/equipment/{equipmentId}/availability")
    public ResponseEntity<Boolean> checkEquipmentAvailability(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Integer quantity) {
        boolean available = rentalService.isEquipmentAvailable(equipmentId, startDate, endDate, quantity);
        return ResponseEntity.ok(available);
    }
    
    @GetMapping("/equipment/{equipmentId}/available-quantity")
    public ResponseEntity<Integer> getAvailableQuantityForPeriod(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        int availableQuantity = rentalService.getAvailableQuantityForPeriod(equipmentId, startDate, endDate);
        return ResponseEntity.ok(availableQuantity);
    }
    
    @GetMapping("/equipment/{equipmentId}/cost")
    public ResponseEntity<BigDecimal> calculateRentalCost(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Integer quantity) {
        try {
            BigDecimal cost = rentalService.calculateRentalCost(equipmentId, startDate, endDate, quantity);
            return ResponseEntity.ok(cost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}