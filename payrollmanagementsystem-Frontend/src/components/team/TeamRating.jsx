import React, { useCallback, useEffect, useState } from "react";
import {
  Container,
  Table,
  Button,
  Modal,
  Form,
  FormControl,
  InputGroup,
} from "react-bootstrap";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import ManagerNavbar from "../navbar/ManagerNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import "../css/TeamRating.css";
import { Formik } from "formik";
import * as Yup from "yup";

const TeamRating = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");
  const employeeId = localStorage.getItem("employeeId");
  const [employees, setEmployees] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

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
    document.title = "Team Rating";
  }, []);

  const handleEditClick = (employee) => {
    setSelectedEmployee(employee);
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);
    setSelectedEmployee(null);
  };

  const fetchEmployees = useCallback(async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/payrollmanagementsystem/employees/getteam`,
        {
          params: { employeeId: employeeId },
        }
      );
      setEmployees(response.data);
    } catch (error) {
      console.error("Error fetching employee data:", error);
      setEmployees([]);
    }
  }, [employeeId]);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  const handleSubmit = async (values, { resetForm }) => {
    if (selectedEmployee) {
      try {
        await axios.post(
          `http://localhost:8080/payrollmanagementsystem/employees/updaterating`,
          {
            employeeId: selectedEmployee.employeeId,
            rating: values.rating,
          }
        );

        resetForm();
        handleClose();
        setSuccessMessage("Rating updated successfully!");
        fetchEmployees();
      } catch (error) {
        console.error("Error updating rating:", error);
        handleClose();
        setErrorMsg("Failed to update rating.");
      }
    }
  };

  const isOctober = new Date().getMonth() === 9;

  const validationSchema = Yup.object().shape({
    rating: Yup.number()
      .required("Rating is required")
      .min(1, "Rating must be at least 1")
      .max(5, "Rating must be at most 5")
      .integer("Rating must be an integer"),
  });

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  const filteredEmployees = employees.filter((employee) =>
    employee.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <>
      <ManagerNavbar />
      <Container className="team-rating-container">
        {/* Error Message */}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 manage-role-error">
            {errorMsg}
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success text-center mb-3 manage-role-success">
            {successMessage}
          </div>
        )}

        <h4 className="text-center mb-4">Team Ratings</h4>
        <InputGroup className="search-bar mb-3">
          <FormControl
            placeholder="Search Employee"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </InputGroup>
        <div className="team-rating-scroll">
          <Table bordered className="team-rating-table">
            <thead className="team-rating-table-head">
              <tr>
                <th style={{ width: "200px" }}>Full Name</th>
                <th style={{ width: "200px" }}>Rating</th>
                <th style={{ width: "180px" }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.length === 0 ? (
                <tr>
                  <td colSpan="3" className="text-center">
                    No employees found
                  </td>
                </tr>
              ) : (
                filteredEmployees.map((employee) => (
                  <tr key={employee.employeeId}>
                    <td>{employee.fullName}</td>
                    <td>
                      {employee.rating !== null ? employee.rating : "None"}
                    </td>
                    <td>
                      {isOctober ? (
                        <Button
                          className="rating-update-button"
                          onClick={() => handleEditClick(employee)}
                        >
                          Edit
                        </Button>
                      ) : (
                        "No action"
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </div>

        <Modal show={showModal} onHide={handleClose} centered>
          <Modal.Header closeButton>
            <Modal.Title>Edit Rating</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ rating: "" }}
              validationSchema={validationSchema}
              onSubmit={handleSubmit}
            >
              {({
                handleSubmit,
                handleChange,
                handleBlur,
                values,
                errors,
                touched,
              }) => (
                <Form noValidate onSubmit={handleSubmit}>
                  <Form.Group controlId="formRating">
                    <Form.Label style={{ fontWeight: "bold" }}>
                      Rating <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="number"
                      name="rating"
                      value={values.rating}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      isInvalid={touched.rating && !!errors.rating}
                    />
                    <Form.Control.Feedback type="invalid">
                      {touched.rating && errors.rating}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      type="submit"
                      disabled={!values.rating || !!errors.rating}
                      className="rating-update-button"
                      style={{ marginTop: "10px" }}
                    >
                      Save
                    </Button>
                  </div>
                </Form>
              )}
            </Formik>
          </Modal.Body>
        </Modal>
      </Container>
      <ModifiedFooter />
    </>
  );
};

export default TeamRating;
