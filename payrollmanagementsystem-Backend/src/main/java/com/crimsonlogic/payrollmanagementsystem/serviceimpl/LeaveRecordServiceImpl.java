package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRecord;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRecordService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the LeaveRecordService interface.
 * This class manages the leave records of employees, including
 * setting up initial leave balances and retrieving leave records
 * for individual employees.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class LeaveRecordServiceImpl implements LeaveRecordService {

    // Repository for accessing leave records
    private final LeaveRecordRepository leaveRecordRepository;

    // Repository for accessing leave types
    private final LeaveTypeRepository leaveTypeRepository;

    // Repository for accessing employee data
    private final EmployeesRepository employeesRepository;

    // Constructor to initialize the repositories
    LeaveRecordServiceImpl(LeaveRecordRepository leaveRecordRepository,
                           LeaveTypeRepository leaveTypeRepository,
                           EmployeesRepository employeesRepository) {
        super();
        this.leaveRecordRepository = leaveRecordRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeesRepository = employeesRepository;
    }

    @Override
    @Transactional
    public void setEmployeeLeaves(Employees employee) {
        log.info("inside setEmployeeLeaves method");
        // Retrieve all leave types to set up leave records
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        // Iterate over each leave type to create a leave record
        for (LeaveType leaveType : leaveTypes) {
            LeaveRecord leaveRecord = new LeaveRecord();
            leaveRecord.setLeaveForEmployee(employee); // Associate leave record with employee
            leaveRecord.setTypeOfLeave(leaveType); // Set type of leave

            Integer totalLeaves = leaveType.getNumberOfLeaves();
            if (totalLeaves != null) {
                leaveRecord.setTotalLeaves(totalLeaves); // Set total leaves
                leaveRecord.setUsedLeaves(BigDecimal.ZERO); // Initialize used leaves to zero
                leaveRecord.setRemainingLeaves(BigDecimal.valueOf(totalLeaves)); // Set remaining leaves
            }

            // Save the leave record to the repository
            leaveRecordRepository.save(leaveRecord);
        }
    }

    @Override
    public List<LeaveRecordDTO> getLeaveRecordById(String employeeId) {
        log.info("inside getLeaveRecordById method");
        // Retrieve leave records for a specific employee by ID
        return employeesRepository.findById(employeeId)
                .map(employee -> {
                    List<LeaveRecordDTO> leaveRecordDTOs = new ArrayList<>();
                    // Find all leave records for the employee
                    leaveRecordRepository.findByLeaveForEmployee(employee)
                            .forEach(leaveRecord -> {
                                LeaveRecordDTO leaveRecordDTO = Mapper.INSTANCE.entityToDtoForLeaveRecord(leaveRecord);
                                leaveRecordDTO.setTypeOfLeave(leaveRecord.getTypeOfLeave().getTypeName()); // Set leave type name
                                leaveRecordDTOs.add(leaveRecordDTO); // Add DTO to the list
                            });
                    return leaveRecordDTOs; // Return the list of leave record DTOs
                })
                .orElseGet(ArrayList::new); // Return an empty list if employee is not found
    }
}
