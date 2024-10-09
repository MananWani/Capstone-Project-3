import React, { useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import AdminNavbar from "../navbar/AdminNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import axios from "axios";
import * as Yup from "yup";
import { Formik } from "formik";
import { useNavigate } from "react-router-dom";
import '../css/AddDesignation.css';

const AddDesignation = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [designations, setDesignations] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [designationToUpdate, setDesignationToUpdate] = useState(null);
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

  const fetchDesignations = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/designation/getalldesignations"
      );
      setDesignations(response.data);
    } catch {
      setDesignations([]);
    }
  };

  const handleAddDesignation = async (values, { resetForm }) => {
    const designationExists = designations.some(designation =>
      designation.designationName.toLowerCase() === values.designationName.toLowerCase()
    );

    if (designationExists) {
      setErrorMsg("Designation already exists!");
      setShowAddModal(false);
      return;
    }

    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/designation/adddesignation",
        { designationName: values.designationName }
      );
      resetForm();
      setShowAddModal(false);
      fetchDesignations();
      setSuccessMessage("Designation added successfully!");

    } catch {
      setShowAddModal(false);
      setErrorMsg("Failed to add designation.");
    }
  };

  const handleUpdateDesignation = async (values, { resetForm }) => {
    if (designationToUpdate) {

      const designationExists = designations.some(designation =>
        designation.designationName.toLowerCase() === values.designationName.toLowerCase()
      );
  
      if (designationExists) {
        setErrorMsg("Designation already exists!");
        setShowUpdateModal(false);
        return;
      }

      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/designation/updatedesignation",
          { designationId: designationToUpdate.designationId, designationName: values.designationName }
        );
        resetForm();
        setShowUpdateModal(false);
        setDesignationToUpdate(null);
        fetchDesignations();
        setSuccessMessage("Designation updated successfully!");
      } catch {
        setShowUpdateModal(false);
        setErrorMsg("Failed to update designation.");  
      }
    }
  };

  const handleEditClick = (designation) => {
    setDesignationToUpdate(designation);
    setShowUpdateModal(true);
  };

  useEffect(() => {
    fetchDesignations();
  }, []);

  useEffect(() => {
    document.title = "Designation List";
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
    designationName: Yup.string()
      .matches(/^[a-zA-Z0-9\s]*$/, "Designation name can only contain alphabets and numbers")
      .min(7, "Designation name must be at least 7 characters")
      .max(30, "Designation name must not exceed 30 characters")
      .required("Designation name is required"),
  });

  return (
    <>
      <AdminNavbar />
      {successMessage && (
          <div className="alert alert-success text-center mb-3 desg-success">
            {successMessage}
          </div>
        )}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 desg-error">
            {errorMsg}
          </div>
        )}
      <Container className="add-desg-container">     
        <h4 className="text-center mb-4">Designation List</h4>
        <div className="table-scroll">
          <Table bordered className="desg-table">
            <thead>
              <tr>
                <th style={{width:'250px'}}>Designation Name</th>
                <th style={{width:'80px'}}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {designations.length === 0 ? (
                <tr>
                  <td colSpan="2" className="text-center">
                    No rows found
                  </td>
                </tr>
              ) : (
                designations.map((designation) => (
                  <tr key={designation.designationId}>
                    <td>{designation.designationName}</td>
                    <td>
                      <Button
                        className="desg-update-button"
                        onClick={() => handleEditClick(designation)}
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
        <Button
          className="add-desg-button"
          onClick={() => setShowAddModal(true)}
        >
          Add New Designation
        </Button>

        {/* Add Designation Modal */}
        <Modal
          show={showAddModal}
          onHide={() => setShowAddModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Add New Designation</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ designationName: "" }}
              validationSchema={validationSchema}
              onSubmit={handleAddDesignation}
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
                  <Form.Group controlId="designationName">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Designation Name <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.designationName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("designationName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.designationName && !!errors.designationName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.designationName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="desg-update-button"
                      style={{ marginTop: "8px" }}
                      type="submit"
                      disabled={!isValid || !dirty}
                    >
                      Add
                    </Button>
                  </div>
                </Form>
              )}
            </Formik>
          </Modal.Body>
        </Modal>

        {/* Update Designation Modal */}
        <Modal
          show={showUpdateModal}
          onHide={() => setShowUpdateModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Update Designation</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ designationName: designationToUpdate ? designationToUpdate.designationName : "" }}
              validationSchema={validationSchema}
              onSubmit={handleUpdateDesignation}
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
                  <Form.Group controlId="designationName">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Designation Name <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.designationName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("designationName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.designationName && !!errors.designationName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.designationName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="desg-update-button"
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

export default AddDesignation;
