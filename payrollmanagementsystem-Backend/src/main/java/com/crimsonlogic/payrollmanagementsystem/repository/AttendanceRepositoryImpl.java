package com.crimsonlogic.payrollmanagementsystem.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the custom repository interface for Attendance.
 * This class provides methods for managing Attendance data,
 * including inserting records for absent employees.
 *
 * @author abdulmanan
 */
@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Inserts attendance records for employees who were absent.
     * This method executes a native SQL command to perform the operation.
     */
    @Override
    @Transactional
    public void insertAbsentAttendance() {
        entityManager.createNativeQuery("DO $$ BEGIN PERFORM insert_absent_attendance(); END $$;")
                .executeUpdate();
    }
}
