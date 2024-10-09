import React, { useEffect, useState } from "react";
import { Container, Table, Button,Modal,FormLabel, ModalFooter } from "react-bootstrap";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import "../css/LeaveRequestList.css";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";

const LeaveRequestList = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const location = useLocation();
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedRequestId, setSelectedRequestId] = useState(null);
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
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    document.title = "Leave Requests";
  }, []);

  useEffect(() => {
    const fetchLeaveRequests = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/leaverequest/getleaverequests",
          {
            params: { employeeId: employeeId },
          }
        );
        setLeaveRequests(response.data);
      } catch (error) {
        console.error("Error fetching leave requests:", error);
      }
    };

    fetchLeaveRequests();
  }, [employeeId]);

  const handleCancel = async (leaveRequestId) => {
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/leaverequest/cancelleaverequest",
        {
          leaveRequestId,
        }
      );

      setSuccessMessage("Leave request cancelled successfully!");
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/leaverequest/getleaverequests",
        {
          params: { employeeId: employeeId },
        }
      );
      setLeaveRequests(response.data);
    } catch (error) {
      console.log(error);
      setErrorMsg("Unexpected error, please try again.");
    }
  };

  const currentDate = new Date();
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1; 
  const currentDay = currentDate.getDate();

  const openModal = (leaveRequestId) => {
    setSelectedRequestId(leaveRequestId);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedRequestId(null);
  };

  const confirmCancel = () => {
    handleCancel(selectedRequestId);
    closeModal();
  };

  return (
    <>
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 leave-request-success">
          {successMessage}
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 leave-request-error">
          {errorMsg}
        </div>
      )}
      <Container className="leave-request-list-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Leave Request List
        </h4>
        <div className="table-container">
          <Table bordered className="leave-request-table">
            <thead>
              <tr>
                <th style={{ width: "120px" }}>Start Date</th>
                <th style={{ width: "120px" }}>Start Half</th>
                <th style={{ width: "120px" }}>End Date</th>
                <th style={{ width: "120px" }}>End Half</th>
                <th style={{ width: "120px" }}>Status</th>
                <th style={{ width: "180px" }}>Leave Type</th>
                <th>Description</th>
                <th style={{ width: "150px" }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {leaveRequests.length === 0 ? (
                <tr>
                  <td colSpan="8" className="text-center">
                    No rows found
                  </td>
                </tr>
              ) : (
                leaveRequests.map((request) => {
                  const [year, month, day] = request.startDate
                    .split("-")
                    .map(Number);

                  const isPastDate =
                    year < currentYear ||
                    (year === currentYear && month < currentMonth) ||
                    (year === currentYear &&
                      month === currentMonth &&
                      day < currentDay);
                  return (
                    <tr key={request.leaveRequestId}>
              <td>{request.startDate}</td>
              <td>{request.startHalf}</td>
              <td>{request.endDate}</td>
              <td>{request.endHalf}</td>
              <td>{request.status}</td>
              <td>{request.typeOfLeave}</td>
              <td>
                {request.description
                  ? request.description
                  : "Pending response from manager."}
              </td>
              <td>
                {!isPastDate &&
                request.status !== "Cancelled" &&
                request.status !== "Rejected" ? (
                  <Button
                    className="leave-request-list-button"
                    onClick={() => openModal(request.leaveRequestId)}
                  >
                    Cancel
                  </Button>
                ) : (
                  <span>No Action</span>
                )}
              </td>
            </tr>
                  );
                })
              )}
            </tbody>
          </Table>
          <Modal show={showModal} onHide={closeModal} centered>
          <Modal.Header closeButton>
          <Modal.Title>Confirm Cancellation</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormLabel>
          Are you sure you want to cancel this leave request?
          </FormLabel>
        </Modal.Body>
        <ModalFooter>

        <Button className='leave-request-list-button' onClick={confirmCancel}>Yes</Button>
        </ModalFooter>
      </Modal>
        </div>
      </Container>
      <ModifiedFooter />
    </>
  );
};

export default LeaveRequestList;
