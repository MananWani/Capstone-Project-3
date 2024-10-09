import React, { useEffect, useState } from "react";
import { Container, Table, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import autoTable from "jspdf-autotable";
import jsPDF from "jspdf";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import '../css/TaxReport.css';

const TaxReport = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const navigate = useNavigate();
  const [taxData, setTaxData] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");

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

  const fetchTaxData = async (quarter) => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/salaryrecord/getquartertax",
        {
          params: { quarter: quarter.label,employeeId:employeeId },
        }
      );
      setTaxData(response.data);
    } catch (err) {
      console.error("Failed to fetch tax data:", err);
      setErrorMsg("Failed to fetch tax data");
    }
  };

  useEffect(() => {
    if (taxData) {
      generateTaxReportPDF(taxData);
    }
  }, [taxData,employeeId]);

  const generateTaxReportPDF = (taxData) => {
    const doc = new jsPDF();

    const headerText = `Tax Report from ${taxData.payPeriodStart} to ${taxData.payPeriodEnd}`;
    const headerWidth =
      (doc.getStringUnitWidth(headerText) * doc.internal.getFontSize()) /
      doc.internal.scaleFactor;
    const pageWidth = doc.internal.pageSize.getWidth();

    doc.setDrawColor(0);
    doc.setFillColor(255);
    doc.setFontSize(16);
    doc.setTextColor(0);
    doc.text(headerText, (pageWidth - headerWidth) / 2, 25);

    const tableData = [
      ["Name of Deductor", "Axis Bank"],
      ["Address of Deductor", "SLN HVP Aster, Whitefield Main Rd, Bengaluru, Karnataka, 560048"],
      ["PAN of Deductor", "GDPLN7612C"],
      ["Total Gross Salary", taxData.grossSalary],
      ["Total Tax Deducted", taxData.taxDeducted],
      ["Net Salary", taxData.netSalary],
      ["Assessment Year", taxData.year],
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

    doc.save(`tax_report_${taxData.payPeriodStart}_${taxData.payPeriodEnd}.pdf`);
  };

  useEffect(() => {
    document.title = "Tax Report";
  }, []);

  const quarters = [
    { label: "Quarter 1", startMonth: 0 },
    { label: "Quarter 2", startMonth: 3 },
    { label: "Quarter 3", startMonth: 6 },
    { label: "Quarter 4", startMonth: 9 },
  ];

  const currentMonth = new Date().getMonth();

  const canGenerateReport = (startMonth) => {
    return currentMonth >= startMonth + 3;
  };

  return (
    <>{role === "HR Manager" && <HrManagerNavbar />}
    {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
    {role === "Manager" && <ManagerNavbar />}
    {role === "Employee" && <EmployeeNavbar />}
    <Container className="tax-report-container">
        {/* Error Message */}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 tax-report-error">
          {errorMsg}
        </div>
      )}
      <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
        Tax Report
      </h4>
      <Table bordered className="tax-report-table">
        <thead>
          <tr>
            <th style={{ width: "200px" }}>Quarter</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {quarters.map((quarter) => (
            <tr key={quarter.label}>
              <td>{quarter.label}</td>
              <td>
              {canGenerateReport(quarter.startMonth) ? (
                   <Button
                   className="tax-report-button"
                   onClick={() => fetchTaxData(quarter)}
                 >
                   Generate
                 </Button>
                  ) : (
                    <span>No Action</span>
                  )}
                
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
    <ModifiedFooter/>
    </>
  );
};

export default TaxReport;
