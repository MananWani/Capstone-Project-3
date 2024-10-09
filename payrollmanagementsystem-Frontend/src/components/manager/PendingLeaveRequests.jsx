import React, { useEffect, useState, useCallback } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import axios from "axios";
import { Formik } from "formik";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import "../css/PendingLeaveRequests.css";
import ModifiedFooter from "../footer/ModifiedFooter";
import ManagerNavbar from "../navbar/ManagerNavbar";

const PendingLeaveRequests = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const [modalShow, setModalShow] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role !== "Manager") {
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
    document.title = "Pending Requests";
  }, []);

  const fetchLeaveRequests = useCallback(async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/leaverequest/getpendingrequests",
        {
          params: { employeeId: employeeId },
        }
      );
      setLeaveRequests(response.data);
    } catch (error) {
      console.error("Error fetching leave requests:", error);
      setLeaveRequests([]);
    }
  }, [employeeId]);

  useEffect(() => {
    fetchLeaveRequests();
  }, [fetchLeaveRequests]);

  const handleUpdateRequest = async (values, { resetForm }) => {
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/leaverequest/updateleaverequest",
        {
          leaveRequestId: selectedRequest.leaveRequestId,
          status: values.status,
          description: values.description,
          noOfDays: selectedRequest.noOfDays,
          typeOfLeave:selectedRequest.typeOfLeave,
          typeId:selectedRequest.typeId,
          employeeId:selectedRequest.employeeId
        }
      );
      setSuccessMessage("Leave request updated successfully!");
      handleClose();
      resetForm();
      fetchLeaveRequests();
    } catch(error) {
      console.log(error);
      setErrorMsg("Failed to update leave request.");
      handleClose();
    }
  };

  const handleShow = (request) => {
    console.log(request);
    setSelectedRequest(request);
    setModalShow(true);
  };

  const handleClose = () => {
    setModalShow(false);
    setSelectedRequest(null);
  };

  const validationSchema = Yup.object().shape({
    status: Yup.string().required("Status is required."),
    description: Yup.string()
      .required("Description is required.")
      .min(5, "Description must be at least 5 characters long.")
      .max(100, "Description cannot exceed 100 characters."),
  });
  

  return (
    <>
      {role === "Manager" && <ManagerNavbar />}
      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 pending-leave-success">
          {successMessage}
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 pending-leave-error">
          {errorMsg}
        </div>
      )}
      <Container className="pending-leave-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Leave Request List
        </h4>
        <div className="pending-leave-scroll">
          <Table bordered className="pending-leave-table">
            <thead>
              <tr>
                <th style={{ width: "140px" }}>Start Date</th>
                <th style={{ width: "120px" }}>Start Half</th>
                <th style={{ width: "140px" }}>End Date</th>
                <th style={{ width: "120px" }}>End Half</th>
                <th style={{ width: "120px" }}>Status</th>
                <th style={{ width: "200px" }}>Leave Type</th>
                <th>Days</th>
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
                leaveRequests.map((request) => (
                  <tr key={request.leaveRequestId}>
                    <td>{request.startDate}</td>
                    <td>{request.startHalf}</td>
                    <td>{request.endDate}</td>
                    <td>{request.endHalf}</td>
                    <td>{request.status}</td>
                    <td>{request.typeOfLeave}</td>
                    <td>{request.noOfDays}</td>
                    <td>
                      <Button
                        className="pending-leave-button"
                        onClick={() => handleShow(request)}
                      >
                        Update
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </div>
      </Container>

      {/* Modal for Updating Leave Request */}
      <Modal show={modalShow} onHide={handleClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>Update Leave Request</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Formik
            initialValues={{ status: "", description: "" }}
            validationSchema={validationSchema}
            onSubmit={handleUpdateRequest}
          >
            {({
              handleSubmit,
              handleChange,
              handleBlur,
              values,
              errors,
              touched,
            }) => (
              <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formBasicSelect">
                  <Form.Label style={{ fontWeight:"bold" }}>Status <span className="label-asterisk">*</span></Form.Label>
                  <Form.Control
                    as="select"
                    name="status"
                    value={values.status}
                    onChange={handleChange}
                    onBlur={handleBlur} // Add onBlur
                    isInvalid={touched.status && !!errors.status}
                  >
                    <option value="">Select</option>
                    <option value="Approved">Approved</option>
                    <option value="Rejected">Rejected</option>
                  </Form.Control>
                  <Form.Control.Feedback type="invalid">
                    {touched.status && (errors.status || "Status is required")}
                  </Form.Control.Feedback>
                </Form.Group>

                <Form.Group controlId="formBasicDescription">
                  <Form.Label style={{ marginTop: "5px", fontWeight:"bold" }}>
                    Description <span className="label-asterisk">*</span>
                  </Form.Label>
                  <Form.Control
                    type="text"
                    name="description"
                    value={values.description}
                    onChange={handleChange}
                    onBlur={handleBlur} // Add onBlur
                    isInvalid={touched.description && !!errors.description}
                  />
                  <Form.Control.Feedback type="invalid">
                    {touched.description &&
                      (errors.description || "Description is required")}
                  </Form.Control.Feedback>
                </Form.Group>
                <div className="text-center">
                  <Button
                    className="pending-leave-button"
                    type="submit"
                    style={{ marginTop: "8px" }}
                    disabled={
                      !(
                        !errors.status &&
                        !errors.description &&
                        values.status &&
                        values.description
                      )
                    }
                  >
                    Submit
                  </Button>
                </div>
              </Form>
            )}
          </Formik>
        </Modal.Body>
      </Modal>

      <ModifiedFooter />
    </>
  );
};

export default PendingLeaveRequests;
