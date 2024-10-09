import React from "react";
import { Navbar as BootstrapNavbar, Nav, Button,NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import Logo from "../images/paynet.png";
import axios from "axios";
import "../css/EmployeeNavbar.css";

const EmployeeNavbar = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";

  const handleLogout = async () => {
    const logId = localStorage.getItem("logId");

    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/loginlogs/setlogoutlog",
        null,
        {
          params: { logId },
        }
      );

      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");

      window.location.href = `${contextPath}/`;
    } catch (error) {
      console.error("Logout error:", error);
    }
  };

  return (
    <BootstrapNavbar className="navbar" expand="lg">
      <BootstrapNavbar.Brand
        as="div"
        className="navbar-brand"
        onClick={() => (window.location.href = `${contextPath}/employee-home`)}
        style={{ cursor: "pointer" }}
      >
        <img
          src={Logo}
          alt="Logo"
          className="employee-nav-logo d-inline-block align-top mr-2"
        />
        PayNet
      </BootstrapNavbar.Brand>

      <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
      <BootstrapNavbar.Collapse id="basic-navbar-nav">
        <Nav className="mx-auto">
          <Nav.Link as={Link} to={`${contextPath}/attendance`}>
            Attendance
          </Nav.Link>
          
          <Nav.Link as={Link} to={`${contextPath}/view-employee`}>
          Employees
          </Nav.Link>

          <NavDropdown title="Leave" id="leave-dropdown">
            <NavDropdown.Item as={Link} to={`${contextPath}/apply-leave`}>
              Apply Leave
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/leave-requests`}>
              My Leave Requests
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/my-leaves`}>
              My Leaves
            </NavDropdown.Item>
          </NavDropdown>

          <NavDropdown title="Salary" id="salary-dropdown">
            <NavDropdown.Item as={Link} to={`${contextPath}/my-salary`}>
              My Salary
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/tax-report`}>
              Tax Documents
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/my-queries`}>
                My Queries
            </NavDropdown.Item>
          </NavDropdown>

          <Nav.Link as={Link} to={`${contextPath}/profile`}>
            Profile
          </Nav.Link>
        </Nav>

        <Button
          onClick={handleLogout} 
          className={`logout-button ml-3`}
        >
          Logout
        </Button>
      </BootstrapNavbar.Collapse>
    </BootstrapNavbar>
  );
};

export default EmployeeNavbar;
