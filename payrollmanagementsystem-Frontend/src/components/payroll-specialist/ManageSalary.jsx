import React, { useEffect, useState } from "react";
import {
  Container,
  Table,
  FormControl,
  InputGroup,
  Modal,
  Button,
} from "react-bootstrap";
import axios from "axios";
import ModifiedFooter from "../footer/ModifiedFooter";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import { useNavigate } from "react-router-dom";
import "../css/ManageSalary.css";

const ManageSalary = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [salaries, setSalaries] = useState([]);
  const [calculatedSalary, setCalculatedSalary] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [month, setMonth] = useState(new Date().getMonth() + 1); // 1-12
  const [year, setYear] = useState(new Date().getFullYear());

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role !== "Payroll Specialist") {
      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");
      navigate(`${contextPath}/login`, {
        state: { error: "Unauthorized access" },
      });
    }
  }, [fullName, navigate, contextPath, role]);

  const fetchEmployees = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/employees/getallemployees"
      );
      setEmployees(response.data);
    } catch {
      setEmployees([]);
    }
  };

  const fetchSalaries = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/salaryrecord/getallsalaries"
      );
      setSalaries(response.data);
    } catch {
      setSalaries([]);
    }
  };

  useEffect(() => {
    document.title = "Manage Salary";
    fetchEmployees();
    fetchSalaries();
  }, []);

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  const filteredEmployees = employees.filter((employee) =>
    employee.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleCalculateSalary = async (employee) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/payrollmanagementsystem/salaryrecord/calculatesalary`,
        {
          params: { employeeId: employee.employeeId },
        }
      );
      setCalculatedSalary(response.data);
      setSelectedEmployee(employee);
      setShowModal(true);
    } catch (error) {
      console.error("Error fetching calculated salary:", error);
    }
  };

  const handleReleaseSalary = async () => {
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/salaryrecord/releasesalary",
        {
          payPeriodStart: calculatedSalary.payPeriodStart,
          grossSalary: calculatedSalary.grossSalary,
          pfAmount: calculatedSalary.pfAmount,
          bonusAmount: calculatedSalary.bonusAmount,
          payPeriodEnd: calculatedSalary.payPeriodEnd,
          penaltyAmount: calculatedSalary.penaltyAmount,
          taxAmount: calculatedSalary.taxAmount,
          netSalary: calculatedSalary.netSalary,
          employeeId: calculatedSalary.employeeId,
        }
      );
      setSuccessMessage("Salary released successfully.");
      fetchSalaries();
      setShowModal(false);
    } catch (error) {
      setErrorMsg("Unexpected error, please try again.");
      setShowModal(false);
    }
  };

  const isSalaryReleased = (employeeId) => {
    return salaries.some(
      (salary) =>
        salary.employeeId === employeeId &&
        new Date(salary.payPeriodStart).getMonth() + 1 === month &&
        new Date(salary.payPeriodStart).getFullYear() === year
    );
  };

  const isLastWorkingDay = () => {
    /* const today = new Date();
    const day = today.getDay();

    if (day === 0 || day === 6) return false;

    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    if (today.getDate() === lastDay.getDate()) return true;

    const nextDay = new Date(today);
    nextDay.setDate(today.getDate() + 1);

    while (nextDay.getDay() === 0 || nextDay.getDay() === 6) {
      nextDay.setDate(nextDay.getDate() + 1);
    }

    return nextDay.getMonth() !== today.getMonth(); */
    return true;
  };

  return (
    <>
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 manage-salary-success">
          {successMessage}
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 manage-salary-error">
          {errorMsg}
        </div>
      )}
      <Container className="manage-salary-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Employee List
        </h4>
        <InputGroup className="search-bar mb-3">
          <FormControl
            placeholder="Search Employee"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />

          <FormControl
            as="select"
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
            style={{ marginLeft: "5px" }}
          >
            {[...Array(12).keys()].map((m) => (
              <option key={m + 1} value={m + 1}>
                {new Date(0, m).toLocaleString("default", { month: "long" })}
              </option>
            ))}
          </FormControl>

          <FormControl
    as="select"
    value={year}
    onChange={(e) => setYear(e.target.value)}
    style={{ marginLeft: "20px" }}
  >
    <option value="" disabled>Select Year</option>
    {Array.from({ length: 21 }, (_, index) => (
      <option key={index} value={new Date().getFullYear() + index}>
        {new Date().getFullYear() - index}
      </option>
    ))}
  </FormControl>
        </InputGroup>

        <div className="manage-salary-container">
          <Table bordered className="manage-salary-table">
            <thead>
              <tr>
                <th>Fullname</th>
                <th>Email</th>
                <th>Designation</th>
                <th>Salary</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.length === 0 ? (
                <tr>
                  <td colSpan="5" className="text-center">
                    No rows found
                  </td>
                </tr>
              ) : (
                filteredEmployees
                  .filter((employee) => employee.isActive)
                  .map((employee) => (
                    <tr key={employee.employeeId}>
                      <td>{employee.fullName}</td>
                      <td>{employee.email}</td>
                      <td>{employee.designation}</td>
                      <td>
                        {isLastWorkingDay() ? (
                          isSalaryReleased(employee.employeeId) ? (
                            "Salary Released"
                          ) : month === new Date().getMonth() + 1 &&
                            year === new Date().getFullYear() ? (
                            <Button
                              className="salary-update-button"
                              onClick={() => handleCalculateSalary(employee)}
                            >
                              Calculate
                            </Button>
                          ) : (
                            "No Action"
                          )
                        ) : (
                          "No Action"
                        )}
                      </td>
                    </tr>
                  ))
              )}
            </tbody>
          </Table>
        </div>
      </Container>

      <ModifiedFooter />

      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>
            Calculated Salary for {selectedEmployee?.fullName}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {calculatedSalary ? (
            <div className="row">
              <div className="col-md-6">
                <p>Pay Period Start: {calculatedSalary.payPeriodStart}</p>
                <p>Gross Salary: {calculatedSalary.grossSalary}</p>
                <p>PF Amount: {calculatedSalary.pfAmount}</p>
                <p>Bonus Amount: {calculatedSalary.bonusAmount}</p>
              </div>
              <div className="col-md-6">
                <p>Pay Period End: {calculatedSalary.payPeriodEnd}</p>
                <p>Penalty Amount: {calculatedSalary.penaltyAmount}</p>
                <p>Tax Amount: {calculatedSalary.taxAmount}</p>
                <p>Net Salary: {calculatedSalary.netSalary}</p>
              </div>
            </div>
          ) : (
            <p>Loading salary details...</p>
          )}
          
        </Modal.Body>

        <Modal.Footer>
        <Button
            style={{marginTop:'8px'}}
            className="salary-update-button"
            onClick={handleReleaseSalary}
          >
            Release
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default ManageSalary;
