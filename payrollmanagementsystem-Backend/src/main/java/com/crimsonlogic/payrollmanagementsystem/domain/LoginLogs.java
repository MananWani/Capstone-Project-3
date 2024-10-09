package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="login_logs")
@Getter
@Setter
@NoArgsConstructor
public class LoginLogs {
    @Id
    @Column(name = "log_id",length = 50)
    private String logId;

    @Column(name = "login_time")
    private Timestamp loginTime;

    @Column(name = "logout_time")
    private Timestamp logoutTime;

    @ManyToOne
    @JoinColumn(name = "log_for_employee", referencedColumnName = "employee_id")
    private Employees logForEmployee;

    @PrePersist
    public void generateId() {
        this.logId = "LOG" + IdGenerator.generateRandomID();
    }
}
