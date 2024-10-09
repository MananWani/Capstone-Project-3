package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="queries")
@Getter
@Setter
@NoArgsConstructor
public class Queries {
    @Id
    @Column(name = "query_id",length = 50)
    private String queryId;

    @Column(name = "query_description",length = 100)
    private String queryDescription;

    @Column(name = "query_status",length = 30)
    private String queryStatus;

    @Column(name="comment",length = 50)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "query_by_employee", referencedColumnName = "employee_id")
    private Employees queryByEmployee;

    @ManyToOne
    @JoinColumn(name = "query_for_salary_record",referencedColumnName = "salary_record_id")
    private SalaryRecord queryForSalaryRecord;

    @PrePersist
    public void generateId() {
        this.queryId = "QRY" + IdGenerator.generateRandomID();
    }
}
