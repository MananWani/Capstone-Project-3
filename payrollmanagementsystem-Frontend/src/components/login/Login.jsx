import React from "react";
import "../css/Login.css";
import Navbar from "../navbar/Navbar";
import ParticlesBackground from "../particles/PacticlesBackground";
import Footer from "../footer/Footer";
import LoginForm from "./LoginForm"; 

const Login = () => {
  return (
    <div className="login-page">
      <Navbar />
      <div className="particles-section">
        <ParticlesBackground />
        <LoginForm /> 
      </div>
      <Footer />
    </div>
  );
};

export default Login;
