import React, { useState, useEffect } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";
import { Formik } from "formik";
import * as Yup from "yup";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import Logo from "../images/paynet.png";
import "../css/ChangePasswordForm.css";

const ChangePasswordForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const location = useLocation();
  const navigate = useNavigate();
  const email = location.state?.email || "";
  const [currentPasswords, setcurrentPasswords] = useState(false);
  const [showPasswords, setShowPasswords] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");
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

  const validationSchema = Yup.object().shape({
    currentPassword: Yup.string().required("Current password is required"),
    newPassword: Yup.string()
      .min(8, "Password must be at least 8 characters")
      .max(15, "Password cannot exceed 15 characters")
      .matches(/[A-Z]/, "Password must have at least one uppercase letter")
      .matches(/[0-9]/, "Password must have at least one digit")
      .matches(
        /[^a-zA-Z0-9]/,
        "Password must have at least one special character"
      )
      .required("Password is required"),
  });

  const handleSubmit = async (values) => {
    if (values.currentPassword === values.newPassword) {
      setErrorMsg("Current password and new password cannot be the same.");
      return; 
    }
  
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/users/updatepassword",
        null,
        {
          params: {
            email,
            currentPassword: values.currentPassword,
            newPassword: values.newPassword,
          },
        }
      );
  
      navigate(`${contextPath}/profile`, {
        state: { successMessage: "Password changed successfully." },
      });
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMsg(error.response.data);
      } else {
        setErrorMsg("Failed to change password. Please try again.");
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

  useEffect(() => {
    document.title = "Change Password";
  }, []);

  return (
    <>
    
    <Container className="d-flex justify-content-center align-items-center change-password-container">
    <Row className="justify-content-center">
        <Col>
        {errorMsg && (
            <div className="alert alert-danger text-center mb-3 change-password-error">
              {errorMsg}
            </div>
          )}
      <div className="card rounded-3 shadow-sm change-password-card">
      
        <div className="card-body p-4">
          <div className="text-center mb-3 change-password-logo">
            <img src={Logo} alt="Logo" className="change-password-logo" />
          </div>
          <h4 className="text-center mb-4">Change Password</h4>

          <Formik
            initialValues={{ currentPassword: "", newPassword: "" }}
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
              isValid,
              dirty,
            }) => (
              <Form noValidate onSubmit={handleSubmit}>
                <Form.Group
                  controlId="email"
                  className="mb-3"
                  style={{ textAlign: "left", fontWeight: "bold" }}
                >
                  <Form.Label style={{  fontWeight:"bold" }}>
                    Email 
                  </Form.Label>
                  <Form.Control type="email" value={email} readOnly />
                </Form.Group>

                <Form.Group
                  controlId="currentPassword"
                  className="mb-3"
                  style={{ textAlign: "left", fontWeight: "bold" }}
                >
                  <Form.Label style={{  fontWeight:"bold" }}>
                    Current Password <span className="required-asterisk">*</span>
                  </Form.Label>
                  <Form.Control
                    type={currentPasswords ? "text" : "password"}
                    name="currentPassword"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.currentPassword}
                    isInvalid={
                      touched.currentPassword && !!errors.currentPassword
                    }
                    required
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.currentPassword}
                  </Form.Control.Feedback>
                  <Form.Check
                    type="checkbox"
                    label="Show Password"
                    onChange={() => setcurrentPasswords((prev) => !prev)}
                    style={{ marginTop: "5px", fontSize: "14px" }}
                  />
                </Form.Group>

                <Form.Group
                  controlId="newPassword"
                  className="mb-3"
                  style={{ textAlign: "left", fontWeight: "bold" }}
                >
                  <Form.Label style={{ fontWeight:"bold" }}>
                    New Password <span className="required-asterisk">*</span>
                  </Form.Label>
                  <Form.Control
                    type={showPasswords ? "text" : "password"}
                    name="newPassword"
                    onChange={handleChange}
                    onBlur={handleBlur}
                    value={values.newPassword}
                    isInvalid={touched.newPassword && !!errors.newPassword}
                    required
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.newPassword}
                  </Form.Control.Feedback>
                  <Form.Check
                    type="checkbox"
                    label="Show Password"
                    onChange={() => setShowPasswords((prev) => !prev)}
                    style={{ marginTop: "5px", fontSize: "14px" }}
                  />
                </Form.Group>

                {isValid && dirty && (
                  <Button className="change-password-button" type="submit">
                    Change Password
                  </Button>
                )}
              </Form>
            )}
          </Formik>
        </div>
      </div>
      </Col>
      </Row>
    </Container>
    </>
  );
};

export default ChangePasswordForm;
