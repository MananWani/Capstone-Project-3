import React, { useEffect, useState } from "react";
import { Container, Table, Button, Modal, Form,FormControl,
  InputGroup } from "react-bootstrap";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import * as Yup from "yup";
import { Formik } from "formik";
import AdminNavbar from "../navbar/AdminNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import "../css/ManageEmployeeRoles.css";

const ManageEmployeeRoles = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const [employees, setEmployees] = useState([]);
  const [roles, setRoles] = useState([]);
  const [showEditRoleModal, setShowEditRoleModal] = useState(false);
  const [employeeToEdit, setEmployeeToEdit] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

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

  useEffect(() => {
    document.title = "Manage Roles";
  }, []);

  useEffect(() => {
    fetchEmployees();
    fetchRoles();
  }, []);

  const fetchEmployees = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/users/getuserrole"
      );
      console.log(response.data);
      setEmployees(response.data);
    } catch {
      setEmployees([]);
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/roles/getallroles"
      );
      console.log(response.data);
      setRoles(response.data);
    } catch {
      setRoles([]);
    }
  };

  const handleEditRole = async (values, { resetForm }) => {
    if (employeeToEdit) {
      const currentEmployee = employees.find(
        (emp) => emp.userId === employeeToEdit.userId
      );
      const currentRole = roles.find((role) => role.roleId === values.role);

      if (
        currentEmployee &&
        currentRole &&
        currentEmployee.role === currentRole.roleName
      ) {
        setErrorMsg("Select a new role.");
        setShowEditRoleModal(false);
        return;
      }

      try {
        await axios.post(
          "http://localhost:8080/payrollmanagementsystem/users/updaterole",
          {
            userId: employeeToEdit.userId,
            role: values.role,
          }
        );
        resetForm();
        setShowEditRoleModal(false);
        setSuccessMessage("Role updated successfully!");
        fetchEmployees();
      } catch (error) {
        console.error("Error updating role:", error);
        setShowEditRoleModal(false);
        setErrorMsg("Failed to update role.");
      }
    }
  };

  const validationSchemaRole = Yup.object().shape({
    role: Yup.string().required("Role is required"),
  });

  const handleEditClick = (employee) => {
    setEmployeeToEdit(employee);
    setShowEditRoleModal(true);
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

  const filteredEmployees = employees.filter((employee) =>
    employee.fullName && employee.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );
  

  return (
    <>
      <AdminNavbar />
      <Container className="manage-role-container">
        {/* Error Message */}
        {errorMsg && (
          <div className="alert alert-danger text-center mb-3 manage-role-error">
            {errorMsg}
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success text-center mb-3 manage-role-success">
            {successMessage}
          </div>
        )}

        <h4 className="text-center mb-4">Manage Roles</h4>
        <InputGroup className="search-bar mb-3">
            <FormControl
              placeholder="Search Employee"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        <div className="manage-role-scroll">
        <Table bordered className="manage-role-table">
          <thead className="manage-role-table-head">
            <tr>
              <th style={{ width: "200px" }}>Full Name</th>
              <th style={{ width: "200px" }}>Role</th>
              <th style={{ width: "180px" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.length === 0 ? (
              <tr>
                <td colSpan="3" className="text-center">
                  No employees found
                </td>
              </tr>
            ) : (
              filteredEmployees
      .filter(employee => employee.fullName) // Filter out employees with null fullName
      .map((employee) => (
        <tr key={employee.userId}>
          <td>{employee.fullName}</td>
          <td>{employee.role}</td>
          <td>
            <Button
              className="manage-role-update-button"
              onClick={() => handleEditClick(employee)}
            >
              Edit Role
            </Button>
          </td>
        </tr>
      ))
  )}
          </tbody>
        </Table>
        </div>

        {/* Edit Role Modal */}
        <Modal
          show={showEditRoleModal}
          onHide={() => setShowEditRoleModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Edit Role</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Formik
              initialValues={{
                role: "",
              }}
              validationSchema={validationSchemaRole}
              onSubmit={handleEditRole}
              enableReinitialize
            >
              {({
                handleSubmit,
                handleChange,
                handleBlur,
                setFieldTouched,
                isValid,
                values,
                errors,
                touched,
              }) => {
                const isChanged = values.role !== "";

                return (
                  <Form noValidate onSubmit={handleSubmit}>
                    <Form.Group controlId="role">
                      <Form.Label style={{ fontWeight:"bold" }}>
                        Role <span className="label-asterisk">*</span>
                      </Form.Label>
                      <Form.Control
                        as="select"
                        value={values.role}
                        onChange={(e) => {
                          handleChange(e);
                          setFieldTouched("role", true);
                        }}
                        onBlur={handleBlur}
                        isInvalid={touched.role && !!errors.role}
                        required
                      >
                        <option value="" disabled>
                          Select Role
                        </option>
                        {roles.map((role) => (
                          <option key={role.roleId} value={role.roleId}>
                            {role.roleName}
                          </option>
                        ))}
                      </Form.Control>
                      <Form.Control.Feedback type="invalid">
                        {errors.role}
                      </Form.Control.Feedback>
                    </Form.Group>
                    <div className="text-center">
                      <Button
                        style={{ marginTop: "8px" }}
                        className="manage-role-update-button"
                        type="submit"
                        disabled={!isChanged || !!errors.role || !isValid}
                      >
                        Update
                      </Button>
                    </div>
                  </Form>
                );
              }}
            </Formik>
          </Modal.Body>
        </Modal>
        
      </Container>
      <ModifiedFooter />
    </>
  );
};

export default ManageEmployeeRoles;
