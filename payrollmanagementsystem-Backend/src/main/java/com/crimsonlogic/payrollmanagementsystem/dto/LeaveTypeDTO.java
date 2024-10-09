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
public class LeaveTypeDTO {
    private String typeId;

    private String typeName;

    private Integer numberOfLeaves;

}
