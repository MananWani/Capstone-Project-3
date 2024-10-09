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

import java.math.BigDecimal;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="leave_record")
@Getter
@Setter
@NoArgsConstructor
public class LeaveRecord {
    @Id
    @Column(name = "leave_record_id",length = 50)
    private String leaveRecordId;

    @Column(name = "total_leaves")
    private Integer totalLeaves;

    @Column(name = "used_leaves")
    private BigDecimal usedLeaves;

    @Column(name = "remaining_leaves")
    private BigDecimal remainingLeaves;

    @ManyToOne
    @JoinColumn(name = "leave_for_employee", referencedColumnName = "employee_id")
    private Employees leaveForEmployee;

    @ManyToOne
    @JoinColumn(name = "type_of_leave",referencedColumnName = "type_id")
    private LeaveType typeOfLeave;

    @PrePersist
    public void generateId() {
        this.leaveRecordId = "LEV" + IdGenerator.generateRandomID();
    }
}
