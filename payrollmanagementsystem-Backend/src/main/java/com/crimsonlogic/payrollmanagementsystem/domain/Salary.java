package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="salary")
@Getter
@Setter
@NoArgsConstructor
public class Salary {
    @Id
    @Column(name="salary_id",length = 50)
    private String salaryId;

    @Column(name="cost_to_company")
    private BigDecimal costToCompany;

    @OneToOne
    @JoinColumn(name = "salary_of_employee", referencedColumnName = "employee_id")
    private Employees salaryOfEmployee;

    @PrePersist
    public void generateId() {
        this.salaryId = "SLY" + IdGenerator.generateRandomID();
    }
}
