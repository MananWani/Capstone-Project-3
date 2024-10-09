import React, { useEffect, useState } from "react";
import { Container, Table, FormControl, InputGroup } from "react-bootstrap";
import axios from "axios";
import ModifiedFooter from "../footer/ModifiedFooter";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import '../css/EmployeeList.css';
import { useNavigate } from "react-router-dom";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";

const EmployeeList = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role === "Admin" || role==="HR Manager") {
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

  useEffect(() => {
    document.title = "Employee List";
  }, []);

  useEffect(() => {
    fetchEmployees();
  }, []);

  const filteredEmployees = employees.filter((employee) =>
    employee.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <>
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      <Container className="employee-list-container">
  <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
    Employee List
  </h4>
  <InputGroup className="search-bar mb-3">
      <FormControl
        placeholder="Search Employee"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
    </InputGroup>
  <div className="table-container">
    <Table bordered className="employee-table">
      <thead>
        <tr>
          <th>Fullname</th>
          <th>Email</th>
          <th>Mobile Number</th>
          <th>Designation</th>
          <th>Manager</th>
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
            .filter(employee => employee.isActive) 
            .map((employee) => (
              <tr key={employee.employeeId}>
                <td>{employee.fullName}</td>
                <td>{employee.email}</td>
                <td>{employee.mobileNumber}</td>
                <td>{employee.designation}</td>
                <td>{employee.manager}</td>
              </tr>
            ))
        )}
      </tbody>
    </Table>
  </div>
</Container>

      <ModifiedFooter />
    </>
  );
};

export default EmployeeList;
