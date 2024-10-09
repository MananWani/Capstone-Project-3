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
@Table(name="attendance")
@Getter
@Setter
@NoArgsConstructor
public class Attendance {
    @Id
    @Column(name="attendance_id",length = 50)
    private String attendanceId;

    @Column(name="attendance_for_date")
    private LocalDate attendanceForDate;

    @Column(name="overtime_hours")
    private BigDecimal overtimeHours;

    @Column(name="total_hours")
    private BigDecimal totalHours;

    @Column(name="status",length = 30)
    private String status;

    @ManyToOne
    @JoinColumn(name = "attendance_by_employee", referencedColumnName = "employee_id")
    private Employees attendanceByEmployee;

    @PrePersist
    public void generateId() {
        this.attendanceId = "ATD" + IdGenerator.generateRandomID();
    }
}
