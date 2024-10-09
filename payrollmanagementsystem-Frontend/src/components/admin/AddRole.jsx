import React, { useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import AdminNavbar from "../navbar/AdminNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import axios from "axios";
import * as Yup from "yup";
import { Formik } from "formik";
import "../css/AddRole.css";

const AddForm = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [roles, setRoles] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [roleToUpdate, setRoleToUpdate] = useState(null);
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

  const fetchRoles = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/roles/getallroles"
      );
      setRoles(response.data);
    } catch {
      setRoles([]);
    }
  };

  const handleAddRole = async (values, { resetForm }) => {
    const roleExists = roles.some(role => role.roleName.toLowerCase() === values.roleName.toLowerCase());
    if (roleExists) {
      setErrorMsg("Role already exists!");
      setShowAddModal(false);
      return;
    }

    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/roles/addrole",
        { roleName: values.roleName }
      );
      resetForm();
      setShowAddModal(false);
      setSuccessMessage("Role added successfully!");
      fetchRoles();
    } catch {
      setShowAddModal(false);
      setErrorMsg("Failed to add role.");
    }
  };

  const handleUpdateRole = async (values, { resetForm }) => {
    if (roleToUpdate) {

      const roleExists = roles.some(role => role.roleName.toLowerCase() === values.roleName.toLowerCase());
      if (roleExists) {
        setErrorMsg("Role already exists!");
        setShowUpdateModal(false);
        return;
      }

      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/roles/updaterole",
          { roleId: roleToUpdate.roleId, roleName: values.roleName }
        );
        resetForm();
        setShowUpdateModal(false);
        setRoleToUpdate(null);
        fetchRoles();
        setSuccessMessage("Role updated successfully!");
      } catch {
        setShowUpdateModal(false);
        setErrorMsg("Failed to update role.");
      }
    }
  };

  const handleEditClick = (role) => {
    setRoleToUpdate(role);
    setShowUpdateModal(true);
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  useEffect(() => {
    document.title = "Role List";
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
    roleName: Yup.string()
      .matches(/^[a-zA-Z\s]*$/, "Role name can only contain alphabets")
      .min(2, "Role name must be at least 2 characters")
      .max(20, "Role name must not exceed 20 characters")
      .required("Role name is required"),
  });

  return (
    <>
      <AdminNavbar />
      <Container className="addrole-container">
        {/* Error Message */}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 role-error">
            {errorMsg}
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success text-center mb-3 role-success">
            {successMessage}
          </div>
        )}
        <h4 className="text-center mb-4">Role List</h4>
        <Table bordered className="role-table">
          <thead className="role-table-head">
            <tr>
              <th style={{width:'250px'}}>Role Name</th>
              <th style={{width:'200px'}}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {roles.length === 0 ? (
              <tr>
                <td colSpan="2" className="text-center">
                  No rows found
                </td>
              </tr>
            ) : (
              roles.map((role) => (
                <tr key={role.roleId}>
                  <td>{role.roleName}</td>
                  <td>
                    <Button
                      className="role-update-button"
                      onClick={() => handleEditClick(role)}
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
          className="add-role-button"
          onClick={() => setShowAddModal(true)}
        >
          Add New Role
        </Button>

        

        {/* Add Role Modal */}
        <Modal
          show={showAddModal}
          onHide={() => setShowAddModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Add New Role</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ roleName: "" }}
              validationSchema={validationSchema}
              onSubmit={handleAddRole}
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
                  <Form.Group controlId="roleName">
                    <Form.Label style={{fontWeight:"bold"}}>
                      Role Name <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.roleName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("roleName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.roleName && !!errors.roleName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.roleName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="role-update-button"
                      style={{ marginTop: "8px" }}
                      type="submit"
                      disabled={!isValid || !dirty}
                    >
                      Add Role
                    </Button>
                  </div>
                </Form>
              )}
            </Formik>
          </Modal.Body>
        </Modal>

        {/* Update Role Modal */}
        <Modal
          show={showUpdateModal}
          onHide={() => setShowUpdateModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Update Role</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{ roleName: roleToUpdate ? roleToUpdate.roleName : "" }}
              validationSchema={validationSchema}
              onSubmit={handleUpdateRole}
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
                  <Form.Group controlId="roleName">
                    <Form.Label style={{fontWeight:"bold" }}>
                      Role Name <span className="label-asterisk">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      value={values.roleName}
                      onChange={(e) => {
                        handleChange(e);
                        setFieldTouched("roleName", true);
                      }}
                      onBlur={handleBlur}
                      isInvalid={touched.roleName && !!errors.roleName}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.roleName}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="text-center">
                    <Button
                      className="role-update-button"
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

export default AddForm;
