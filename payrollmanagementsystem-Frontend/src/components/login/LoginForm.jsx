import React, { useState, useEffect } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";
import { Formik } from "formik";
import * as Yup from "yup";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import Logo from "../images/paynet.png";
import "../css/LoginForm.css";

const LoginForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const location = useLocation();
  const [showPassword, setShowPassword] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (location.state?.error) {
      setErrorMsg(location.state.error);
    }
  }, [location.state]);

  const validationSchema = Yup.object().shape({
    userEmail: Yup.string().required("Email is required"),
    userPassword: Yup.string().required("Password is required"),
  });

  const handleSubmit = async (values) => {
    if (values.userPassword.length > 15) {
      setErrorMsg("Invalid email or password.");
      return;
    }

    const emailParts = values.userEmail.split("@");
    if (emailParts[0].length > 20) {
      setErrorMsg("Invalid email or password.");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/payrollmanagementsystem/users/login",
        {
          email: values.userEmail,
          passwordHash: values.userPassword,
        }
      );

      const { role, employeeId, fullName, logId } = response.data;
      console.log(role);
      console.log(fullName);
      localStorage.setItem("employeeId", employeeId);
      localStorage.setItem("fullName", fullName);
      localStorage.setItem("role", role);
      localStorage.setItem("logId", logId);

      switch (role) {
        case "HR Manager":
          navigate(`${contextPath}/hr-home`);
          break;
        case "Admin":
          navigate(`${contextPath}/admin-home`);
          break;
        case "Payroll Specialist":
          navigate(`${contextPath}/payroll-home`);
          break;
        case "Manager":
          navigate(`${contextPath}/manager-home`);
          break;
        case "Employee":
          navigate(`${contextPath}/employee-home`);
          break;
        default:
          navigate(`${contextPath}/`, {
            state: {
              errorMessage: "Unexpected error, please try again later.",
            },
          });
      }
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMsg(error.response.data);
      } else {
        setErrorMsg("Login failed. Please try again.");
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
    document.title = "Login";
  }, []);

  return (
    <Container className="d-flex justify-content-center align-items-center login-container">
      <Row className="justify-content-center" style={{ width: "100%" }}>
        <Col xs={12} sm={10} md={8} lg={6} xl={5} xxl={4}>
          {errorMsg && (
            <div className="alert alert-danger text-center mb-3 fixed-error">
              {errorMsg}
            </div>
          )}
          <div className="card rounded-3 shadow-sm login-card">
            <div className="card-body p-4">
              <div className="text-center mb-3 login-logo">
                <img src={Logo} alt="Logo" className="login-logo" />
              </div>

              <h4 className="text-center mb-4 card-title">Sign In</h4>

              <Formik
                initialValues={{ userEmail: "", userPassword: "" }}
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
                }) => {
                  const isValid =
                    Object.keys(errors).length === 0 &&
                    values.userEmail &&
                    values.userPassword;

                  return (
                    <Form noValidate onSubmit={handleSubmit}>
                      <Form.Group
                        controlId="userEmail"
                        className="mb-3"
                        style={{ textAlign: "left", fontWeight: "bold" }}
                      >
                        <Form.Label className="text-start">
                          Email <span className="required-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          type="email"
                          name="userEmail"
                          style={{ color: "black" }}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          value={values.userEmail}
                          isInvalid={touched.userEmail && !!errors.userEmail}
                          required
                        />
                        <Form.Control.Feedback
                          type="invalid"
                          className="form-group"
                        >
                          {errors.userEmail}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Form.Group
                        controlId="userPassword"
                        className="mb-3 position-relative"
                        style={{ textAlign: "left", fontWeight: "bold" }}
                      >
                        <Form.Label className="text-start">
                          Password <span className="required-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          type={showPassword ? "text" : "password"}
                          name="userPassword"
                          style={{ color: "black" }}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          value={values.userPassword}
                          isInvalid={
                            touched.userPassword && !!errors.userPassword
                          }
                          required
                        />
                        {values.userPassword && (
                          <span
                            className="position-absolute"
                            style={{
                              right: "15px",
                              top: "55%",
                              cursor: "pointer",
                            }}
                            onClick={() => setShowPassword(!showPassword)}
                          >
                            <FontAwesomeIcon
                              icon={showPassword ? faEyeSlash : faEye}
                            />
                          </span>
                        )}
                        <Form.Control.Feedback
                          type="invalid"
                          className="form-group"
                        >
                          {errors.userPassword}
                        </Form.Control.Feedback>
                      </Form.Group>

                      {isValid && (
                        <Button
                          variant="primary"
                          type="submit"
                          className="login-button"
                        >
                          Login
                        </Button>
                      )}
                    </Form>
                  );
                }}
              </Formik>
            </div>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default LoginForm;
