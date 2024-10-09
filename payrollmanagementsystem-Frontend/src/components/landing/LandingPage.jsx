import React,{useEffect,useState} from "react";
import { Link } from "react-router-dom"; 
import Footer from "../footer/Footer";
import Navbar from "../navbar/Navbar";
import ParticleBackground from "../particles/PacticlesBackground";
import '../css/LandingPage.css'; 
import { useLocation } from "react-router-dom";

const LandingPage = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || ""; 
  const location = useLocation();
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (location.state && location.state.errorMessage) {
      setErrorMessage(location.state.errorMessage);
      localStorage.removeItem("employeeId");
      localStorage.removeItem("fullName");
      localStorage.removeItem("role");
      localStorage.removeItem("logId");
    }
  }, [location.state]);

  useEffect(() => {
    if (errorMessage) {
      const timer = setTimeout(() => {
        setErrorMessage("");
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [errorMessage]);
  return (
    <>
    <div className="landing-page">
      <Navbar />
      {errorMessage && (
      <div className="alert alert-danger text-center mb-3 landing-error">
        {errorMessage}
      </div>
    )}
      <div className="particles-section">
        <ParticleBackground /> 
        <div className="overlay">
          <h1>Welcome to PayNet.</h1>
          <p>Streamline Your Payroll, Empower Your Team!</p>
          <Link to={`${contextPath}/login`} className="landing-page-button">Login</Link>
        </div>
      </div> 
      <Footer />
    </div>
    </>
  );
};

export default LandingPage;
