package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class EmployeesDTO {
    private String employeeId;

    private String fullName;

    private String designation;

    private String mobileNumber;

    private LocalDate dateOfBirth;

    private LocalDate joiningDate;

    private String manager;

    private Integer rating;

    private Boolean isActive;

    private String role;

    private String email;

    private String passwordHash;

    EmployeesDTO(String employeeId,String fullName){
        super();
        this.employeeId=employeeId;
        this.fullName=fullName;
    }

    EmployeesDTO(String employeeId,String fullName,Integer rating){
        super();
        this.employeeId=employeeId;
        this.fullName=fullName;
        this.rating=rating;
    }

    public EmployeesDTO(String employeeId, String fullName, String mobileNumber, String email,
                        Boolean isActive,String designation,  String manager) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.designation = designation;
        this.manager = manager;
        this.mobileNumber=mobileNumber;
        this.isActive=isActive;
    }
}
