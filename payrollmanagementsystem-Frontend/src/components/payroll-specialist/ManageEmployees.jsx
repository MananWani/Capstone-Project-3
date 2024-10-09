import React, { useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import axios from "axios";
import * as Yup from "yup";
import { Formik } from "formik";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import "../css/ManageEmployees.css";
import { useNavigate } from "react-router-dom";

const ManageEmployees = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [employees, setEmployees] = useState([]);
  const [showEditCTCModal, setShowEditCTCModal] = useState(false);
  const [employeeToEdit, setEmployeeToEdit] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
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

  useEffect(() => {
    fetchEmployees();
  }, []);

  useEffect(() => {
    document.title = "Manage Employees";
  }, []);
  const fetchEmployees = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/salary/getctcdetails"
      );
      setEmployees(response.data);
    } catch {
      setEmployees([]);
    }
  };

  const handleEditCTC = async (values, { resetForm }) => {
    if (employeeToEdit) {
      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/salary/updatectcdetails",
          {
            salaryId: employeeToEdit.salaryId,
            costToCompany: values.costToCompany,
          }
        );
        resetForm();
        setShowEditCTCModal(false);
        setSuccessMessage("CTC updated successfully!");
        fetchEmployees();
      } catch {
        setShowEditCTCModal(false);
        setErrorMsg("Failed to update CTC.");
      }
    }
  };

  const validationSchemaCTC = Yup.object().shape({
    costToCompany: Yup.number()
      .min(18000, "Minimum amount is 18000")
      .max(5000000, "Exceeded the maximum CTC")
      .required("CTC is required"),
  });

  const handleEditClick = (employee) => {
    setEmployeeToEdit(employee);
    setShowEditCTCModal(true);
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
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  return (
    <>
      <PayrollSpecialistNavbar />
      <Container className="pr-manage-employee-container">
        {/* Error Message */}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 manage-employee-error">
            {errorMsg}
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success text-center mb-3 manage-employee-success">
            {successMessage}
          </div>
        )}

        <h4 className="text-center mb-4" style={{marginTop:'20px'}}>Manage Employees</h4>
        <div className="ps-table-container">
        <Table bordered className="manage-employee-table">
          <thead className="manage-employee-table-head">
            <tr>
              <th style={{ width: "200px" }}>Full Name</th>
              <th style={{ width: "200px" }}>Cost to Company</th>
              <th style={{ width: "180px" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {employees.length === 0 ? (
              <tr>
                <td colSpan="3" className="text-center">
                  No employees found
                </td>
              </tr>
            ) : (
              employees.map((employee) => (
                <tr key={employee.salaryId}>
                  <td>{employee.fullName}</td>
                  <td>{employee.costToCompany}</td>
                  <td>
                    <Button
                      className="manage-employee-update-button"
                      onClick={() => handleEditClick(employee)}
                    >
                      Edit CTC
                    </Button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
        </div>
        {/* Edit CTC Modal */}
        <Modal
          show={showEditCTCModal}
          onHide={() => setShowEditCTCModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Edit CTC</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{
                costToCompany: employeeToEdit
                  ? employeeToEdit.costToCompany
                  : "",
              }}
              validationSchema={validationSchemaCTC}
              onSubmit={handleEditCTC}
              enableReinitialize
            >
              {({ handleSubmit, handleChange,handleBlur,setFieldTouched, isValid, values, errors, touched }) => (
                <Form noValidate onSubmit={handleSubmit}>
                  <Form.Group controlId="costToCompany">
                    <Form.Label>
                      Cost to Company <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="number"
                      value={values.costToCompany}
                       onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("costToCompany", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={
                        touched.costToCompany && !!errors.costToCompany
                      }
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.costToCompany}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      style={{ marginTop: "8px" }}
                      className="manage-employee-update-button"
                      type="submit"
                      disabled={!values.costToCompany || !!errors.costToCompany || !isValid}
                    >
                      Update CTC
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

export default ManageEmployees;
