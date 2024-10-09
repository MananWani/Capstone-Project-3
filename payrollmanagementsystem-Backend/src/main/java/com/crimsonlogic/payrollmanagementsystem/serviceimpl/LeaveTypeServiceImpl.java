package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveTypeDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveTypeService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LeaveTypeServiceImpl implements LeaveTypeService to manage leave types.
 * It provides methods to retrieve, add, and update leave types.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    // Constructor to initialize the LeaveTypeRepository
    LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository) {
        super();
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    public List<LeaveTypeDTO> getAllTypes() {
        log.info("inside getAllTypes method");
        // Retrieve all leave types and convert them to DTOs
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        return Mapper.INSTANCE.entityToDtoForLeaveTypes(leaveTypes);
    }

    @Override
    @Transactional
    public Boolean addLeaveType(LeaveTypeDTO newType) {
        log.info("inside addLeaveType method");
        // Check if the type name is provided
        if (newType.getTypeName() == null) {
            return false; // Return false if the type name is null
        }

        // Create a new LeaveType entity and set its properties
        LeaveType leaveType = new LeaveType();
        leaveType.setTypeName(newType.getTypeName());
        leaveType.setNumberOfLeaves(newType.getNumberOfLeaves());

        // Save the new leave type to the repository
        leaveTypeRepository.save(leaveType);
        return true; // Return true to indicate success
    }

    @Override
    @Transactional
    public boolean updateLeaveType(LeaveTypeDTO updateType) {
        log.info("inside updateLeaveType method");
        // Find the leave type by its ID and update its properties if found
        return leaveTypeRepository.findById(updateType.getTypeId())
                .map(type -> {
                    type.setTypeName(updateType.getTypeName());
                    type.setNumberOfLeaves(updateType.getNumberOfLeaves());
                    leaveTypeRepository.save(type); // Save the updated leave type
                    return true; // Return true to indicate success
                })
                .orElse(false); // Return false if the leave type was not found
    }
}
