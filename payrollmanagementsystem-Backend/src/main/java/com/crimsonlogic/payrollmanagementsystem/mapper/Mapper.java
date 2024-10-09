package com.crimsonlogic.payrollmanagementsystem.mapper;

import com.crimsonlogic.payrollmanagementsystem.domain.*;
import com.crimsonlogic.payrollmanagementsystem.dto.*;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@org.mapstruct.Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface Mapper {
    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    List<RolesDTO> entityToDtoForRoles(List<Roles> roles);

    List<DesignationDTO> entityToDtoForDesignations(List<Designation> designations);

    @Mapping(target = "designation", ignore=true)
    @Mapping(target = "manager",ignore = true)
    @Mapping(target = "user",ignore = true)
    Employees dtoToEntityForEmployees(EmployeesDTO newEmployee);

    @Mapping(target = "designation", ignore=true)
    @Mapping(target = "manager",ignore = true)
    @Mapping(target = "role",ignore = true)
    @Mapping(target = "email",ignore = true)
    @Mapping(target = "passwordHash",ignore = true)
    EmployeesDTO entityToDtoForEmployees(Employees employee);

    @Mapping(target = "attendanceByEmployee",ignore = true)
    AttendanceDTO entityToDtoForAttendance(Attendance attendance);

    List<LeaveTypeDTO> entityToDtoForLeaveTypes(List<LeaveType> leaveTypes);

    @Mapping(target = "fullName",ignore = true)
    SalaryDTO entityToDtoForSalary(Salary salaryRecord);

    @Mapping(target = "fullName",ignore = true)
    @Mapping(target = "joiningDate",ignore = true)
    @Mapping(target = "designation",ignore = true)
    @Mapping(target = "employeeId",ignore = true)
    SalaryRecordDTO entityToDtoSalaryRecord(SalaryRecord salaryRecord);

    @Mapping(target = "requestByEmployee",ignore = true)
    @Mapping(target = "typeOfLeave",ignore = true)
    @Mapping(target = "status",ignore = true)
    @Mapping(target = "noOfDays",ignore = true)
    LeaveRequest dtoToEntityForLeaveRequest(LeaveRequestDTO leaveRequestDTO);

    @Mapping(target = "requestByEmployee",ignore = true)
    @Mapping(target = "typeOfLeave",ignore = true)
    @Mapping(target = "employeeId",source = "requestByEmployee.employeeId")
    @Mapping(target = "typeId",source = "typeOfLeave.typeId")
    LeaveRequestDTO entityToDtoForLeaveRequest(LeaveRequest request);

    @Mapping(target = "leaveForEmployee",ignore = true)
    @Mapping(target = "typeOfLeave",ignore = true)
    LeaveRecordDTO entityToDtoForLeaveRecord(LeaveRecord leaveRecord);

    @Mapping(target = "salaryRecordOfEmployee",ignore = true)
    SalaryRecord dtoToEntityForSalaryRecord(SalaryRecordDTO salaryRecordDTO);

    @Mapping(target = "employeeId",source = "queryByEmployee.employeeId")
    @Mapping(target = "salaryRecordId",source = "queryForSalaryRecord.salaryRecordId")
    @Mapping(target = "payPeriodStart",source = "queryForSalaryRecord.payPeriodStart")
    @Mapping(target = "payPeriodEnd",source = "queryForSalaryRecord.payPeriodEnd")
    QueriesDTO entityToDtoForQueries(Queries query);

    @Mapping(target = "logForEmployee",source = "logForEmployee.employeeId")
    LoginLogsDTO entityTODtoForLoginLogs(LoginLogs logs);
}
