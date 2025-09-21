package com.hospital.equipment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.equipment.entity.Customer;
import com.hospital.equipment.entity.Equipment;
import com.hospital.equipment.entity.Rental;
import com.hospital.equipment.repository.RentalRepository;

@Service
@Transactional
public class RentalService {
    
    private final RentalRepository rentalRepository;
    private final EquipmentService equipmentService;
    private final CustomerService customerService;
    
    @Autowired
    public RentalService(RentalRepository rentalRepository, 
                        EquipmentService equipmentService,
                        CustomerService customerService) {
        this.rentalRepository = rentalRepository;
        this.equipmentService = equipmentService;
        this.customerService = customerService;
    }
    
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
    
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }
    
    public Rental getRentalByIdWithDetails(Long id) {
        return rentalRepository.findByIdWithDetails(id);
    }
    
    public List<Rental> getRentalsByCustomer(Long customerId) {
        return rentalRepository.findByCustomerId(customerId);
    }
    
    public List<Rental> getRentalsByEquipment(Long equipmentId) {
        return rentalRepository.findByEquipmentId(equipmentId);
    }
    
    public List<Rental> getRentalsByStatus(Rental.RentalStatus status) {
        return rentalRepository.findByStatus(status);
    }
    
    public List<Rental> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDate.now());
    }
    
    public List<Rental> getActiveRentalsOnDate(LocalDate date) {
        return rentalRepository.findActiveRentalsOnDate(date);
    }
    
    public Rental createRental(Rental rental) {
        // Validate customer exists
        Customer customer = customerService.getCustomerById(rental.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Validate equipment exists and is available
        Equipment equipment = equipmentService.getEquipmentById(rental.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        // Check availability for the requested period
        if (!isEquipmentAvailable(equipment.getId(), rental.getStartDate(), 
                                 rental.getEndDate(), rental.getQuantity())) {
            throw new IllegalArgumentException("Equipment not available for the requested period and quantity");
        }
        
        // Set rental details
        rental.setCustomer(customer);
        rental.setEquipment(equipment);
        rental.setDailyRate(equipment.getDailyPrice());
        rental.setTotalAmount(rental.calculateTotalAmount());
        
        // Save rental
        Rental savedRental = rentalRepository.save(rental);
        
        // Update equipment availability
        equipmentService.updateAvailableQuantity(equipment.getId(), -rental.getQuantity());
        
        return savedRental;
    }
    
    public Rental updateRental(Long id, Rental rentalDetails) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        
        // Only allow updates for pending rentals or certain fields for active rentals
        if (rental.getStatus() == Rental.RentalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update completed rental");
        }
        
        rental.setStartDate(rentalDetails.getStartDate());
        rental.setEndDate(rentalDetails.getEndDate());
        rental.setQuantity(rentalDetails.getQuantity());
        rental.setNotes(rentalDetails.getNotes());
        rental.setTotalAmount(rental.calculateTotalAmount());
        
        return rentalRepository.save(rental);
    }
    
    public Rental updateRentalStatus(Long id, Rental.RentalStatus status) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        
        Rental.RentalStatus oldStatus = rental.getStatus();
        rental.setStatus(status);
        
        // Handle equipment quantity changes based on status transitions
        if (oldStatus == Rental.RentalStatus.PENDING && status == Rental.RentalStatus.CANCELLED) {
            // Return equipment to available pool
            equipmentService.updateAvailableQuantity(rental.getEquipment().getId(), rental.getQuantity());
        } else if (status == Rental.RentalStatus.COMPLETED && rental.getActualReturnDate() == null) {
            rental.setActualReturnDate(LocalDate.now());
            // Return equipment to available pool
            equipmentService.updateAvailableQuantity(rental.getEquipment().getId(), rental.getQuantity());
        }
        
        return rentalRepository.save(rental);
    }
    
    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        
        // Only allow deletion of pending or cancelled rentals
        if (rental.getStatus() == Rental.RentalStatus.ACTIVE || 
            rental.getStatus() == Rental.RentalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete active or completed rental");
        }
        
        // If rental was pending, return equipment to available pool
        if (rental.getStatus() == Rental.RentalStatus.PENDING || 
            rental.getStatus() == Rental.RentalStatus.CONFIRMED) {
            equipmentService.updateAvailableQuantity(rental.getEquipment().getId(), rental.getQuantity());
        }
        
        rentalRepository.delete(rental);
    }
    
    public boolean isEquipmentAvailable(Long equipmentId, LocalDate startDate, 
                                      LocalDate endDate, Integer requestedQuantity) {
        Equipment equipment = equipmentService.getEquipmentById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        // Check if equipment is in available status
        if (equipment.getStatus() != Equipment.EquipmentStatus.AVAILABLE) {
            return false;
        }
        
        // Get total booked quantity for the period
        Integer bookedQuantity = rentalRepository.getTotalBookedQuantity(equipmentId, startDate, endDate);
        if (bookedQuantity == null) {
            bookedQuantity = 0;
        }
        
        // Check if requested quantity is available
        return (equipment.getTotalQuantity() - bookedQuantity) >= requestedQuantity;
    }
    
    public int getAvailableQuantityForPeriod(Long equipmentId, LocalDate startDate, LocalDate endDate) {
        Equipment equipment = equipmentService.getEquipmentById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        Integer bookedQuantity = rentalRepository.getTotalBookedQuantity(equipmentId, startDate, endDate);
        if (bookedQuantity == null) {
            bookedQuantity = 0;
        }
        
        return equipment.getTotalQuantity() - bookedQuantity;
    }
    
    public BigDecimal calculateRentalCost(Long equipmentId, LocalDate startDate, 
                                        LocalDate endDate, Integer quantity) {
        Equipment equipment = equipmentService.getEquipmentById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        long daysBetween = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        return equipment.getDailyPrice()
                .multiply(BigDecimal.valueOf(daysBetween))
                .multiply(BigDecimal.valueOf(quantity));
    }
}