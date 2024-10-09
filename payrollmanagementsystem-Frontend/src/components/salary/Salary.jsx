import React, { useEffect, useState } from "react";
import {
  Container,
  Table,
  Button,
  FormControl,
  InputGroup,
  Modal,
  FormLabel,
} from "react-bootstrap";
import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";
import axios from "axios";
import "../css/Salary.css";
import ModifiedFooter from "../footer/ModifiedFooter";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import { useNavigate } from "react-router-dom";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";

const Salary = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const [month, setMonth] = useState("");
  const [year, setYear] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [salaryRecords, setSalaryRecords] = useState([]);
  const [query, setQuery] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedRecordId, setSelectedRecordId] = useState(null);
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

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  const filteredRecords = salaryRecords.filter((record) => {
    const recordStartDate = new Date(record.payPeriodStart);
    const recordMonth = recordStartDate.getMonth() + 1;
    const recordYear = recordStartDate.getFullYear();

    return (
      (month ? recordMonth === Number(month) : true) &&
      (year ? recordYear === Number(year) : true)
    );
  });

  useEffect(() => {
    document.title = "Salary Records";
  }, []);

  useEffect(() => {
    const fetchSalaryRecords = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/payrollmanagementsystem/salaryrecord/getsalary",
          {
            params: { employeeId: employeeId },
          }
        );
        setSalaryRecords(response.data);
      } catch (error) {
        console.error("Error fetching salary records:", error);
      }
    };
    fetchSalaryRecords();
  }, [employeeId]);

  const generatePayslipPDF = (record) => {
    const doc = new jsPDF();

    const payPeriodStart = new Date(record.payPeriodStart);
    const monthName = payPeriodStart.toLocaleString("default", {
      month: "long",
    });
    const year = payPeriodStart.getFullYear();

    const headerText = `Payslip for month of ${monthName} ${year}`;
    const headerWidth =
      (doc.getStringUnitWidth(headerText) * doc.internal.getFontSize()) /
      doc.internal.scaleFactor;
    const pageWidth = doc.internal.pageSize.getWidth();

    doc.setDrawColor(0);
    doc.setFillColor(255);
    doc.setFontSize(16);
    doc.setTextColor(0);
    doc.text(headerText, (pageWidth - headerWidth) / 2, 25);

    const totalDaysInMonth = new Date(
      year,
      payPeriodStart.getMonth() + 1,
      0
    ).getDate();
    const sundaysInMonth = Array.from(
      { length: totalDaysInMonth },
      (_, i) => new Date(year, payPeriodStart.getMonth(), i + 1)
    ).filter((date) => date.getDay() === 0).length;
    const effectiveWorkingDays = totalDaysInMonth - sundaysInMonth;

    const deductionDetails = [
      `Penalty Amount: ${record.penaltyAmount}`,
      `PF Amount: ${record.pfAmount}`,
      `Tax Amount: ${record.taxAmount}`,
    ];

    const tableData = [
      ["Full Name", record.fullName],
      ["Date of Joining", record.joiningDate],
      ["Designation", record.designation],
      ["Days in Month", totalDaysInMonth],
      ["Effective Working Days", effectiveWorkingDays],
      ["Gross Salary", record.grossSalary],
      ["Deductions", deductionDetails.join("\n")],
      ["Bonus", record.bonusAmount],
      ["Net Salary", record.netSalary],
    ];

    autoTable(doc, {
      head: [["Description", "Details"]],
      body: tableData,
      startY: 50,
      theme: "grid",
      styles: {
        halign: "left",
        textColor: [0, 0, 0],
        lineColor: [0, 0, 0],
        cellPadding: 4,
      },
      headStyles: {
        fillColor: [255, 255, 255],
        textColor: [0, 0, 0],
        lineWidth: 0.2,
        lineColor: [0, 0, 0],
      },
    });

    doc.save(`payslip_${monthName}_${year}.pdf`);
  };

  const handleOpenModal = (recordId) => {
    setSelectedRecordId(recordId);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setQuery("");
  };

  const handleQuerySubmit = async () => {
    try {
      await axios.post(
        "http://localhost:8080/payrollmanagementsystem/queries/addquery",
        {
          employeeId: employeeId,
          salaryRecordId: selectedRecordId,
          queryDescription: query,
        }
      );
      handleCloseModal();
      navigate(`${contextPath}/my-queries`, {
        state: { successMessage: "Query posted successfully!" },
      });
    } catch (error) {
      console.error("Error submitting query:", error);
      handleCloseModal();
      setErrorMsg("Unexpected error, please try again later.");
    }
  };

  const isQueryValid = query.length >= 5 && query.length <= 100;

  return (
    <>
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      {/* Error Message */}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 salary-error">
          {errorMsg}
        </div>
      )}
      <Container className="salary-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          Salary Records
        </h4>
        <InputGroup className="search-bar mb-3">
          <FormControl
            as="select"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
          >
            <option value="" disabled>
              Select Month
            </option>
            {Array.from({ length: 12 }, (_, index) => (
              <option key={index} value={index + 1}>
                {new Date(0, index).toLocaleString("default", {
                  month: "long",
                })}
              </option>
            ))}
          </FormControl>

          <FormControl
            as="select"
            value={year}
            onChange={(e) => setYear(e.target.value)}
            style={{ marginLeft: "20px" }}
          >
            <option value="" disabled>
              Select Year
            </option>
            {Array.from({ length: 21 }, (_, index) => (
              <option key={index} value={new Date().getFullYear() + index}>
                {new Date().getFullYear() - index}
              </option>
            ))}
          </FormControl>
        </InputGroup>

        <div className="salary-table-container">
          <Table bordered className="salary-table">
            <thead>
              <tr>
                <th>Pay Period Start</th>
                <th>Pay Period End</th>
                <th>Payslip</th>
                <th>Query</th>
              </tr>
            </thead>
            <tbody>
              {filteredRecords.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center">
                    No records found
                  </td>
                </tr>
              ) : (
                filteredRecords.map((record) => (
                  <tr key={record.salaryRecordId}>
                    <td>{record.payPeriodStart}</td>
                    <td>{record.payPeriodEnd}</td>
                    <td>
                      <Button
                        className="generate-button"
                        onClick={() => generatePayslipPDF(record)}
                      >
                        Download Payslip
                      </Button>
                    </td>
                    <td>
                      {role === "Payroll Specialist" ? (
                        "No Action"
                      ) : (
                        <Button
                          className="query-button"
                          onClick={() => handleOpenModal(record.salaryRecordId)}
                        >
                          Submit Query
                        </Button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </div>
      </Container>

      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Submit a Query</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormLabel style={{ fontWeight: "bold" }}>
            Description <span className="label-asterisk">*</span>
          </FormLabel>
          <FormControl
            as="textarea"
            placeholder="Describe your query (5-100 characters)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            maxLength={100}
          />
          <div className="text-center">
            <Button
              style={{ marginTop: "8px" }}
              className="query-button"
              onClick={handleQuerySubmit}
              disabled={!isQueryValid}
            >
              Send
            </Button>
          </div>
        </Modal.Body>
      </Modal>
      <ModifiedFooter />
    </>
  );
};

export default Salary;
