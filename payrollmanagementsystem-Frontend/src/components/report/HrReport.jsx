import React, { useEffect, useState } from "react";
import { Container, Table, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import axios from "axios";
import "../css/HrReport.css";
import autoTable from "jspdf-autotable";
import jsPDF from "jspdf";

const HrReport = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const navigate = useNavigate();
  const [reportData, setReportData] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (!fullName) {
      navigate(`${contextPath}/login`, {
        state: { error: "Please log in again." },
      });
    } else if (role !== "HR Manager") {
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
    document.title = "Report";
  }, []);

  const currentMonth = new Date().getMonth();
  const quarters = [
    { label: "Quarter 1", startMonth: 0 },
    { label: "Quarter 2", startMonth: 3 },
    { label: "Quarter 3", startMonth: 6 },
    { label: "Quarter 4", startMonth: 9 },
  ];

  const canGenerateReport = (startMonth) => {
    return currentMonth >= startMonth + 3;
  };

  useEffect(() => {
    if (reportData) {
      generateQuarterlyReportPDF(reportData);
    }
  }, [reportData]);

  const fetchReportData = async (quarter) => {
    try {
      const response = await axios.get(
        "http://localhost:8080/payrollmanagementsystem/salaryrecord/getquartersalary",
        {
          params: { quarter: quarter.label },
        }
      );
      setReportData(response.data);
    } catch (err) {
      console.error("Failed to fetch report data:", err);
      setErrorMsg("Failed to fetch report data");
    }
  };

  const generateQuarterlyReportPDF = (reportData) => {
    const doc = new jsPDF();

    const headerText = `Quarterly Report from ${reportData.payPeriodStart} to ${reportData.payPeriodEnd}`;
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
      ["Total Gross Salary", reportData.grossSalary],
      ["Total Net Salary", reportData.netSalary],
      ["Total Bonus Amount", reportData.bonusAmount],
      ["Total Penalty Amount", reportData.penaltyAmount],
      ["Total PF Amount", reportData.pfAmount],
      ["Total Tax Amount", reportData.taxAmount],
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

    doc.save(
      `quarterly_report_${reportData.payPeriodStart}_${reportData.payPeriodEnd}.pdf`
    );
  };

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  return (
    <>
      <HrManagerNavbar />
      {/* Error Message */}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 hr-report-error">
          {errorMsg}
        </div>
      )}
      <Container className="hr-report-container">
        <h4 className="text-center mb-4" style={{ marginTop: "20px" }}>
          HR Report
        </h4>
        <Table bordered className="hr-report-table">
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
                      className="hr-report-button"
                      onClick={() => fetchReportData(quarter)}
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
      <ModifiedFooter />
    </>
  );
};

export default HrReport;
