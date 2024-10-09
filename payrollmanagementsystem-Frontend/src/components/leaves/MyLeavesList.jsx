import React, { useEffect, useState } from "react";
import { Container, Table } from "react-bootstrap";
import axios from "axios";
import "../css/MyLeavesList.css"; 
import HrManagerNavbar from "../navbar/HrManagerNavbar"; 
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar"; 
import ModifiedFooter from "../footer/ModifiedFooter"; 
import { useNavigate } from "react-router-dom";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";

const MyLeavesList = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const [leaveBalances, setLeaveBalances] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role === "Admin") {
      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");
      navigate(`${contextPath}/login`, {
        state: { error: "Unauthorized access" },
      });
    }
  }, [fullName, navigate, contextPath, role]);
  
  useEffect(() => {
    document.title = "My Leaves";
  }, []);

  useEffect(() => {
    const fetchLeaveBalances = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/leaverecord/getleaverecord",
          {
            params: { employeeId: employeeId },
          }
        );
        setLeaveBalances(response.data);
      } catch (error) {
        console.error("Error fetching leave balances:", error);
      }
    };

    fetchLeaveBalances();
  }, [employeeId]);

  return (
    <>
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      <Container className="my-leaves-list-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          My Leaves List
        </h4>
          <Table bordered className="my-leaves-table">
            <thead>
              <tr>
                <th>Type of Leave</th>
                <th style={{width:'120px'}}>Total</th>
                <th style={{width:'120px'}}>Used</th>
                <th style={{width:'120px'}}>Remaining</th>
              </tr>
            </thead>
            <tbody>
              {leaveBalances.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center">
                    No leaves available
                  </td>
                </tr>
              ) : (
                leaveBalances.map((leave) => (
                  <tr key={leave.typeOfLeave}>
                    <td>{leave.typeOfLeave}</td>
                    <td>{leave.totalLeaves}</td>
                    <td>{leave.usedLeaves}</td>
                    <td>{leave.remainingLeaves}</td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
      </Container>
      <ModifiedFooter />
    </>
  );
};

export default MyLeavesList;
