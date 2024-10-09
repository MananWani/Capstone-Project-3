import React from "react";
import { Navbar as BootstrapNavbar, Nav, Button,NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import Logo from "../images/paynet.png";
import axios from "axios";
import "../css/ManagerNavbar.css";

const ManagerNavbar = () => {
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
        onClick={() => (window.location.href = `${contextPath}/manager-home`)}
        style={{ cursor: "pointer" }}
      >
        <img
          src={Logo}
          alt="Logo"
          className="manager-nav-logo d-inline-block align-top mr-2"
        />
        PayNet
      </BootstrapNavbar.Brand>

      <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
      <BootstrapNavbar.Collapse id="basic-navbar-nav">
        <Nav className="mx-auto">

          <NavDropdown title="Attendance" id="attendance-dropdown">
            <NavDropdown.Item as={Link} to={`${contextPath}/attendance`}>
              My Attendance
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/team-attendance`}>
                Team Attendance
            </NavDropdown.Item>
          </NavDropdown>
          
          <NavDropdown title="Employee" id="employee-dropdown">
            <NavDropdown.Item as={Link} to={`${contextPath}/view-employee`}>
              View Employee
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/employee-leave-requests`}>
                Leave Requests
            </NavDropdown.Item>
            <NavDropdown.Item as={Link} to={`${contextPath}/team-rating`}>
               My Team
            </NavDropdown.Item>
          </NavDropdown>

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

export default ManagerNavbar;
