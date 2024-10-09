import React from 'react'
import HrManagerNavbar from '../navbar/HrManagerNavbar'
import ParticlesBackground from '../particles/PacticlesBackground'
import Footer from '../footer/Footer'
import '../css/Login.css'
import ChangePasswordForm from './ChangePasswordForm'
import ManagerNavbar from '../navbar/ManagerNavbar'
import EmployeeNavbar from '../navbar/EmployeeNavbar'
import PayrollSpecialistNavbar from '../navbar/PayrollSpecialistNavbar'

const ChangePassword=()=> {
    const role = localStorage.getItem("role");
  return (
    <>
    {role === "HR Manager" && <HrManagerNavbar />}
    {role === "Payroll Specialist" && <PayrollSpecialistNavbar />}
    {role === "Manager" && <ManagerNavbar />}
    {role === "Employee" && <EmployeeNavbar />}
    <div className="change-password-page">
      <div className="particles-section">
        <ParticlesBackground />
        <ChangePasswordForm/>
      </div>
      <Footer />
    </div>
    </>
  )
}

export default ChangePassword