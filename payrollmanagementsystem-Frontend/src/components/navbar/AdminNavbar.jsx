import React from "react";
import {
  Navbar as BootstrapNavbar,
  Nav,
  Button,
  NavDropdown,
} from "react-bootstrap";
import { Link } from "react-router-dom";
import "../css/AdminNavbar.css";
import axios from "axios";
import Logo from "../images/paynet.png";

const AdminNavbar = () => {
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
        onClick={() => (window.location.href = `${contextPath}/admin-home`)}
        style={{ cursor: "pointer" }}
      >
        <img
          src={Logo}
          alt="Logo"
          className="hr-nav-logo d-inline-block align-top mr-2"
        />
        PayNet
      </BootstrapNavbar.Brand>

      <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
      <BootstrapNavbar.Collapse id="basic-navbar-nav">
        <Nav className="mx-auto">
          <NavDropdown title="Roles" id="roles-dropdown">
            <NavDropdown.Item as={Link} to={`${contextPath}/add-role`}>
              Roles List
            </NavDropdown.Item>
            <NavDropdown.Item
              as={Link}
              to={`${contextPath}/manage-employee-roles`}
            >
              Employee Roles
            </NavDropdown.Item>
          </NavDropdown>
          <Nav.Link as={Link} to={`${contextPath}/add-designation`}>
            Designation
          </Nav.Link>
          <Nav.Link as={Link} to={`${contextPath}/add-leave-type`}>
            Leave
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

export default AdminNavbar;
