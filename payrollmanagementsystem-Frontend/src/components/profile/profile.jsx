import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Button, Table, Modal } from 'react-bootstrap';
import HrManagerNavbar from '../navbar/HrManagerNavbar';
import PayrollSpecialistNavbar from '../navbar/PayrollSpecialistNavbar';
import ManagerNavbar from '../navbar/ManagerNavbar';
import EmployeeNavbar from '../navbar/EmployeeNavbar';
import ModifiedFooter from "../footer/ModifiedFooter";
import { useNavigate, useLocation } from "react-router-dom";
import '../css/Profile.css';

const Profile = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const location = useLocation();
  const navigate = useNavigate();
  const [user, setUser] = useState({});
  const employeeId = localStorage.getItem("employeeId");
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");
  const [successMessage, setSuccessMessage] = useState("");
  const [loginLogs, setLoginLogs] = useState([]);
  const [showModal, setShowModal] = useState(false);

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
    if (location.state && location.state.successMessage) {
      setSuccessMessage(location.state.successMessage);
    }
  }, [location.state]);

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/employees/getemployee",
          {
            params: { employeeId: employeeId },
          }
        );
        setUser(response.data);
      } catch (error) {
        console.log("Error fetching the details: " + error);
      }
    };

    fetchUserData();
  }, [employeeId]);

  useEffect(() => {
    document.title = "Profile";
  }, []);

  const fetchLoginLogs = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/loginlogs/getlogs",
        {
          params: { employeeId: employeeId },
        }
      );
      setLoginLogs(response.data);
    } catch (error) {
      console.log("Error fetching login logs: " + error);
    }
  };

  const handleShowModal = () => {
    fetchLoginLogs();
    setShowModal(true);
  };

  return (
    <>
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 profile-success">
          {successMessage}
        </div>
      )}
      <Container className="profile-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          My Profile
        </h4>
        <Table bordered className='profile-table'>
          <thead>
            <tr>
              <th colSpan="2" className="text-center">
                User Information
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th>Full Name</th>
              <td>{user.fullName}</td>
            </tr>
            <tr>
              <th>Email Address</th>
              <td>{user.email}</td>
            </tr>
            <tr>
              <th>Mobile Number</th>
              <td>{user.mobileNumber}</td>
            </tr>
            <tr>
              <th>Designation</th>
              <td>{user.designation}</td>
            </tr>
            <tr>
              <th>Manager</th>
              <td>{user.manager}</td>
            </tr>
            <tr>
              <th>History</th>
              <td><Button
          className='login-logs-button'
          onClick={handleShowModal}
        >
          Login History
        </Button></td>
            </tr>
          </tbody>
        </Table>

        <Button
          className='profile-button'
          onClick={() => navigate(`${contextPath}/change-password`, {
            state: { email: user.email },
          })}
        >
          Change Password
        </Button>
      </Container>

      {/* Login History Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Login History</Modal.Title>
        </Modal.Header>
        <Modal.Body>
        <div className="logs-table-scroll">
          <Table bordered className="logs-table">
            <thead>
              <tr>
                <th>Login Time</th>
                <th>Logout Time</th>
              </tr>
            </thead>
            <tbody>
              {loginLogs.length > 0 ? (
                loginLogs.map((log, index) => (
                  <tr key={index}>
                    <td>{new Date(log.loginTime).toLocaleString()}</td>
                    <td>{log.logoutTime ? new Date(log.logoutTime).toLocaleString() : "None"}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="2" className="text-center">No login history available.</td>
                </tr>
              )}
            </tbody>
          </Table>
          </div>
        </Modal.Body>
        
      </Modal>

      <ModifiedFooter />
    </>
  );
};

export default Profile;
