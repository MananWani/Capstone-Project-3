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
public class QueriesDTO {
    private String queryId;

    private String queryDescription;

    private String queryStatus;

    private String comment;

    private String employeeId;

    private String salaryRecordId;

    private String payPeriodStart;

    private String payPeriodEnd;

}
