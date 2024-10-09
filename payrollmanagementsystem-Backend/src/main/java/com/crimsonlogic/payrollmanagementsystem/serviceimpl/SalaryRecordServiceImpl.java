package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRequest;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.domain.SalaryRecord;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.TaxResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRequestRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRepository;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryRecordService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for handling salary records and related operations.
 *
 * @author abdulmanan
 */
@Service
@Slf4j
public class SalaryRecordServiceImpl implements SalaryRecordService {

    // Constants for different types of deductions
    private static final String PENALTY = "penaltyAmount";
    private static final String PF = "pfAmount";
    private static final String TAX = "taxAmount";

    private static final String EMPLOYEE_NOT_FOUND ="Employee not found.";

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeesRepository employeesRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRepository salaryRepository;

    // Constructor to initialize repositories
    SalaryRecordServiceImpl(SalaryRecordRepository salaryRecordRepository, EmployeesRepository employeesRepository,
                            AttendanceRepository attendanceRepository, SalaryRepository salaryRepository,
                            LeaveRequestRepository leaveRequestRepository,
                            LeaveTypeRepository leaveTypeRepository) {
        super();
        this.salaryRecordRepository = salaryRecordRepository;
        this.employeesRepository = employeesRepository;
        this.attendanceRepository = attendanceRepository;
        this.salaryRepository = salaryRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    public List<SalaryRecordDTO> getSalaryByEmployeeId(String employeeId) {
        log.info("inside getSalaryByEmployeeId method");
        // Fetch employee by ID and map their salary records to DTOs
        return employeesRepository.findById(employeeId)
                .map(employee -> {
                    List<SalaryRecord> salaryRecordList = salaryRecordRepository.findBySalaryRecordOfEmployee(employee);
                    return salaryRecordList.stream()
                            .map(salaryRecord -> {
                                SalaryRecordDTO salaryRecordDTO = Mapper.INSTANCE.entityToDtoSalaryRecord(salaryRecord);
                                // Set additional employee details in the DTO
                                salaryRecordDTO.setFullName(employee.getFullName());
                                salaryRecordDTO.setJoiningDate(employee.getJoiningDate());
                                salaryRecordDTO.setDesignation(employee.getDesignation().getDesignationName());
                                return salaryRecordDTO;
                            })
                            .toList();
                })
                .orElseGet(ArrayList::new); // Return empty list if employee not found
    }

    @Override
    public void releaseSalaryForEmployee(SalaryRecordDTO salaryRecordDTO)
            throws ResourceNotFoundException {
        log.info("inside releaseSalaryForEmployee method");
        // Validate employee existence before releasing salary
        Employees employee = employeesRepository.findById(salaryRecordDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND));

        // Map DTO to entity and save salary record
        SalaryRecord salaryRecord = Mapper.INSTANCE.dtoToEntityForSalaryRecord(salaryRecordDTO);
        salaryRecord.setSalaryRecordOfEmployee(employee);
        salaryRecordRepository.save(salaryRecord);
    }

    @Override
    public List<SalaryRecordDTO> getAllSalaries() {
        log.info("inside getAllSalaries method");
        // Retrieve all salary records and map to DTOs
        return salaryRecordRepository.findAll().stream()
                .map(salaryRecord -> {
                    SalaryRecordDTO salaryRecordDTO = new SalaryRecordDTO();
                    salaryRecordDTO.setEmployeeId(salaryRecord.getSalaryRecordOfEmployee().getEmployeeId());
                    salaryRecordDTO.setPayPeriodStart(salaryRecord.getPayPeriodStart());
                    salaryRecordDTO.setPayPeriodEnd(salaryRecord.getPayPeriodEnd());
                    return salaryRecordDTO;
                })
                .toList();
    }

    @Override
    public SalaryRecordDTO getSalaryByQuarter(String quarter)
            throws ResourceNotFoundException {
        log.info("inside getSalaryByQuarter method");
        LocalDate startDate;
        LocalDate endDate = switch (quarter.toLowerCase()) {
            case "quarter 1" -> {
                startDate = LocalDate.of(Year.now().getValue(), 1, 1);
                yield LocalDate.of(Year.now().getValue(), 3, 31);
            }
            case "quarter 2" -> {
                startDate = LocalDate.of(Year.now().getValue(), 4, 1);
                yield LocalDate.of(Year.now().getValue(), 6, 30);
            }
            case "quarter 3" -> {
                startDate = LocalDate.of(Year.now().getValue(), 7, 1);
                yield LocalDate.of(Year.now().getValue(), 9, 30);
            }
            case "quarter 4" -> {
                startDate = LocalDate.of(Year.now().getValue(), 10, 1);
                yield LocalDate.of(Year.now().getValue(), 12, 31);
            }
            default -> throw new ResourceNotFoundException("Invalid quarter: " + quarter);
        };

        // Fetch salary records for the specified quarter
        List<SalaryRecord> salaryRecords = salaryRecordRepository.findSalaryRecordsByQuarter(startDate, endDate);
        return getSalaryRecordDTO(startDate, endDate, salaryRecords);
    }

