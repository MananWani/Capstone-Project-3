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
public class UsersDTO {

    private String userId;

    private String email;

    private String passwordHash;

    private String role;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String fullName;

    public UsersDTO(String userId, String role){
        super();
        this.userId=userId;
        this.role=role;
    }
}
