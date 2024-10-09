package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employees {
    @Id
    @Column(name = "employee_id",length = 50)
    private String employeeId;

    @Column(name = "full_name",length=50)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "designation", referencedColumnName = "designation_id")
    private Designation designation;

    @Column(name = "mobile_number",length = 20)
    private String mobileNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @ManyToOne
    @JoinColumn(name = "manager", referencedColumnName = "employee_id")
    private Employees manager;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @PrePersist
    public void generateId() {
        this.employeeId = "EMP" + IdGenerator.generateRandomID();
    }
}
