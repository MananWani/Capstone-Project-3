import React, { useEffect } from "react";
import HrManagerNavbar from "../navbar/HrManagerNavbar";
import ParticlesBackground from "../particles/PacticlesBackground";
import Footer from "../footer/Footer";
import { useNavigate } from "react-router-dom";
import '../css/HrManagerHome.css';

const HrManagerHome = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";

  const navigate = useNavigate();
  const fullName = localStorage.getItem('fullName');
  const role =localStorage.getItem('role');

  useEffect(() => {
    if (!fullName) {
        navigate(`${contextPath}/login`,{ state: { error: "Please log in again." } }); 
    }else if(role!=="HR Manager"){
      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");
        navigate(`${contextPath}/login`,{ state: { error: "Unauthorized access" } });
    }
}, [fullName, navigate, contextPath,role]);

useEffect(() => {
  document.title = "HR Home";
}, []);
  return (
    <div className="admin-home-page">
      <HrManagerNavbar />
      <div className="particles-section">
        <ParticlesBackground />
        <div className="overlay">
          <h1>Hello, {fullName}.</h1>
          <p>Welcome back to PayNet.</p>
          <p>Let’s make payroll processing a breeze together!</p>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default HrManagerHome;
