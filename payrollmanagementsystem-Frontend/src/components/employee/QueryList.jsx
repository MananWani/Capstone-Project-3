import React, { useEffect, useState } from 'react';
import { Container, Table } from 'react-bootstrap';
import axios from 'axios';
import "../css/QueryList.css";
import HrManagerNavbar from '../navbar/HrManagerNavbar';
import ManagerNavbar from '../navbar/ManagerNavbar';
import EmployeeNavbar from '../navbar/EmployeeNavbar';
import { useLocation, useNavigate } from 'react-router-dom';
import ModifiedFooter from '../footer/ModifiedFooter';

const QueryList = () => {
    const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
    const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
    const location = useLocation();
  const employeeId = localStorage.getItem("employeeId");
  const [queries, setQueries] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const role = localStorage.getItem("role");

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role === "Admin" || role==="Payroll Specialist") {
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
    if (location.state && location.state.successMessage) {
      setSuccessMessage(location.state.successMessage);
    }
  }, [location.state]);

  useEffect(() => {
    const fetchQueries = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/queries/getqueries", 
          {
            params: { employeeId: employeeId },
          }
        );
        setQueries(response.data);
      } catch (error) {
        console.error("Error fetching queries:", error);
        setQueries([]);
      }
    };

    fetchQueries();
  }, [employeeId]);

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  useEffect(() => {
    document.title = "Query Records";
  }, []);
  return (
    <>
    {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
       {successMessage && (
        <div className="alert alert-success text-center mb-3 query-list-success">
          {successMessage}
        </div>
      )}
    <Container className="query-list-container">
      <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
        Query List
      </h4>
      <div className="query-list-scroll">
      <Table bordered className="query-list-table">
        <thead>
          <tr>
            <th style={{width:'150px'}}>Pay Period Start</th>
            <th style={{width:'140px'}}>Pay Period End</th>
            <th>Description</th>
            <th style={{width:'140px'}}>Status</th>
            <th>Comment</th>
          </tr>
        </thead>
        <tbody>
          {queries.length === 0 ? (
            <tr>
              <td colSpan="5" className="text-center">
                No queries available
              </td>
            </tr>
          ) : (
            queries.map((query) => (
              <tr key={query.queryId}> 
                <td>{query.payPeriodStart}</td>
                <td>{query.payPeriodEnd}</td>
                <td>{query.queryDescription}</td>
                <td>{query.queryStatus}</td>
                <td>{query.comment}</td>
              </tr>
            ))
          )}
        </tbody>
      </Table>
      </div>
    </Container>
    <ModifiedFooter/>
    </>
  );
};

export default QueryList;
