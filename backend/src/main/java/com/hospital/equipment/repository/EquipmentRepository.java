package com.hospital.equipment.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hospital.equipment.entity.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    List<Equipment> findByCategoryId(Long categoryId);
    
    List<Equipment> findByStatus(Equipment.EquipmentStatus status);
    
    List<Equipment> findByAvailableQuantityGreaterThan(Integer quantity);
    
    @Query("SELECT e FROM Equipment e WHERE e.availableQuantity > 0 AND e.status = 'AVAILABLE'")
    List<Equipment> findAvailableEquipment();
    
    @Query("SELECT e FROM Equipment e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Equipment> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Equipment e WHERE e.dailyPrice BETWEEN :minPrice AND :maxPrice")
    List<Equipment> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT e FROM Equipment e LEFT JOIN FETCH e.category WHERE e.id = :id")
    Optional<Equipment> findByIdWithCategory(Long id);
    
    @Query("SELECT e FROM Equipment e WHERE e.category.id = :categoryId AND " +
           "e.availableQuantity > 0 AND e.status = 'AVAILABLE'")
    List<Equipment> findAvailableByCategoryId(@Param("categoryId") Long categoryId);
}