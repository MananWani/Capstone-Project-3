package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="salary_record")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SalaryRecord {
    @Id
    @Column(name="salary_record_id",length = 50)
    private String salaryRecordId;

    @Column(name="pay_period_start")
    private LocalDate payPeriodStart;

    @Column(name="pay_period_end")
    private LocalDate payPeriodEnd;

    @Column(name="gross_salary")
    private BigDecimal grossSalary;

    @Column(name="bonusAmount")
    private BigDecimal bonusAmount;

    @Column(name="tax_amount")
    private BigDecimal taxAmount;

    @Column(name="pf_amount")
    private BigDecimal pfAmount;

    @Column(name="penalty_amount")
    private BigDecimal penaltyAmount;

    @Column(name="net_salary")
    private BigDecimal netSalary;

    @ManyToOne
    @JoinColumn(name = "salary_record_of_employee", referencedColumnName = "employee_id")
    private Employees salaryRecordOfEmployee;

    @PrePersist
    public void generateId() {
        this.salaryRecordId = "RCD" + IdGenerator.generateRandomID();
    }
}
