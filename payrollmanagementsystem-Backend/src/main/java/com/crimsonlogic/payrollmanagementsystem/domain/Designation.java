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
@Table(name="designation")
@Getter
@Setter
@NoArgsConstructor
public class Designation {
    @Id
    @Column(name = "designation_id",length = 50)
    private String designationId;

    @Column(name = "designation_name",length = 70)
    private String designationName;

    @PrePersist
    public void generateId() {
        this.designationId = "DSG" + IdGenerator.generateRandomID();
    }
}
