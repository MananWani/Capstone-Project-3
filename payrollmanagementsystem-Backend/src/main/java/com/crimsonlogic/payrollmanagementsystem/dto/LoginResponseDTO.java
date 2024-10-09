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
public class LoginResponseDTO {
    private String employeeId;

    private String fullName;

    private String role;

    private String logId;

}
