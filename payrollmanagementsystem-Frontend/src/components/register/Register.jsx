import React from 'react'
import HrManagerNavbar from '../navbar/HrManagerNavbar'
import ParticlesBackground from '../particles/PacticlesBackground'
import Footer from '../footer/Footer'
import '../css/Login.css'
import RegisterForm from './RegisterForm'

const Register=()=> {
  return (
    <div className="register-page">
      <HrManagerNavbar />
      <div className="particles-section">
        <ParticlesBackground />
        <RegisterForm/>
      </div>
      <Footer />
    </div>
  )
}

export default Register