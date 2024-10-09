import React, { useEffect, useState } from "react";
import { Formik } from "formik";
import { Form, Button, Container, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import * as Yup from "yup";
import axios from "axios";
import Logo from "../images/paynet.png";
import "../css/LeaveForm.css";

const LeaveForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const [leaveTypes, setLeaveTypes] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
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

  const fetchLeaveTypes = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/leavetype/getalltypes"
      );
      setLeaveTypes(response.data);
    } catch {
      setLeaveTypes([]);
    }
  };

  useEffect(() => {
    fetchLeaveTypes();
  }, []);

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    document.title = "Apply Leave";
  }, []);

  const validationSchema = Yup.object().shape({
    startDate: Yup.date()
      .required("Start date is required")
      .min(
        new Date(new Date().setHours(0, 0, 0, 0)),
        "Start date cannot be in the past"
      ),

    startHalf: Yup.string().required("Start half is required"),
    endDate: Yup.date()
      .required("End date is required")
      .min(Yup.ref("startDate"), "End date cannot be before start date"),
    endHalf: Yup.string().required("End half is required"),
    reason: Yup.string()
      .required("Reason is required")
      .min(4, "Reason must be at least 4 characters")
      .max(50, "Reason cannot exceed 50 characters"),
    typeOfLeave: Yup.string().required("Leave type is required"),
  });

  const handleSubmit = async (values, { resetForm }) => {
    try {
      const requestData = {
        ...values,
        requestByEmployee: employeeId,
      };
      console.log(requestData)
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/leaverequest/requestleave",
        requestData
      );

      navigate(`${contextPath}/leave-requests`, {
        state: { successMessage: "Leave requested successfully!" },
      });
      resetForm();
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMsg(error.response.data);
      } else {
        setErrorMsg("An unexpected error occurred");
      }
    }
  };

  return (
    <Container className="leave-container">
      <Row className="justify-content-center">
        <Col>
          {errorMsg && (
            <div className="alert alert-danger text-center mb-3 leave-error">
              {errorMsg}
            </div>
          )}
          <div className="card leave-card">
            <div className="card-body">
              <div className="text-center mb-3 logo">
                <img src={Logo} alt="Logo" className="logo" />
              </div>
              <h4 className="text-center card-title">Apply Leave</h4>
              <Formik
                initialValues={{
                  startDate: "",
                  startHalf: "",
                  endDate: "",
                  endHalf: "",
                  reason: "",
                  typeOfLeave: "",
                }}
                validationSchema={validationSchema}
                onSubmit={handleSubmit}
                validateOnChange={true}
                validateOnBlur={true}
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
                      <Col md={6}>
                        <Form.Group controlId="startDate" className="group">
                          <Form.Label>
                            Start Date{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="date"
                            name="startDate"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.startDate}
                            isInvalid={
                              !!errors.startDate &&
                              (touched.startDate || values.startDate)
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.startDate}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={6}>
                        <Form.Group controlId="startHalf" className="group">
                          <Form.Label>
                            Start Half{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="startHalf"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.startHalf}
                            isInvalid={
                              !!errors.startHalf &&
                              (touched.startHalf || values.startHalf)
                            }
                            required
                          >
                            <option value="">Select half</option>
                            <option value="Morning">Morning</option>
                            <option value="Afternoon">Afternoon</option>
                          </Form.Control>
                          <Form.Control.Feedback type="invalid">
                            {errors.startHalf}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col md={6}>
                        <Form.Group controlId="endDate" className="group">
                          <Form.Label>
                            End Date{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            type="date"
                            name="endDate"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.endDate}
                            isInvalid={
                              !!errors.endDate &&
                              (touched.endDate || values.endDate)
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.endDate}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={6}>
                        <Form.Group controlId="endHalf" className="group">
                          <Form.Label>
                            End Half{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="endHalf"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.endHalf}
                            isInvalid={
                              !!errors.endHalf &&
                              (touched.endHalf || values.endHalf)
                            }
                            required
                          >
                            <option value="">Select half</option>
                            <option value="Morning">Morning</option>
                            <option value="Afternoon">Afternoon</option>
                          </Form.Control>
                          <Form.Control.Feedback type="invalid">
                            {errors.endHalf}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col md={6}>
                        <Form.Group controlId="reason" className="group">
                          <Form.Label>
                            Reason <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="input"
                            name="reason"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.reason}
                            isInvalid={
                              !!errors.reason &&
                              (touched.reason || values.reason)
                            }
                            required
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.reason}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                      <Col md={6}>
                        <Form.Group controlId="typeOfLeave" className="group">
                          <Form.Label>
                            Leave Type{" "}
                            <span className="required-asterisk">*</span>
                          </Form.Label>
                          <Form.Control
                            as="select"
                            name="typeOfLeave"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.typeOfLeave}
                            isInvalid={
                              !!errors.typeOfLeave &&
                              (touched.typeOfLeave || values.typeOfLeave)
                            }
                            required
                          >
                            <option value="">Select leave type</option>
                            {leaveTypes.map((type) => (
                              <option key={type.typeId} value={type.typeId}>
                                {type.typeName}
                              </option>
                            ))}
                          </Form.Control>
                          <Form.Control.Feedback type="invalid">
                            {errors.typeOfLeave}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row className="mb-3">
                      <Col>
                        {isValid && dirty && (
                          <Button type="submit" className="leave-button">
                            Apply
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

export default LeaveForm;
