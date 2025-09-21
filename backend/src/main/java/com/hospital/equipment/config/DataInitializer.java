package com.hospital.equipment.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hospital.equipment.entity.Category;
import com.hospital.equipment.entity.Customer;
import com.hospital.equipment.entity.Equipment;
import com.hospital.equipment.repository.CategoryRepository;
import com.hospital.equipment.repository.CustomerRepository;
import com.hospital.equipment.repository.EquipmentRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final CustomerRepository customerRepository;
    
    @Autowired
    public DataInitializer(CategoryRepository categoryRepository,
                          EquipmentRepository equipmentRepository,
                          CustomerRepository customerRepository) {
        this.categoryRepository = categoryRepository;
        this.equipmentRepository = equipmentRepository;
        this.customerRepository = customerRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create categories
        Category mobilityCategory = new Category("Mobility Equipment", "Equipment to assist with patient mobility");
        Category monitoringCategory = new Category("Monitoring Equipment", "Equipment for patient monitoring and diagnostics");
        Category respiratoryCategory = new Category("Respiratory Equipment", "Equipment for respiratory care and support");
        Category therapyCategory = new Category("Therapy Equipment", "Equipment for physical and occupational therapy");
        
        categoryRepository.save(mobilityCategory);
        categoryRepository.save(monitoringCategory);
        categoryRepository.save(respiratoryCategory);
        categoryRepository.save(therapyCategory);
        
        // Create equipment
        Equipment wheelchair = new Equipment(
            "Standard Wheelchair",
            "Manual wheelchair with standard features for patient mobility",
            "SW-2023",
            "MedEquip Inc",
            new BigDecimal("25.00"),
            5,
            mobilityCategory
        );
        wheelchair.setImageUrl("https://via.placeholder.com/300x200?text=Wheelchair");
        
        Equipment walker = new Equipment(
            "Folding Walker",
            "Lightweight aluminum folding walker with adjustable height",
            "FW-101",
            "WalkSafe Corp",
            new BigDecimal("15.00"),
            8,
            mobilityCategory
        );
        walker.setImageUrl("https://via.placeholder.com/300x200?text=Walker");
        
        Equipment hospitalBed = new Equipment(
            "Electric Hospital Bed",
            "Fully electric hospital bed with side rails and mattress",
            "EHB-500",
            "BedCare Systems",
            new BigDecimal("75.00"),
            3,
            mobilityCategory
        );
        hospitalBed.setImageUrl("https://via.placeholder.com/300x200?text=Hospital+Bed");
        
        Equipment bloodPressureMonitor = new Equipment(
            "Digital Blood Pressure Monitor",
            "Automatic digital blood pressure monitor with large display",
            "BP-200",
            "VitalCheck",
            new BigDecimal("20.00"),
            10,
            monitoringCategory
        );
        bloodPressureMonitor.setImageUrl("https://via.placeholder.com/300x200?text=BP+Monitor");
        
        Equipment pulseOximeter = new Equipment(
            "Pulse Oximeter",
            "Fingertip pulse oximeter for oxygen saturation monitoring",
            "PO-50",
            "OxyMed",
            new BigDecimal("10.00"),
            15,
            monitoringCategory
        );
        pulseOximeter.setImageUrl("https://via.placeholder.com/300x200?text=Pulse+Oximeter");
        
        Equipment oxygenConcentrator = new Equipment(
            "Portable Oxygen Concentrator",
            "Lightweight portable oxygen concentrator for home use",
            "POC-300",
            "AirLife Medical",
            new BigDecimal("100.00"),
            4,
            respiratoryCategory
        );
        oxygenConcentrator.setImageUrl("https://via.placeholder.com/300x200?text=Oxygen+Concentrator");
        
        Equipment nebulizer = new Equipment(
            "Compressor Nebulizer",
            "Electric compressor nebulizer for medication delivery",
            "NEB-150",
            "BreathEasy",
            new BigDecimal("35.00"),
            6,
            respiratoryCategory
        );
        nebulizer.setImageUrl("https://via.placeholder.com/300x200?text=Nebulizer");
        
        Equipment exerciseBike = new Equipment(
            "Stationary Exercise Bike",
            "Low-impact stationary bike for physical therapy and rehabilitation",
            "EB-400",
            "RehabFit",
            new BigDecimal("45.00"),
            2,
            therapyCategory
        );
        exerciseBike.setImageUrl("https://via.placeholder.com/300x200?text=Exercise+Bike");
        
        Equipment crutches = new Equipment(
            "Adjustable Crutches",
            "Lightweight aluminum crutches with comfortable grips",
            "CR-75",
            "SupportWalk",
            new BigDecimal("12.00"),
            12,
            mobilityCategory
        );
        crutches.setImageUrl("https://via.placeholder.com/300x200?text=Crutches");
        
        Equipment thermoMeter = new Equipment(
            "Digital Thermometer",
            "Fast-reading digital thermometer for accurate temperature measurement",
            "DT-101",
            "TempCheck",
            new BigDecimal("8.00"),
            20,
            monitoringCategory
        );
        thermoMeter.setImageUrl("https://via.placeholder.com/300x200?text=Thermometer");
        
        equipmentRepository.save(wheelchair);
        equipmentRepository.save(walker);
        equipmentRepository.save(hospitalBed);
        equipmentRepository.save(bloodPressureMonitor);
        equipmentRepository.save(pulseOximeter);
        equipmentRepository.save(oxygenConcentrator);
        equipmentRepository.save(nebulizer);
        equipmentRepository.save(exerciseBike);
        equipmentRepository.save(crutches);
        equipmentRepository.save(thermoMeter);
        
        // Create sample customers
        Customer customer1 = new Customer("John", "Smith", "john.smith@email.com", "15550123");
        customer1.setAddress("123 Main St");
        customer1.setCity("Springfield");
        customer1.setState("IL");
        customer1.setZipCode("62701");
        
        Customer customer2 = new Customer("Sarah", "Johnson", "sarah.johnson@email.com", "15550456");
        customer2.setAddress("456 Oak Ave");
        customer2.setCity("Chicago");
        customer2.setState("IL");
        customer2.setZipCode("60601");
        
        Customer customer3 = new Customer("Michael", "Brown", "michael.brown@email.com", "15550789");
        customer3.setAddress("789 Pine St");
        customer3.setCity("Rockford");
        customer3.setState("IL");
        customer3.setZipCode("61101");
        
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        
        System.out.println("Sample data initialized successfully!");
    }
}