package com.hospital.equipment.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hospital.equipment.entity.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    List<Rental> findByCustomerId(Long customerId);
    
    List<Rental> findByEquipmentId(Long equipmentId);
    
    List<Rental> findByStatus(Rental.RentalStatus status);
    
    @Query("SELECT r FROM Rental r WHERE r.endDate < :currentDate AND r.status = 'ACTIVE'")
    List<Rental> findOverdueRentals(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT r FROM Rental r WHERE r.startDate <= :date AND r.endDate >= :date")
    List<Rental> findActiveRentalsOnDate(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Rental r LEFT JOIN FETCH r.customer LEFT JOIN FETCH r.equipment WHERE r.id = :id")
    Rental findByIdWithDetails(Long id);
    
    @Query("SELECT r FROM Rental r WHERE r.equipment.id = :equipmentId AND " +
           "r.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') AND " +
           "((r.startDate <= :endDate) AND (r.endDate >= :startDate))")
    List<Rental> findConflictingRentals(@Param("equipmentId") Long equipmentId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(r.quantity) FROM Rental r WHERE r.equipment.id = :equipmentId AND " +
           "r.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') AND " +
           "((r.startDate <= :endDate) AND (r.endDate >= :startDate))")
    Integer getTotalBookedQuantity(@Param("equipmentId") Long equipmentId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}