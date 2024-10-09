import React, { useState, useEffect } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Formik } from "formik";
import * as Yup from "yup";
import axios from "axios";
import "../css/RegisterForm.css";
import Logo from "../images/paynet.png";

const RegisterForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [designations, setDesignations] = useState([]);
  const [roles, setRoles] = useState([]);
  const [managers, setManagers] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleShowPasswordToggle = () => {
    setShowPassword((prevState) => !prevState);
  };

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
    document.title = "Register";
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch roles
        const rolesResponse = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/roles/getallroles"
        );
        setRoles(rolesResponse.data);

        // Fetch designations
        const designationsResponse = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/designation/getalldesignations"
        );
        setDesignations(designationsResponse.data);

        // Fetch managers
        const managersResponse = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/employees/getmanagers"
        );
        setManagers(managersResponse.data);
      } catch (error) {
        console.error("Error fetching data:", error);
        setRoles([]);
        setDesignations([]);
        setManagers([]);
      }
    };

    fetchData();
  }, []);

  const validationSchema = Yup.object().shape({
    fullname: Yup.string()
      .min(3, "Full name must be at least 3 characters")
      .max(30, "Full name cannot exceed 30 characters")
      .required("Full name is required"),
    userEmail: Yup.string()
      .matches(
        /^[a-zA-Z0-9._%+-]{3,20}@crimsonlogic\.com$/,
        "Please enter a valid email"
      )
      .required("Email is required"),
    mobileNumber: Yup.string()
      .matches(
        /^[6789]\d{9}$/,
        "Mobile number must start with [6,7,8,9] and have 10 digits"
      )
      .required("Mobile number is required"),
    dateOfBirth: Yup.date()
      .required("Date of birth is required")
      .min(
        new Date(new Date().setFullYear(new Date().getFullYear() - 60)),
        "Maximum age is 60 years"
      )
      .max(
        new Date(new Date().setFullYear(new Date().getFullYear() - 21)),
        "Minimum age is 21 years"
      ),
    dateOfJoining: Yup.date()
      .required("Date of joining is required")
      .max(new Date(), "Date of joining cannot be in the future")
      .min(
        new Date(new Date().setFullYear(new Date().getFullYear() - 40)),
        "Date of joining cannot be more than 40 years back"
      ),
    designation: Yup.string().required("Designation is required"),
    role: Yup.string().required("Role is required"),
    password: Yup.string()
      .min(8, "Password must be at least 8 characters")
      .max(15, "Password cannot exceed 15 characters")
      .matches(/[A-Z]/, "Password must have at least one uppercase letter")
      .matches(/[0-9]/, "Password must have at least one digit")
      .matches(
        /[^a-zA-Z0-9]/,
        "Password must have at least one special character"
      )
      .required("Password is required"),
    isActive: Yup.string().required("Status is required"),
    manager: Yup.string().required("Manager is required"),
  });

  const handleSubmit = async (values, resetForm) => {
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/employees/addemployee",
        {
          fullName: values.fullname,
          email: values.userEmail,
          mobileNumber: values.mobileNumber,
          dateOfBirth: values.dateOfBirth,
          joiningDate: values.dateOfJoining,
          designation: values.designation,
          role: values.role,
          passwordHash: values.password,
          isActive: values.isActive,
          manager: values.manager,
        }
      );

      navigate(`${contextPath}/hr-view-employee`, {
        state: { successMessage: "Employee added successfully!" },
      });

      resetForm();
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMsg(error.response.data);
      } else {
        setErrorMsg("Registration failed. Please try again.");
      }
    }
  };

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  return (
    <Container className="form-container">
      <Row className="justify-content-center">
        <Col>
          {errorMsg && (
            <div className="alert alert-danger text-center mb-3 register-error">
              {errorMsg}
            </div>
          )}
          <div className="card register-card">
            <div className="card-body">
              <div className="text-center mb-3 logo">
                <img src={Logo} alt="Logo" className="logo" />
              </div>
              <h4 className="text-center card-title">Register</h4>
              <Formik
                initialValues={{
                  fullname: "",
                  userEmail: "",
                  mobileNumber: "",
                  dateOfBirth: "",
                  dateOfJoining: "",
                  designation: "",
                  role: "",
                  password: "",
                  isActive: "",
                  manager: "",
                }}
                validationSchema={validationSchema}
                onSubmit={(values, { resetForm }) =>
                  handleSubmit(values, resetForm)
                }
              >
                {({
                  handleSubmit,
                  handleChange,
                  handleBlur,
                  values,
                  errors,
                  touched,
                  isValid,
                  dirty,
                }) => (
                  <Form noValidate onSubmit={handleSubmit}>
                    <Row className="mb-3">
                      <Col md={4}>
                        <Form.Group controlId="fullname" className="group">
                          <Form.Label>
                            Full Name{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="text"
                            name="fullname"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.fullname}
                            isInvalid={touched.fullname && !!errors.fullname}
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.fullname}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={4}>
                        <Form.Group controlId="userEmail" className="group">
                          <Form.Label>
                            Email <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="email"
                            name="userEmail"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.userEmail}
                            isInvalid={touched.userEmail && !!errors.userEmail}
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.userEmail}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={4}>
                        <Form.Group controlId="mobileNumber" className="group">
                          <Form.Label>
                            Mobile Number{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="text"
                            name="mobileNumber"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.mobileNumber}
                            isInvalid={
                              touched.mobileNumber && !!errors.mobileNumber
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.mobileNumber}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col md={4}>
                        <Form.Group controlId="dateOfBirth" className="group">
                          <Form.Label>
                            Date of Birth{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="date"
                            name="dateOfBirth"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.dateOfBirth}
                            isInvalid={
                              touched.dateOfBirth && !!errors.dateOfBirth
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.dateOfBirth}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={4}>
                        <Form.Group controlId="dateOfJoining" className="group">
                          <Form.Label>
                            Date of Joining{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="date"
                            name="dateOfJoining"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.dateOfJoining}
                            isInvalid={
                              touched.dateOfJoining && !!errors.dateOfJoining
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.dateOfJoining}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={4}>
                        <Form.Group controlId="isActive" className="group">
                          <Form.Label>
                            Status <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="isActive"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.isActive}
                            isInvalid={touched.isActive && !!errors.isActive}
                            required
                          >
                            <option value="">Select status</option>
                            <option value="true">Active</option>
                            <option value="false">Inactive</option>
                          </Form.Control>
                          <Form.Control.Feedback type="invalid">
                            {errors.isActive}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col md={4}>
                        <Form.Group controlId="designation" className="group">
                          <Form.Label>
                            Designation{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="designation"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.designation}
                            isInvalid={
                              touched.designation && !!errors.designation
                            }
                            required
                          >
                            <option value="">Select designation</option>
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
                      </Col>
                      <Col md={4}>
                        <Form.Group controlId="role" className="group">
                          <Form.Label>
                            Role <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="role"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.role}
                            isInvalid={touched.role && !!errors.role}
                            required
                          >
                            <option value="">Select role</option>
                            {roles
                              .filter((role) => role.roleName !== "Admin")
                              .map((role) => (
                                <option key={role.roleId} value={role.roleId}>
                                  {role.roleName}
                                </option>
                              ))}
                          </Form.Control>
                          <Form.Control.Feedback type="invalid">
                            {errors.role}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col md={4}>
                        <Form.Group controlId="manager" className="group">
                          <Form.Label>
                            Reporting Manager{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="manager"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.manager}
                            isInvalid={touched.manager && !!errors.manager}
                            required
                          >
                            <option value="">Select manager</option>
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
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col md={6}>
                        <Form.Group controlId="password" className="group">
                          <Form.Label>
                            Password{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type={showPassword ? "text" : "password"}
                            name="password"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.password}
                            isInvalid={touched.password && !!errors.password}
                            required
                          />
                          <Form.Check
                            type="checkbox"
                            label="Show Password"
                            checked={showPassword}
                            onChange={handleShowPasswordToggle}
                            style={{ marginTop: "2px" }} // Optional styling for spacing
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.password}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col style={{ marginTop: "20px" }}>
                        {isValid && dirty && (
                          <Button
                            variant="primary"
                            type="submit"
                            className="register-button"
                          >
                            Register
                          </Button>
                        )}
                      </Col>
                    </Row>
                  </Form>
                )}
              </Formik>
            </div>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default RegisterForm;
