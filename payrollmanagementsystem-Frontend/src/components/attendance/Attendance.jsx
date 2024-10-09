import React, { useEffect, useState, useCallback } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import ModifiedFooter from "../footer/ModifiedFooter";
import "../css/Attendance.css";
import axios from "axios";
import { Modal, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import PayrollSpecialistNavbar from "../navbar/PayrollSpecialistNavbar";
import ManagerNavbar from "../navbar/ManagerNavbar";
import EmployeeNavbar from "../navbar/EmployeeNavbar";

const Attendance = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");
  const role = localStorage.getItem("role");
  const employeeId = localStorage.getItem("employeeId");
  const [events, setEvents] = useState([]);
  const [attendanceData, setAttendanceData] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isRegularizeModalOpen, setIsRegularizeModalOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
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

  const fetchAttendance = useCallback(async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/payrollmanagementsystem/attendance/getattendanceforemployee`,
        {
          params: { employeeId: employeeId },
        }
      );
      setAttendanceData(response.data);
    } catch (error) {
      console.error("Error fetching attendance data");
      setAttendanceData([]);
    }
  }, [employeeId]);

  useEffect(() => {
    fetchAttendance();
  }, [fetchAttendance]);

  useEffect(() => {
    document.title = "Attendance";
  }, []);

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  useEffect(() => {
    if (errorMsg) {
      const timer = setTimeout(() => {
        setErrorMsg("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMsg]);

  useEffect(() => {
    const formattedEvents = attendanceData.map(
      ({ attendanceForDate, status }) => {
        let backgroundColor = "transparent";
        let title = "";

        if (status === "Present") {
          backgroundColor = "green";
          title = "P";
        } else if (status === "Absent") {
          backgroundColor = "red";
          title = "A";
        } else if (status === "Leave") {
          backgroundColor = "orange";
          title = "L";
        } else if (status === "Half Day") {
          backgroundColor = "orange";
          title = "HD";
        }

        return {
          title: title,
          start: attendanceForDate,
          allDay: true,
          backgroundColor: backgroundColor,
          borderColor: "transparent",
        };
      }
    );

    setEvents(formattedEvents);
  }, [attendanceData]);

  const now = new Date();
  const options = {
    timeZone: "Asia/Kolkata",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  };

  const formatter = new Intl.DateTimeFormat("en-IN", options);
  const [day, month, year] = formatter.format(now).split("/");
  const today = `${year}-${month}-${day}`;

  const hasTodayRecord = attendanceData.some(
    (record) => record.attendanceForDate === today
  );

  const isWeekend = () => {
    const dayOfWeek = now.getDay();
    return dayOfWeek === 0 || dayOfWeek === 6;
  };

  const isWithinTimeRange = () => {
    const now = new Date();
    const hour = now.getHours();
    return hour >= 9 && hour < 18;
  };

  const canRegularize = () => {
    const now = new Date();
    const hour = now.getHours();
    const minutes = now.getMinutes();
    return (hour > 18 && minutes >= 1) || (hour > 18 && hour < 23);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const openModalRegularize = () => {
    setIsRegularizeModalOpen(true);
  };

  const closeModalRegularize = () => {
    setIsRegularizeModalOpen(false);
  };

  const handleMarkAttendance = async () => {
    try {
      await axios.post(
        `http://localhost:8080/payrollmanagementsystem/attendance/markattendance`,
        {
          attendanceByEmployee: employeeId,
          attendanceForDate: today,
          status: "Present",
        }
      );
      fetchAttendance();
      setSuccessMessage("Attendance marked successfully!");
      handleCloseModal();
    } catch (error) {
      console.error(error);
      handleCloseModal();
    }
  };

  const handleRegularize = async () => {
    try {
      await axios.post(
        `http://localhost:8080/payrollmanagementsystem/attendance/regularize`
      );
      fetchAttendance();
      setSuccessMessage("Attendance regularized!");
      closeModalRegularize();
    } catch (error) {
      console.error(error);
      setErrorMsg("Unexpected error, please try again.");
      closeModalRegularize();
    }
  };

  return (
    <>
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success text-center mb-3 attendance-success">
          {successMessage}
        </div>
      )}
      {/* Error Message */}
      {errorMsg && (
        <div className="alert alert-danger text-center mb-3 attendance-error">
          {errorMsg}
        </div>
      )}
      <div className="calendar-container">
        <FullCalendar
          plugins={[dayGridPlugin]}
          initialView="dayGridMonth"
          events={events}
          customButtons={{
            markToday: {
              text: "Mark Attendance",
              click: handleOpenModal,
            },
            ...(role === "HR Manager" && {
              regularize: {
                text: "Regularize",
                click: openModalRegularize,
              },
            }),
          }}
          headerToolbar={{
            left: "title",
            center:
              isWeekend() || !canRegularize() || role !== "HR Manager"
                ? ""
                : "regularize",
            right:
              hasTodayRecord || isWeekend() || !isWithinTimeRange()
                ? "today prev,next"
                : "markToday today prev,next",
          }}
        />
      </div>
      <ModifiedFooter />

      <Modal show={isModalOpen} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Attendance</Modal.Title>
        </Modal.Header>
        <Modal.Body>Mark attendance for today?</Modal.Body>
        <Modal.Footer>
          <Button className="mark-button" onClick={handleMarkAttendance}>
            OK
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={isRegularizeModalOpen}
        onHide={closeModalRegularize}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>Regularization</Modal.Title>
        </Modal.Header>
        <Modal.Body>Regularize attendance for today?</Modal.Body>
        <Modal.Footer>
          <Button className="mark-button" onClick={handleRegularize}>
            OK
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default Attendance;
