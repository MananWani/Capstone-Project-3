import React, { useEffect, useState } from "react";
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
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import { Formik } from "formik";
import * as Yup from "yup";
import "../css/HrEmployeeList.css";
import { useNavigate,useLocation } from "react-router-dom";

const HrEmployeeList = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const location = useLocation();
  const [employees, setEmployees] = useState([]);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [designations, setDesignations] = useState([]);
  const [managers, setManagers] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role !== "HR Manager") {
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

  const fetchEmployees = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/employees/getallemployees"
      );
      setEmployees(response.data);
    } catch {
      setEmployees([]);
    }
  };

  const fetchData = async () => {
    try {
      const designationsResponse = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/designation/getalldesignations"
      );
      setDesignations(designationsResponse.data);

      const managersResponse = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/employees/getmanagers"
      );
      setManagers(managersResponse.data);
    } catch (error) {
      console.error("Error fetching data:", error);
      setDesignations([]);
      setManagers([]);
    }
  };

  const handleEditClick = (employee) => {
    setSelectedEmployee(employee);
    setShowEditModal(true);
  };

  const handleEditEmployee = async (values) => {
    if (selectedEmployee) {
      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/employees/updateemployee",
          { ...selectedEmployee, ...values }
        );
        fetchEmployees();
        setSuccessMessage("Employee updated successfully!");
      } catch {
        setErrorMsg("Update failed. Please try again.");
      }
      setShowEditModal(false);
      setSelectedEmployee(null);
    }
  };

  useEffect(() => {
    document.title = "Employee List";
  }, []);

  useEffect(() => {
    fetchEmployees();
    fetchData();
  }, []);

  const filteredEmployees = employees.filter((employee) =>
    employee.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const validationSchema = Yup.object().shape({
    mobileNumber: Yup.string()
      .matches(
        /^[6789]\d{9}$/,
        "Mobile number must start with [6,7,8,9] and have 10 digits"
      )
      .required("Mobile number is required"),
    designation: Yup.string().required("Designation is required"),
    manager: Yup.string().required("Manager is required"),
    isActive: Yup.boolean().required("Status is required"),
  });

  return (
    <>
      <HrManagerNavbar />
      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 employee-list-success">
          {successMessage}
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 employee-list-error">
          {errorMsg}
        </div>
      )}
      <Container className="hr-employee-list-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Employee List
        </h4>
        <InputGroup className="search-bar mb-3">
            <FormControl
              placeholder="Search Employee"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        <div className="hr-table-container">
          
          <Table bordered className="employee-table">
            <thead>
              <tr>
                <th>Fullname</th>
                <th>Email</th>
                <th>Mobile Number</th>
                <th>Designation</th>
                <th>Manager</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center">
                    No rows found
                  </td>
                </tr>
              ) : (
                filteredEmployees.map((employee) => (
                  <tr key={employee.employeeId}>
                    <td>{employee.fullName}</td>
                    <td>{employee.email}</td>
                    <td>{employee.mobileNumber}</td>
                    <td>{employee.designation}</td>
                    <td>{employee.manager}</td>
                    <td>{employee.isActive ? "Active" : "Inactive"}</td>
                    <td>
                      <Button
                        className="employee-edit-button"
                        onClick={() => handleEditClick(employee)}
                      >
                        Edit
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </div>

        {/* Edit Employee Modal */}
        <Modal
          show={showEditModal}
          onHide={() => setShowEditModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Edit Employee</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {selectedEmployee && (
              <Formik
                initialValues={{
                  mobileNumber: selectedEmployee.mobileNumber,
                  designation: "",
                  manager: "",
                  isActive: "",
                }}
                validationSchema={validationSchema}
                onSubmit={handleEditEmployee}
              >
                {({
                  handleSubmit,
                  handleChange,
                  handleBlur,
                  values,
                  errors,
                  touched,
                  isValid,
                }) => {
                  const isChanged =
                    values.mobileNumber !== selectedEmployee.mobileNumber ||
                    values.designation !== "" ||
                    values.manager !== "" ||
                    values.isActive !== "";

                  return (
                    <Form onSubmit={handleSubmit}>
                      <Form.Group controlId="mobileNumber">
                        <Form.Label style={{fontWeight:"bold" }}>
                          Mobile Number <span className="label-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          type="text"
                          value={values.mobileNumber}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={
                            touched.mobileNumber && !!errors.mobileNumber
                          }
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.mobileNumber}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Form.Group controlId="designation">
                        <Form.Label style={{ marginTop: "3px",fontWeight:"bold" }}>
                          Designation <span className="label-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          as="select"
                          value={values.designation}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={
                            touched.designation && !!errors.designation
                          }
                        >
                          <option value="" disabled>
                            Select Designation
                          </option>
                          {designations.map((designation) => (
                            <option
                              key={designation.designationId}
                              value={designation.designationId}
                            >
                              {designation.designationName}
                            </option>
                          ))}
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">
                          {errors.designation}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Form.Group controlId="manager">
                        <Form.Label style={{ marginTop: "3px",fontWeight:"bold" }}>
                          Manager <span className="label-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          as="select"
                          value={values.manager}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={touched.manager && !!errors.manager}
                        >
                          <option value="" disabled>
                            Select Manager
                          </option>
                          {managers.map((manager) => (
                            <option
                              key={manager.employeeId}
                              value={manager.employeeId}
                            >
                              {manager.fullName}
                            </option>
                          ))}
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">
                          {errors.manager}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Form.Group controlId="isActive">
                        <Form.Label style={{ marginTop: "3px",fontWeight:"bold" }}>
                          Status <span className="label-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          as="select"
                          value={values.isActive}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={touched.isActive && !!errors.isActive}
                        >
                          <option value="" disabled>
                            Select Status
                          </option>
                          <option value="true">Active</option>
                          <option value="false">Inactive</option>
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">
                          {errors.isActive}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <div className="text-center">
                        <Button
                          className="employee-update-button"
                          type="submit"
                          disabled={!isChanged || !isValid}
                        >
                          Update
                        </Button>
                      </div>
                    </Form>
                  );
                }}
              </Formik>
            )}
          </Modal.Body>
        </Modal>
      </Container>
      <ModifiedFooter />
    </>
  );
};

export default HrEmployeeList;
