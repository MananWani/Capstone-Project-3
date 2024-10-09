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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="leave_request")
@Getter
@Setter
@NoArgsConstructor
public class LeaveRequest {
    @Id
    @Column(name="leave_request_id",length = 50)
    private String leaveRequestId;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="start_half",length = 20)
    private String startHalf;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="end_half",length = 20)
    private String endHalf;

    @Column(name="status",length = 30)
    private String status;

    @Column(name="no_of_days")
    private BigDecimal noOfDays;

    @Column(name="reason",length = 50)
    private String reason;

    @Column(name="description",length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "request_by_employee", referencedColumnName = "employee_id")
    private Employees requestByEmployee;

    @ManyToOne
    @JoinColumn(name = "type_of_leave",referencedColumnName = "type_id")
    private LeaveType typeOfLeave;

    @PrePersist
    public void generateId() {
        this.leaveRequestId = "REQ" + IdGenerator.generateRandomID();
    }
}


