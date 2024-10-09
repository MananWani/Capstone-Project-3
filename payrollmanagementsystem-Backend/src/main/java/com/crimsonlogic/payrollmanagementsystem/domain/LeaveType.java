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
@Table(name="leave_type")
@Getter
@Setter
@NoArgsConstructor
public class LeaveType {
    @Id
    @Column(name = "type_id",length = 50)
    private String typeId;

    @Column(name = "type_name",length = 50)
    private String typeName;

    @Column(name="number_of_leaves")
    private Integer numberOfLeaves;

    @PrePersist
    public void generateId() {
        this.typeId = "TYP" + IdGenerator.generateRandomID();
    }
}
