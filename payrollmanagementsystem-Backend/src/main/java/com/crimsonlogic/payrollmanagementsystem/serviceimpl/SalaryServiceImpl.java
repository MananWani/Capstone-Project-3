package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Salary;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRepository;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation for managing salary details, including retrieving and updating
 * cost-to-company (CTC) details for employees.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class SalaryServiceImpl implements SalaryService {

    // Repository for salary data
    private final SalaryRepository salaryRepository;

    // Constructor to initialize the salary repository
    SalaryServiceImpl(SalaryRepository salaryRepository) {
        super();
        this.salaryRepository = salaryRepository;
    }

    @Override
    public List<SalaryDTO> getCtcDetails() {
        log.info("inside getCtcDetails method");
        // Fetch all salary records and convert them to DTOs with employee names
        return salaryRepository.findAll().stream()
                .map(salary -> {
                    SalaryDTO salaryDTO = Mapper.INSTANCE.entityToDtoForSalary(salary);
                    salaryDTO.setFullName(salary.getSalaryOfEmployee().getFullName()); // Set full name of employee
                    return salaryDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public boolean updateCtc(SalaryDTO salaryDTO) {
        log.info("inside updateCtc method");
        // Update the cost-to-company for an existing salary record
        String salaryId = salaryDTO.getSalaryId();

        return salaryRepository.findById(salaryId)
                .map(salary -> {
                    salary.setCostToCompany(salaryDTO.getCostToCompany()); // Update CTC
                    salaryRepository.save(salary); // Save the updated salary
                    return true; // Indicate success
                })
                .orElse(false); // Return false if salary record not found
    }

    @Override
    @Transactional
    public void setSalary(Employees employee) {
        log.info("inside setSalary method");
        // Create a new salary record for the employee with an initial CTC of zero
        Salary newSalary = new Salary();
        newSalary.setSalaryOfEmployee(employee); // Associate the salary with the employee
        newSalary.setCostToCompany(BigDecimal.ZERO); // Initialize CTC to zero
        salaryRepository.save(newSalary); // Save the new salary record
    }
}

