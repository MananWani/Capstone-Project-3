package com.crimsonlogic.payrollmanagementsystem.domain;

import com.crimsonlogic.payrollmanagementsystem.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author abdulmanan
 *
 */
@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
public class Roles {
    @Id
    @Column(name="role_id",length = 50)
    private String roleId;

    @Column(name="role_name",length = 30)
    private String roleName;

    @PrePersist
    public void generateId() {
        this.roleId = "ROL" + IdGenerator.generateRandomID();
    }
}
