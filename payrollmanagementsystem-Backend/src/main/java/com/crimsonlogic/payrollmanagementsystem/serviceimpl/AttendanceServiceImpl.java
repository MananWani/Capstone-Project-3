package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Attendance;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepositoryCustom;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.AttendanceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the AttendanceService interface.
 * This class handles the business logic related to employee attendance,
 * including retrieving attendance records, marking attendance,
 * and generating attendance reports for teams.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class AttendanceServiceImpl implements AttendanceService {

    // Repository for accessing attendance data
    private final AttendanceRepository attendanceRepository;

    // Custom repository for specialized attendance queries
    private final AttendanceRepositoryCustom attendanceRepositoryCustom;

    // Repository for accessing employee data
    private final EmployeesRepository employeesRepository;

    // Constructor to initialize the repositories
    AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                          EmployeesRepository employeesRepository,
                          AttendanceRepositoryCustom attendanceRepositoryCustom) {
        super();
        this.attendanceRepository = attendanceRepository;
        this.employeesRepository = employeesRepository;
        this.attendanceRepositoryCustom = attendanceRepositoryCustom;
    }

    @Override
    public List<AttendanceDTO> getAttendanceForEmployee(String employeeId) {
        log.info("inside getAttendanceForEmployee method");
        // Retrieve attendance records for a specific employee
        return employeesRepository.findById(employeeId)
                .map(employee -> attendanceRepository.findByAttendanceByEmployee(employee).stream()
                        .map(attendance -> {
                            // Map the Attendance entity to AttendanceDTO
                            AttendanceDTO attendanceDTO = Mapper.INSTANCE.entityToDtoForAttendance(attendance);
                            // Set the employee ID in the DTO
                            attendanceDTO.setAttendanceByEmployee(attendance.getAttendanceByEmployee().getEmployeeId());
                            return attendanceDTO;
                        })
                        .toList())
                .orElseGet(ArrayList::new); // Return an empty list if the employee is not found
    }

    @Override
    @Transactional
    public void markAttendance(AttendanceDTO attendanceDTO) throws ResourceNotFoundException {
        log.info("inside markAttendance method");
        // Find the employee based on the provided ID in the DTO
        Employees employee = employeesRepository.findById(attendanceDTO.getAttendanceByEmployee())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Create a new Attendance entity and set its fields
        Attendance attendance = new Attendance();
        attendance.setAttendanceForDate(attendanceDTO.getAttendanceForDate());
        attendance.setTotalHours(BigDecimal.valueOf(8)); // Default total hours
        attendance.setOvertimeHours(BigDecimal.ZERO); // Default overtime hours
        attendance.setStatus(attendanceDTO.getStatus());
        attendance.setAttendanceByEmployee(employee); // Associate attendance with the employee

        // Save the attendance record
        attendanceRepository.save(attendance);
    }

    @Override
    public AttendanceResponseDTO getTeamAttendance(Integer month, String employeeId)
            throws ResourceNotFoundException {
        log.info("inside getTeamAttendance method");
        // Find the employee to retrieve attendance for their team
        Employees employee = employeesRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Unexpected error, try again later."));

        // Retrieve attendance records for the specified month
        List<Attendance> attendanceList = attendanceRepository.findByMonthAndEmployee(month, employee);
        AttendanceResponseDTO attendanceResponse = new AttendanceResponseDTO();

        // If there are attendance records, set the employee's full name
        if (!attendanceList.isEmpty()) {
            attendanceResponse.setFullName(attendanceList.get(0).getAttendanceByEmployee().getFullName());
        }

        // Count the attendance status occurrences
        for (Attendance attendance : attendanceList) {
            switch (attendance.getStatus()) {
                case "Present":
                    attendanceResponse.setPresentCount(attendanceResponse.getPresentCount() + 1);
                    break;
                case "Leave":
                    attendanceResponse.setLeaveCount(attendanceResponse.getLeaveCount() + 1);
                    break;
                case "Half Day":
                    attendanceResponse.setHalfDayCount(attendanceResponse.getHalfDayCount() + 1);
                    break;
                case "Absent":
                    attendanceResponse.setAbsentCount(attendanceResponse.getAbsentCount() + 1);
                    break;
                default:
                    break;
            }
        }

        // Return the attendance response with aggregated data
        return attendanceResponse;
    }

    @Override
    public void regularize() {
        log.info("inside regularize method");
        // Regularize attendance by inserting absent records as needed
        attendanceRepositoryCustom.insertAbsentAttendance();
    }
}
