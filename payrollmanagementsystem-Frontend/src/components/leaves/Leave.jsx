import React from 'react'
import HrManagerNavbar from '../navbar/HrManagerNavbar'
import ParticlesBackground from '../particles/PacticlesBackground'
import Footer from '../footer/Footer'
import '../css/Login.css'
import LeaveForm from './LeaveForm'
import PayrollSpecialistNavbar from '../navbar/PayrollSpecialistNavbar'
import ManagerNavbar from '../navbar/ManagerNavbar'
import EmployeeNavbar from '../navbar/EmployeeNavbar'

const Leave=()=> {
    const role = localStorage.getItem("role");
  
  return (
    <div className="leave-page">
      {role === "HR Manager" && <HrManagerNavbar />}
      {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
      {role === "Manager" && <ManagerNavbar />}
      {role === "Employee" && <EmployeeNavbar />}
      <div className="particles-section">
        <ParticlesBackground />
        <LeaveForm/>
      </div>
      <Footer />
    </div>
  )
}

export default Leave