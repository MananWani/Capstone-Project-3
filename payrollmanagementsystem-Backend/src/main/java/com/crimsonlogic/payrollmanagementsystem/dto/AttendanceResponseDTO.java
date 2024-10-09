package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class AttendanceResponseDTO {
    private String fullName;
    private Integer presentCount=0;
    private Integer leaveCount=0;
    private Integer halfDayCount=0;
    private Integer absentCount=0;
}
