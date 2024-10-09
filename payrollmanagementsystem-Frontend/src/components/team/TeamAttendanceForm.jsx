import React, { useEffect, useState } from "react";
import { Form, Button, Container, Row, Col, Modal } from "react-bootstrap";
import axios from "axios";
import { Formik } from "formik";
import * as Yup from "yup";
import Logo from "../images/paynet.png";
import "../css/TeamAttendanceForm.css";
import { useNavigate } from "react-router-dom";

const TeamAttendanceForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const [teamMembers, setTeamMembers] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
  const [attendanceData, setAttendanceData] = useState([]);
  const [modalShow, setModalShow] = useState(false);
  const role = localStorage.getItem("role");
  const fullName = localStorage.getItem("fullName");
  const employeeId = localStorage.getItem("employeeId");

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
    const fetchTeamMembers = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/payrollmanagementsystem/employees/getteam`,
          {
            params: { employeeId: employeeId },
          }
        );
        setTeamMembers(response.data);
      } catch (error) {
        console.log(error);
        setTeamMembers([]);
      }
    };
    fetchTeamMembers();
  }, [employeeId]);

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    document.title = "Team Attendance";
  }, []);

  const handleSubmit = async (values) => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/attendance/getteamattendance",
        {
          params: {
            month: values.month,
            employeeId: values.teamMember,
          },
        }
      );
      setAttendanceData(response.data);
      setModalShow(true);
    } catch (error) {
      setErrorMsg("Unexpected error, please try again.");
    }
  };

  const validationSchema = Yup.object({
    month: Yup.string().required("Month is required"),
    teamMember: Yup.string().required("Team member is required"),
  });
  return (
    <>
      <Container className="d-flex justify-content-center align-items-center attendance-form-container">
        <Row className="justify-content-center">
          <Col>
            {errorMsg && (
              <div className="alert alert-danger text-center mb-3 attendance-form-error">
                {errorMsg}
              </div>
            )}
            <div className="card rounded-3 shadow-sm attendance-form-card">
              <div className="card-body p-4">
                <div className="text-center mb-3 attendance-form-logo">
                  <img src={Logo} alt="Logo" className="attendance-form-logo" />
                </div>
                <h4 className="text-center mb-4">Team Attendance</h4>
                <Formik
                  initialValues={{ month: "", teamMember: "" }}
                  validationSchema={validationSchema}
                  onSubmit={handleSubmit}
                >
                  {({
                    handleSubmit,
                    handleChange,
                    handleBlur,
                    values,
                    errors,
                    isValid,
                    isSubmitting,
                  }) => (
                    <Form noValidate onSubmit={handleSubmit}>
                      <Form.Group
                        controlId="month"
                        className="mb-3"
                        style={{ textAlign: "left", fontWeight: "bold" }}
                      >
                        <Form.Label>
                          Month <span className="required-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          as="select"
                          name="month"
                          value={values.month}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={!!errors.month}
                        >
                          <option value="">Select a month</option>
                          {Array.from({ length: 12 }, (_, i) => (
                            <option key={i} value={i + 1}>
                              {new Date(0, i).toLocaleString("default", {
                                month: "long",
                              })}
                            </option>
                          ))}
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">
                          {errors.month}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Form.Group
                        controlId="teamMember"
                        className="mb-3"
                        style={{ textAlign: "left", fontWeight: "bold" }}
                      >
                        <Form.Label>
                          Team Member{" "}
                          <span className="required-asterisk">*</span>
                        </Form.Label>
                        <Form.Control
                          as="select"
                          name="teamMember"
                          value={values.teamMember}
                          onChange={handleChange}
                          onBlur={handleBlur}
                          isInvalid={!!errors.teamMember}
                        >
                          <option value="">Select a team member</option>
                          {teamMembers.map((member) => (
                            <option
                              key={member.employeeId}
                              value={member.employeeId}
                            >
                              {member.fullName}
                            </option>
                          ))}
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">
                          {errors.teamMember}
                        </Form.Control.Feedback>
                      </Form.Group>

                      <Button
                        className="attendance-form-button"
                        type="submit"
                        disabled={!isValid || isSubmitting}
                      >
                        Get Attendance
                      </Button>
                    </Form>
                  )}
                </Formik>

                <Modal
                  show={modalShow}
                  onHide={() => setModalShow(false)}
                  centered
                >
                  <Modal.Header closeButton>
                    <Modal.Title>Attendance Details</Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    {attendanceData ? (
                      <div className="row">
                        <div className="col-md-8">
                          <p>
                            <strong>Employee Name:</strong>{" "}
                            {attendanceData.fullName}
                          </p>
                          
                        </div>
                        <div className="col-md-6">
                        <p>
                            <strong>Present Days:</strong>{" "}
                            {attendanceData.presentCount}
                          </p>
                          <p>
                            <strong>Leave Days:</strong>{" "}
                            {attendanceData.leaveCount}
                          </p>
                          
                        </div>
                        <div className="col-md-6">
                        <p>
                            <strong>Half Days:</strong>{" "}
                            {attendanceData.halfDayCount}
                          </p>
                          <p>
                            <strong>Absent Days:</strong>{" "}
                            {attendanceData.absentCount}
                          </p>
                        </div>
                      </div>
                    ) : (
                      <p>Loading attendance details...</p>
                    )}
                  </Modal.Body>
                  <Modal.Footer>
                    <Button
                      className="attendance-modal-button"
                      onClick={() => setModalShow(false)}
                    >
                      Close
                    </Button>
                  </Modal.Footer>
                </Modal>
              </div>
            </div>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default TeamAttendanceForm;
