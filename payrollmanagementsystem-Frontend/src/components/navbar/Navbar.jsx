import React from 'react';
import { Navbar as BootstrapNavbar } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Logo from "../images/paynet.png";
import '../css/Navbar.css'; 

const Navbar = () => {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || ""; 

  return (
    <BootstrapNavbar className="navbar">
      <BootstrapNavbar.Brand 
        as={Link} 
        to={`${contextPath}`} 
        className="navbar-brand"
      >
        <img 
          src={Logo} 
          alt="Logo"
          className="nav-logo d-inline-block align-top mr-2" 
        />
        PayNet 
      </BootstrapNavbar.Brand>
    </BootstrapNavbar>
  );
};

export default Navbar;
