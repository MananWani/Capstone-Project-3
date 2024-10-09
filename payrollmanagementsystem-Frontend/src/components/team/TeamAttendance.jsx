import React from 'react'
import ParticlesBackground from '../particles/PacticlesBackground'
import Footer from '../footer/Footer'
import '../css/Login.css'
import ManagerNavbar from '../navbar/ManagerNavbar'
import TeamAttendanceForm from './TeamAttendanceForm'

const TeamAttendance=()=> {
  return (
    <div className="register-page">
      <ManagerNavbar />
      <div className="particles-section">
        <ParticlesBackground />
        <TeamAttendanceForm/>
      </div>
      <Footer />
    </div>
  )
}

export default TeamAttendance