package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class AttendanceDTO {

    private String attendanceId;

    private LocalDate attendanceForDate;

    private BigDecimal overtimeHours;

    private BigDecimal totalHours;

    private String status;

    private String attendanceByEmployee;
}
