import React, { useCallback, useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import axios from "axios";
import "../css/ManageQueries.css";
import { useNavigate } from "react-router-dom";
import ModifiedFooter from "../footer/ModifiedFooter";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";

const ManageQueries = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const [queries, setQueries] = useState([]);
  const [successMessage, setSuccessMessage] = useState("");
  const role = localStorage.getItem("role");
  const [showModal, setShowModal] = useState(false);
  const [currentQuery, setCurrentQuery] = useState(null);
  const [status, setStatus] = useState("");
  const [comment, setComment] = useState("");
  const [isFormValid, setIsFormValid] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

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

  const fetchQueries = useCallback(async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/queries/getallqueries"
      );
      setQueries(response.data);
    } catch (error) {
      console.error("Error fetching queries:", error);
      setQueries([]);
    }
  }, []);

  useEffect(() => {
    fetchQueries();
  }, [fetchQueries]);

  const handleEditClick = (query) => {
    setCurrentQuery(query);
    setStatus(query.queryStatus);
    setComment(query.comment);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setCurrentQuery(null);
    setStatus("");
    setComment("");
  };

  const handleStatusChange = (e) => {
    setStatus(e.target.value);
    validateForm(e.target.value, comment);
  };

  const handleCommentChange = (e) => {
    setComment(e.target.value);
    validateForm(status, e.target.value);
  };

  const validateForm = (status, comment) => {
    if (status && comment) {
      setIsFormValid(true);
    } else {
      setIsFormValid(false);
    }
  };

  useEffect(() => {
    document.title = "Manage Queries";
  }, []);

  const handleUpdate = async () => {
    if (currentQuery) {
      try {
        await axios.post(
          `http://localhost:8080/payrollmanagementsystem/queries/responsetoquery`,
          {
            queryId: currentQuery.queryId,
            queryStatus: status,
            comment: comment,
          }
        );
        setShowModal(false);
        setSuccessMessage("Query updated successfully.");
        fetchQueries();
      } catch (error) {
        console.error("Error updating query:", error);
        setErrorMsg("Unexpected error, please try again.");
        setShowModal(false);
      }
    }
  };

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
  return (
    <>
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 manage-query-success">
          {successMessage}
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 manage-query-error">
          {errorMsg}
        </div>
      )}
      <Container className="manage-query-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Query List
        </h4>
        <div className="manage-query-scroll">
        <Table bordered className="manage-query-table">
          <thead>
            <tr>
              <th style={{ width: "150px" }}>Pay Period Start</th>
              <th style={{ width: "140px" }}>Pay Period End</th>
              <th style={{ width: "300px" }}>Description</th>
              <th style={{ width: "120px" }}>Status</th>
              <th style={{ width: "250px" }}>Comment</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {queries.length === 0 ? (
              <tr>
                <td colSpan="6" className="text-center">
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
                  <td>
                    {query.queryStatus === "Resolved" ? (
                      <span>No Action</span>
                    ) : (
                      <Button
                        className="manage-query-button"
                        onClick={() => handleEditClick(query)}
                      >
                        Edit
                      </Button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
        </div>
      </Container>

      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Edit Query</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="formStatus">
              <Form.Label>
                Status<span className="label-asterisk">*</span>
              </Form.Label>
              <Form.Select
                value={status}
                onChange={handleStatusChange}
                required
              >
                <option value="">Select Status</option>
                <option value="In Progress">In Progress</option>
                <option value="Resolved">Resolved</option>
              </Form.Select>
            </Form.Group>
            <Form.Group controlId="formComment">
              <Form.Label style={{ marginTop: "10px" }}>
                Comment<span className="label-asterisk">*</span>
              </Form.Label>
              <Form.Control
                type="text"
                placeholder="Describe your comment (5-50 characters)"
                value={comment}
                onChange={handleCommentChange}
                maxLength={50}
                required
              />
            </Form.Group>
          </Form>
          <div className="text-center">
            <Button
              style={{ marginTop: "8px" }}
              className="manage-query-button"
              onClick={handleUpdate}
              disabled={!isFormValid}
            >
              Update
            </Button>
          </div>
        </Modal.Body>
      </Modal>

      <ModifiedFooter />
    </>
  );
};

export default ManageQueries;
