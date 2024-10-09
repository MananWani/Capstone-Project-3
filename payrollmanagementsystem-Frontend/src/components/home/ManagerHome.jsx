import React, { useEffect } from "react";
import ManagerNavbar from "../navbar/ManagerNavbar";
import ParticlesBackground from "../particles/PacticlesBackground";
import Footer from "../footer/Footer";
import { useNavigate } from "react-router-dom";
import '../css/ManagerHome.css';

const ManagerHome = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";

  const navigate = useNavigate();
  const fullName = localStorage.getItem('fullName');
  const role =localStorage.getItem('role');

  useEffect(() => {
    if (!fullName) {
        navigate(`${contextPath}/login`,{ state: { error: "Please log in again." } }); 
    }else if(role!=="Manager"){
      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");
        navigate(`${contextPath}/login`,{ state: { error: "Unauthorized access" } });
    }
}, [fullName, navigate, contextPath,role]);

useEffect(() => {
  document.title = "Manager Home";
}, []);
  return (
    <div className="manager-home-page">
      <ManagerNavbar />
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

export default ManagerHome;
