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

import java.sql.Timestamp;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    @Column(name="user_id",length = 50)
    private String userId;

    @Column(name="email",length=50)
    private String email;

    @Column(name="password_hash",length=100)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Roles role;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="updated_at")
    private Timestamp updatedAt;

    @PrePersist
    public void generateId() {
        this.userId = "USR" + IdGenerator.generateRandomID();
    }
}
