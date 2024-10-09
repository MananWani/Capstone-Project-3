package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRecord;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRequest;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.domain.Attendance;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRequestDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.LeavesExhaustedException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRequestRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * LeaveRequestServiceImpl is the implementation of the LeaveRequestService interface.
 * This service handles all leave request operations including creating, updating,
 * fetching, and cancelling leave requests for employees.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    // Constants for indicating morning and afternoon sessions
    private static final String MORNING = "Morning";
    private static final String AFTERNOON = "Afternoon";

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeesRepository employeesRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRecordRepository leaveRecordRepository;

    // Constructor to initialize repositories
    LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository,
                            EmployeesRepository employeesRepository,
                            LeaveTypeRepository leaveTypeRepository,
                            LeaveRecordRepository leaveRecordRepository,
                            AttendanceRepository attendanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeesRepository = employeesRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRecordRepository = leaveRecordRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public List<LeaveRequestDTO> getLeaveRequestById(String employeeId) {
        log.info("inside getLeaveRequestById method");
        // Retrieve leave requests for a specific employee
        return employeesRepository.findById(employeeId)
                .map(employee -> {
                    List<LeaveRequestDTO> leaveRequestDTOs = new ArrayList<>();
                    leaveRequestRepository.findByRequestByEmployee(employee)
                            .forEach(request -> {
                                LeaveRequestDTO leaveRequestDTO = Mapper.INSTANCE.entityToDtoForLeaveRequest(request);
                                leaveRequestDTO.setTypeOfLeave(request.getTypeOfLeave().getTypeName());
                                leaveRequestDTOs.add(leaveRequestDTO);
                            });
                    return leaveRequestDTOs;
                })
                .orElseGet(ArrayList::new); // Return empty list if employee not found
    }

    @Override
    @Transactional
    public void requestLeave(LeaveRequestDTO leaveRequestDTO)
            throws LeavesExhaustedException, ResourceNotFoundException {
        log.info("inside requestLeave method");
        // Check for employee and leave type existence
        Optional<Employees> employeesOpt = employeesRepository.findById(leaveRequestDTO.getRequestByEmployee());
        Optional<LeaveType> leaveTypeOpt = leaveTypeRepository.findById(leaveRequestDTO.getTypeOfLeave());

        if (employeesOpt.isEmpty() || leaveTypeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Unexpected error, Please try again later.");
        }

        Employees employee = employeesOpt.get();
        LeaveType leaveType = leaveTypeOpt.get();

        // Calculate requested leave days
        BigDecimal requestedDays = calculateRequestedDays(leaveRequestDTO.getStartDate(),
                leaveRequestDTO.getEndDate(),
                leaveRequestDTO.getStartHalf(),
                leaveRequestDTO.getEndHalf());

        // Check leave record for the employee and type of leave
        LeaveRecord leaveRecord = leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType);
        if (leaveRecord != null) {
            // Validate if enough leave days are available
            if (leaveRecord.getRemainingLeaves().compareTo(requestedDays) < 0) {
                throw new LeavesExhaustedException(leaveRecord.getTypeOfLeave().getTypeName() + " count is exhausted.");
            }


            // Create and save the leave request
            LeaveRequest leaveRequest = Mapper.INSTANCE.dtoToEntityForLeaveRequest(leaveRequestDTO);
            leaveRequest.setStatus("Pending"); // Set initial status
            leaveRequest.setRequestByEmployee(employee);
            leaveRequest.setTypeOfLeave(leaveType);
            leaveRequest.setNoOfDays(requestedDays);
            leaveRequestRepository.save(leaveRequest);
        } else {
            throw new ResourceNotFoundException("Unexpected error, please try again later.");
        }
    }

    @Override
    public List<LeaveRequestDTO> getPendingRequestsById(String employeeId) {
        log.info("inside getPendingRequestsById method");
        // Fetch pending leave requests for the given employee
        return employeesRepository.findById(employeeId)
                .map(employee -> {
                    List<LeaveRequestDTO> leaveRequestDTOs = new ArrayList<>();
                    leaveRequestRepository.findPendingLeaveRequests(employee)
                            .forEach(request -> {
                                LeaveRequestDTO leaveRequestDTO = Mapper.INSTANCE.entityToDtoForLeaveRequest(request);
                                leaveRequestDTO.setTypeOfLeave(request.getTypeOfLeave().getTypeName());
                                leaveRequestDTO.setRequestByEmployee(request.getRequestByEmployee().getFullName());
                                leaveRequestDTOs.add(leaveRequestDTO);
                            });
                    return leaveRequestDTOs;
                })
                .orElseGet(ArrayList::new); // Return empty list if employee not found
    }

    @Override
    @Transactional
    public boolean updateLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        log.info("inside updateLeaveRequest method");
        // Update an existing leave request based on the provided DTO
        return leaveRequestRepository.findById(leaveRequestDTO.getLeaveRequestId())
                .map(request -> {
                    boolean isUpdated = true;

                    // If the request is approved, update leave records and attendance
                    if ("Approved".equalsIgnoreCase(leaveRequestDTO.getStatus())) {
                        isUpdated = updateLeaveRecord(leaveRequestDTO.getNoOfDays(),
                                leaveRequestDTO.getTypeId(), leaveRequestDTO.getEmployeeId());
                        if (isUpdated) {
                            insertAttendanceRecords(request); // Log attendance for the leave
                        }
                    }

                    // Update request details
                    if (isUpdated) {
                        request.setStatus(leaveRequestDTO.getStatus());
                        request.setDescription(leaveRequestDTO.getDescription());
                        leaveRequestRepository.save(request);
                    }

                    return isUpdated; // Return update status
                })
                .orElse(false); // Return false if request not found
    }

    @Transactional
    private void insertAttendanceRecords(LeaveRequest leaveRequest) {
        log.info("inside insertAttendanceRecords method");
        // Insert attendance records based on the leave request
        LocalDate startDate = leaveRequest.getStartDate();
        LocalDate endDate = leaveRequest.getEndDate();

        // Iterate over each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Attendance attendance = new Attendance();
            attendance.setAttendanceByEmployee(leaveRequest.getRequestByEmployee());
            attendance.setAttendanceForDate(date);

            // Determine attendance status based on the leave request details
            if (date.isEqual(startDate) && leaveRequest.getStartHalf().equals(AFTERNOON)) {
                attendance.setStatus("Half Day");
            } else if (date.isEqual(endDate) && leaveRequest.getEndHalf().equals(MORNING)) {
                attendance.setStatus("Half Day");
            } else if (date.isAfter(startDate) && date.isBefore(endDate)) {
                attendance.setStatus("Leave");
            } else {
                attendance.setStatus("Leave");
            }

            attendance.setTotalHours(BigDecimal.valueOf(8)); // Default total hours for a full day
            attendance.setOvertimeHours(BigDecimal.ZERO); // No overtime during leave

            attendanceRepository.save(attendance); // Save attendance record
        }
    }

    @Override
    @Transactional
    public boolean cancelLeaveRequest(String leaveRequestId) {
        log.info("inside cancelLeaveRequest method");
        // Cancel a leave request and revert leave records
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(leaveRequestId);

        if (leaveRequestOpt.isPresent()) {
            LeaveRequest leaveRequest = leaveRequestOpt.get();

            // If the leave request was approved, update the leave record
            if ("Approved".equalsIgnoreCase(leaveRequest.getStatus())) {
                LeaveType leaveType = leaveRequest.getTypeOfLeave();
                Employees employee = leaveRequest.getRequestByEmployee();

                LeaveRecord leaveRecord = leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType);
                leaveRecord.setRemainingLeaves(leaveRecord.getRemainingLeaves().add(leaveRequest.getNoOfDays()));
                leaveRecord.setUsedLeaves(leaveRecord.getUsedLeaves().subtract(leaveRequest.getNoOfDays()));
                leaveRecordRepository.save(leaveRecord);
            }

            // Delete attendance records for the cancelled leave
            deleteAttendanceRecords(leaveRequest.getStartDate(), leaveRequest.getEndDate(), leaveRequest.getRequestByEmployee());

            // Mark the leave request as cancelled
            leaveRequest.setStatus("Cancelled");
            leaveRequest.setDescription("Cancelled.");
            leaveRequestRepository.save(leaveRequest);
            return true; // Return success status
        }

        return false; // Return false if leave request not found
    }

    private void deleteAttendanceRecords(LocalDate startDate, LocalDate endDate,
                                         Employees requestByEmployee) {
        log.info("inside deleteAttendanceRecords method");
        // Delete attendance records for the given date range and employee
        attendanceRepository.deleteByEmployeeAndDateRange(
                requestByEmployee, startDate, endDate
        );
    }

    @Transactional
    private boolean updateLeaveRecord(BigDecimal noOfDays, String typeId, String employeeId) {
        log.info("inside updateLeaveRecord method");
        // Update the leave record based on the leave request
        Optional<LeaveType> leaveTypeOpt = leaveTypeRepository.findById(typeId);
        Optional<Employees> employeesOpt = employeesRepository.findById(employeeId);

        if (leaveTypeOpt.isPresent() && employeesOpt.isPresent()) {
            LeaveType leaveType = leaveTypeOpt.get();
            Employees employee = employeesOpt.get();
            LeaveRecord leaveRecord = leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType);

            if (leaveRecord != null) {
                // Adjust the used and remaining leave counts
                leaveRecord.setUsedLeaves(leaveRecord.getUsedLeaves().add(noOfDays));
                leaveRecord.setRemainingLeaves(leaveRecord.getRemainingLeaves().subtract(noOfDays));
                leaveRecordRepository.save(leaveRecord);
                return true; // Return success status
            }
        }
        return false; // Return false if leave record not found
    }

    private BigDecimal calculateRequestedDays(LocalDate startDate, LocalDate endDate,
                                              String startHalf, String endHalf) {
        log.info("inside calculateRequestedDays method");
        // Calculate the total requested leave days based on start and end dates and halves
        BigDecimal totalDays = BigDecimal.ZERO;

        if (startDate.isEqual(endDate)) {
            // If the start and end dates are the same
            if (startHalf.equals(MORNING) && endHalf.equals(MORNING)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5)); // Half day
            } else if (startHalf.equals(AFTERNOON) && endHalf.equals(AFTERNOON)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5)); // Half day
            } else {
                totalDays = totalDays.add(BigDecimal.ONE); // Full day
            }
        } else {
            // If the start and end dates are different
            if (startHalf.equals(MORNING)) {
                totalDays = totalDays.add(BigDecimal.ONE); // Full day for the first day
            } else if (startHalf.equals(AFTERNOON)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5)); // Half day for the first day
            }

            // Count full days in between
            totalDays = totalDays.add(BigDecimal.valueOf(ChronoUnit.DAYS.between(startDate.plusDays(1), endDate)));

            // Add for the end date
            if (endHalf.equals(MORNING)) {
                totalDays = totalDays.add(BigDecimal.valueOf(0.5)); // Half day for the last day
            } else if (endHalf.equals(AFTERNOON)) {
                totalDays = totalDays.add(BigDecimal.ONE); // Full day for the last day
            }
        }

        return totalDays; // Return the total calculated days
    }
}