    @Override
    public TaxResponseDTO getTaxByQuarter(String quarter, String employeeId)
            throws ResourceNotFoundException {
        log.info("inside getTaxByQuarter method");
        // Validate employee existence
        Employees employee = employeesRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND));

        LocalDate startDate;
        LocalDate endDate = switch (quarter.toLowerCase()) {
            case "quarter 1" -> {
                startDate = LocalDate.of(Year.now().getValue(), 1, 1);
                yield LocalDate.of(Year.now().getValue(), 3, 31);
            }
            case "quarter 2" -> {
                startDate = LocalDate.of(Year.now().getValue(), 4, 1);
                yield LocalDate.of(Year.now().getValue(), 6, 30);
            }
            case "quarter 3" -> {
                startDate = LocalDate.of(Year.now().getValue(), 7, 1);
                yield LocalDate.of(Year.now().getValue(), 9, 30);
            }
            case "quarter 4" -> {
                startDate = LocalDate.of(Year.now().getValue(), 10, 1);
                yield LocalDate.of(Year.now().getValue(), 12, 31);
            }
            default -> throw new ResourceNotFoundException("Invalid quarter: " + quarter); // Handle invalid input
        };

        // Fetch salary records for the specified quarter and employee
        List<SalaryRecord> salaryRecords = salaryRecordRepository.findSalaryRecordsByQuarterAndEmployee(startDate, endDate, employee);

        // Calculate total gross salary, tax deducted, and net salary
        BigDecimal totalGrossSalary = salaryRecords.stream()
                .map(SalaryRecord::getGrossSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxDeducted = salaryRecords.stream()
                .map(SalaryRecord::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetSalary = salaryRecords.stream()
                .map(SalaryRecord::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return buildTaxResponseDTO(startDate, endDate, totalGrossSalary, taxDeducted, totalNetSalary);
    }

    // Build tax response DTO from calculated values
    private TaxResponseDTO buildTaxResponseDTO
    (LocalDate startDate, LocalDate endDate, BigDecimal grossSalary,
     BigDecimal taxDeducted, BigDecimal netSalary) {
        log.info("inside buildTaxResponseDTO method");
        TaxResponseDTO taxResponseDTO = new TaxResponseDTO();
        taxResponseDTO.setPayPeriodStart(startDate);
        taxResponseDTO.setPayPeriodEnd(endDate);
        taxResponseDTO.setGrossSalary(grossSalary);
        taxResponseDTO.setTaxDeducted(taxDeducted);
        taxResponseDTO.setNetSalary(netSalary);
        taxResponseDTO.setYear(Year.now());
        return taxResponseDTO;
    }

    // Get salary record DTO by aggregating salary records
    private static SalaryRecordDTO getSalaryRecordDTO
    (LocalDate startDate, LocalDate endDate, List<SalaryRecord> salaryRecords) {
        log.info("inside getSalaryRecordDTO method");
        SalaryRecordDTO dto = new SalaryRecordDTO();
        dto.setPayPeriodStart(startDate);
        dto.setPayPeriodEnd(endDate);
        dto.setGrossSalary(salaryRecords.stream().map(SalaryRecord::getGrossSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setNetSalary(salaryRecords.stream().map(SalaryRecord::getNetSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setPenaltyAmount(salaryRecords.stream().map(SalaryRecord::getPenaltyAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setPfAmount(salaryRecords.stream().map(SalaryRecord::getPfAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setBonusAmount(salaryRecords.stream().map(SalaryRecord::getBonusAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTaxAmount(salaryRecords.stream().map(SalaryRecord::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }

    @Override
    @Transactional
    public SalaryRecordDTO calculateSalaryForEmployee(String employeeId)
            throws ResourceNotFoundException {
        log.info("inside calculateSalaryForEmployee method");
        // Fetch employee and calculate various salary components
        Employees employee = getEmployeeById(employeeId);
        BigDecimal costToCompany = salaryRepository.findCostToCompany(employee);

        YearMonth yearMonth = YearMonth.now();
        BigDecimal monthlySalary = calculateMonthlySalary(costToCompany);
        BigDecimal perDaySalary = calculatePerDaySalary(monthlySalary, yearMonth.lengthOfMonth()-1);

        // Get the joining date of the employee
        LocalDate joiningDate = employee.getJoiningDate();

        // Count absences in the current month
        long absentCount = countAbsences(employee, yearMonth);

        // Check if the current month is the month of joining
        if (joiningDate.getYear() == yearMonth.getYear() && joiningDate.getMonth() == yearMonth.getMonth()) {
            // Calculate the number of days before the joining date in the current month
            int daysBeforeJoining = joiningDate.getDayOfMonth() - 1;
            absentCount += daysBeforeJoining;
        }

        BigDecimal penaltyAmount = calculatePenaltyAmount(absentCount, perDaySalary);
        BigDecimal pfAmount = calculatePfAmount(monthlySalary);
        BigDecimal taxAmount = calculateTax(monthlySalary);

        // Prepare deductions map
        Map<String, BigDecimal> deductions = Map.of(
                PENALTY, penaltyAmount,
                PF, pfAmount,
                TAX, taxAmount
        );

        // Calculate total bonuses and net salary
        BigDecimal totalBonus = calculateTotalBonus(employee, yearMonth);
        BigDecimal netSalary = calculateNetSalary(monthlySalary, deductions, totalBonus);

        return setSalaryRecordDTO(employee, yearMonth, monthlySalary, netSalary, deductions, totalBonus);
    }


    // Calculate total bonuses for the employee
    private BigDecimal calculateTotalBonus(Employees employee, YearMonth yearMonth) {
        log.info("inside calculateTotalBonus method");
        return calculateBirthdayBonus(employee, yearMonth)
                .add(calculateDiwaliBonus(yearMonth))
                .add(calculateMarriageBonus(employee, yearMonth));
    }

    // Calculate birthday bonus if applicable
    private BigDecimal calculateBirthdayBonus(Employees employee, YearMonth yearMonth) {
        log.info("inside calculateBirthdayBonus method");
        LocalDate birthday = employee.getDateOfBirth();
        return (birthday != null && birthday.getMonth() == yearMonth.getMonth()) ? BigDecimal.valueOf(1000) : BigDecimal.ZERO;
    }

    // Calculate Diwali bonus if applicable
    private BigDecimal calculateDiwaliBonus(YearMonth yearMonth) {
        log.info("inside calculateDiwaliBonus method");
        return (yearMonth.getMonth() == Month.NOVEMBER) ? BigDecimal.valueOf(2000) : BigDecimal.ZERO;
    }

    // Calculate marriage bonus if applicable
    private BigDecimal calculateMarriageBonus(Employees employee, YearMonth yearMonth) {
        log.info("inside calculateMarriageBonus method");
        LeaveType leaveType = leaveTypeRepository.findByTypeName("Marriage Leave");
        LeaveRequest marriageLeave = (leaveType != null) ? leaveRequestRepository
                .findByRequestByEmployeeAndStartDateAndTypeOfLeave(
                        employee,
                        yearMonth.getYear(),
                        yearMonth.getMonthValue(),
                        leaveType) : null;

        return (marriageLeave != null && "Approved".equalsIgnoreCase(marriageLeave.getStatus())) ? BigDecimal.valueOf(10000) : BigDecimal.ZERO;
    }

    // Calculate tax based on monthly salary
    private BigDecimal calculateTax(BigDecimal monthlySalary) {
        log.info("inside calculateTax method");
        BigDecimal annualSalary = monthlySalary.multiply(BigDecimal.valueOf(12));
        BigDecimal taxAmount;

        // Tax slabs
        if (annualSalary.compareTo(BigDecimal.valueOf(300000)) <= 0) {
            taxAmount = BigDecimal.ZERO;
        } else if (annualSalary.compareTo(BigDecimal.valueOf(300000)) > 0 && annualSalary.compareTo(BigDecimal.valueOf(600000)) <= 0) {
            taxAmount = annualSalary.subtract(BigDecimal.valueOf(300000)).multiply(BigDecimal.valueOf(0.05));
        } else if (annualSalary.compareTo(BigDecimal.valueOf(600000)) > 0 && annualSalary.compareTo(BigDecimal.valueOf(900000)) <= 0) {
            taxAmount = BigDecimal.valueOf(15000)
                    .add(annualSalary.subtract(BigDecimal.valueOf(600000)).multiply(BigDecimal.valueOf(0.10)));
        } else if (annualSalary.compareTo(BigDecimal.valueOf(900000)) > 0 && annualSalary.compareTo(BigDecimal.valueOf(1200000)) <= 0) {
            taxAmount = BigDecimal.valueOf(45000)
                    .add(annualSalary.subtract(BigDecimal.valueOf(900000)).multiply(BigDecimal.valueOf(0.15)));
        } else if (annualSalary.compareTo(BigDecimal.valueOf(1200000)) > 0 && annualSalary.compareTo(BigDecimal.valueOf(1500000)) <= 0) {
            taxAmount = BigDecimal.valueOf(90000)
                    .add(annualSalary.subtract(BigDecimal.valueOf(1200000)).multiply(BigDecimal.valueOf(0.20)));
        } else {
            taxAmount = BigDecimal.valueOf(150000)
                    .add(annualSalary.subtract(BigDecimal.valueOf(1500000)).multiply(BigDecimal.valueOf(0.20)));
        }

        // Return monthly tax amount
        return taxAmount.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }

    // Helper method to fetch employee by ID
    private Employees getEmployeeById(String employeeId) throws ResourceNotFoundException {
        log.info("inside getEmployeeById method");
        return employeesRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND));
    }

    // Calculate monthly salary based on cost to company
    private BigDecimal calculateMonthlySalary(BigDecimal costToCompany) {
        log.info("inside calculateMonthlySalary method");
        return costToCompany.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
    }

    // Calculate per day salary based on monthly salary and number of days
    private BigDecimal calculatePerDaySalary(BigDecimal monthlySalary, int daysInMonth) {
        log.info("inside calculatePerDaySalary method");
        return monthlySalary.divide(BigDecimal.valueOf(daysInMonth), RoundingMode.HALF_UP).setScale(2, RoundingMode.CEILING);
    }

    // Count number of absences for the employee in a month
    private Long countAbsences(Employees employee, YearMonth yearMonth) {
        log.info("inside countAbsences method");
        return attendanceRepository.findByEmployeeAndDate(employee, yearMonth.getMonthValue(), yearMonth.getYear())
                .stream()
                .filter(attendance -> "absent".equalsIgnoreCase(attendance.getStatus()))
                .count();
    }

    // Calculate penalty amount based on absences
    private BigDecimal calculatePenaltyAmount(Long absentCount, BigDecimal perDaySalary) {
        log.info("inside calculatePenaltyAmount method");
        return perDaySalary.multiply(BigDecimal.valueOf(absentCount));
    }

    // Calculate provident fund amount
    private BigDecimal calculatePfAmount(BigDecimal monthlySalary) {
        log.info("inside calculatePfAmount method");
        return monthlySalary.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
    }

    // Calculate net salary after deductions and adding bonuses
    private BigDecimal calculateNetSalary
    (BigDecimal monthlySalary, Map<String, BigDecimal> deductions, BigDecimal totalBonus) {
        log.info("inside calculateNetSalary method");
        return monthlySalary.subtract(deductions.get(PENALTY))
                .subtract(deductions.get(PF))
                .subtract(deductions.get(TAX))
                .add(totalBonus)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Save the salary record and return the DTO
    private SalaryRecordDTO setSalaryRecordDTO(Employees employee, YearMonth yearMonth, BigDecimal monthlySalary,
                                               BigDecimal netSalary, Map<String, BigDecimal> deductions,
                                               BigDecimal totalBonus) {
        log.info("inside setSalaryRecordDTO method");
        SalaryRecordDTO salaryRecordDTO = new SalaryRecordDTO();
        salaryRecordDTO.setPayPeriodStart(yearMonth.atDay(1));
        salaryRecordDTO.setPayPeriodEnd(yearMonth.atEndOfMonth());
        salaryRecordDTO.setGrossSalary(monthlySalary);
        salaryRecordDTO.setBonusAmount(totalBonus);
        salaryRecordDTO.setPenaltyAmount(deductions.get(PENALTY));
        salaryRecordDTO.setPfAmount(deductions.get(PF));
        salaryRecordDTO.setTaxAmount(deductions.get(TAX));
        salaryRecordDTO.setNetSalary(netSalary);
        salaryRecordDTO.setEmployeeId(employee.getEmployeeId());
        return salaryRecordDTO;
    }
}
