import React, { useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import AdminNavbar from "../navbar/AdminNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import axios from "axios";
import * as Yup from "yup";
import { Formik } from "formik";
import "../css/LeaveType.css";

const LeaveType = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [leaveTypes, setLeaveTypes] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [leaveTypeToUpdate, setLeaveTypeToUpdate] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role !== "Admin") {
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

  const handleAddLeaveType = async (values, { resetForm }) => {
    const leaveTypeExists = leaveTypes.some(
      (leaveType) =>
        leaveType.typeName.toLowerCase() === values.typeName.toLowerCase()
    );
    if (leaveTypeExists) {
      setErrorMsg("Leave type already exists!");
      setShowAddModal(false);
      return;
    }

    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/leavetype/addleavetype",
        { typeName: values.typeName, numberOfLeaves: values.numberOfLeaves }
      );
      resetForm();
      setShowAddModal(false);
      setSuccessMessage("Leave type added successfully!");
      fetchLeaveTypes();
    } catch {
      setShowAddModal(false);
      setErrorMsg("Failed to add leave type.");
    }
  };

  const handleUpdateLeaveType = async (values, { resetForm }) => {
    if (leaveTypeToUpdate) {
      const leaveTypeExists = leaveTypes.some(
        (leaveType) =>
          leaveType.typeName.toLowerCase() === values.typeName.toLowerCase() &&
          leaveType.typeId !== leaveTypeToUpdate.typeId // Ensure it's not the same leave type
      );

      if (leaveTypeExists) {
        setErrorMsg("Leave type already exists!");
        setShowUpdateModal(false);
        return;
      }

      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/leavetype/updateleavetype",
          {
            typeId: leaveTypeToUpdate.typeId,
            typeName: values.typeName,
            numberOfLeaves: values.numberOfLeaves,
          }
        );
        resetForm();
        setShowUpdateModal(false);
        setLeaveTypeToUpdate(null);
        fetchLeaveTypes();
        setSuccessMessage("Leave type updated successfully!");
      } catch {
        setShowUpdateModal(false);
        setErrorMsg("Failed to update leave type.");
      }
    }
  };

  const handleEditClick = (leaveType) => {
    setLeaveTypeToUpdate(leaveType);
    setShowUpdateModal(true);
  };

  useEffect(() => {
    fetchLeaveTypes();
  }, []);

  useEffect(() => {
    document.title = "Leave Type List";
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
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  const validationSchema = Yup.object().shape({
    typeName: Yup.string()
      .matches(/^[a-zA-Z\s]*$/, "Leave type can only contain alphabets")
      .min(8, "Leave type must be at least 8 characters")
      .max(30, "Leave type must not exceed 30 characters")
      .required("Leave type is required"),
    numberOfLeaves: Yup.number()
      .max(90, "Maximum number can be 90")
      .typeError("Number of leaves must be a number")
      .positive("Number of leaves must be positive")
      .integer("Number of leaves must be an integer")
      .required("Number of leaves is required"),
  });

  return (
    <>
      <AdminNavbar />
      <Container className="leave-type-container">
        {/* Error Message */}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 leave-type-error">
            {errorMsg}
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success text-center mb-3 leave-type-success">
            {successMessage}
          </div>
        )}
        <h4 className="text-center mb-4">Leave Type List</h4>
        <Table bordered className="leave-type-table">
          <thead className="leave-type-table-head">
            <tr>
              <th style={{ width: "250px" }}>Leave Type</th>
              <th style={{ width: "200px" }}>Number of Leaves</th>
              <th style={{ width: "200px" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {leaveTypes.length === 0 ? (
              <tr>
                <td colSpan="3" className="text-center">
                  No rows found
                </td>
              </tr>
            ) : (
              leaveTypes.map((leaveType) => (
                <tr key={leaveType.typeId}>
                  <td>{leaveType.typeName}</td>
                  <td>{leaveType.numberOfLeaves}</td>
                  <td>
                    <Button
                      className="leave-type-update-button"
                      onClick={() => handleEditClick(leaveType)}
                    >
                      Edit
                    </Button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
        <Button
          className="add-leave-type-button"
          onClick={() => setShowAddModal(true)}
        >
          Add Leave Type
        </Button>

        {/* Add Leave Type Modal */}
        <Modal
          show={showAddModal}
          onHide={() => setShowAddModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Add Leave Type</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ typeName: "", numberOfLeaves: "" }}
              validationSchema={validationSchema}
              onSubmit={handleAddLeaveType}
            >
              {({
                handleSubmit,
                handleChange,
                handleBlur,
                values,
                errors,
                touched,
                setFieldTouched,
                isValid,
                dirty,
              }) => (
                <Form onSubmit={handleSubmit}>
                  <Form.Group controlId="typeName">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Leave Type <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.typeName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("typeName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.typeName && !!errors.typeName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.typeName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group controlId="numberOfLeaves">
                    <Form.Label style={{marginTop:'5px',fontWeight:"bold"}}>
                      Number of Leaves <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="number"
                      value={values.numberOfLeaves}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("numberOfLeaves", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={
                        touched.numberOfLeaves && !!errors.numberOfLeaves
                      }
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.numberOfLeaves}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="leave-type-update-button"
                      style={{ marginTop: "8px" }}
                      type="submit"
                      disabled={!isValid || !dirty}
                    >
                      Add Leave
                    </Button>
                  </div>
                </Form>
              )}
            </Formik>
          </Modal.Body>
        </Modal>

        {/* Update Leave Type Modal */}
        <Modal
          show={showUpdateModal}
          onHide={() => setShowUpdateModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Update Leave Type</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{
                typeName: leaveTypeToUpdate ? leaveTypeToUpdate.typeName : "",
                numberOfLeaves: leaveTypeToUpdate
                  ? leaveTypeToUpdate.numberOfLeaves
                  : "",
              }}
              validationSchema={validationSchema}
              onSubmit={handleUpdateLeaveType}
              enableReinitialize
            >
              {({
                handleSubmit,
                handleChange,
                handleBlur,
                values,
                errors,
                touched,
                setFieldTouched,
                isValid,
                dirty,
              }) => (
                <Form onSubmit={handleSubmit}>
                  <Form.Group controlId="typeName">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Leave Type <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.typeName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("typeName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.typeName && !!errors.typeName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.typeName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group controlId="numberOfLeaves">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Number of Leaves <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="number"
                      value={values.numberOfLeaves}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("numberOfLeaves", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={
                        touched.numberOfLeaves && !!errors.numberOfLeaves
                      }
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.numberOfLeaves}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="leave-type-update-button"
                      style={{ marginTop: "8px" }}
                      type="submit"
                      disabled={!isValid || !dirty}
                    >
                      Update
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

export default LeaveType;
