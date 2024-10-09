package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginLogsDTO {
    private String logId;

    private Timestamp loginTime;

    private Timestamp logoutTime;

    private String logForEmployee;
}
