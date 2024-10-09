import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import "./App.css";
import LandingPage from "./components/landing/LandingPage";
import Login from "./components/login/Login";
import HrManagerHome from "./components/home/HrManagerHome";
import Register from "./components/register/Register";
import AdminHome from "./components/home/AdminHome";
import AddRole from "./components/admin/AddRole";
import AddDesignation from "./components/admin/AddDesignation";
import HrEmployeeList from "./components/hr/HrEmployeeList";
import Attendance from "./components/attendance/Attendance";
import LeaveType from "./components/admin/LeaveType";
import PayrollSpecialistHome from "./components/home/PayrollSpecialistHome";
import ManageEmployees from "./components/payroll-specialist/ManageEmployees";
import ManageEmployeeRoles from "./components/admin/ManageEmployeeRoles";
import Leave from "./components/leaves/Leave";
import LeaveRequestList from "./components/leaves/LeaveRequestList";
import MyLeavesList from "./components/leaves/MyLeavesList";
import EmployeeList from "./components/employee/EmployeeList";
import Salary from "./components/salary/Salary";
import ManagerHome from "./components/home/ManagerHome";
import PendingLeaveRequests from "./components/manager/PendingLeaveRequests";
import EmployeeHome from "./components/home/EmployeeHome";
import Profile from "./components/profile/profile";
import ChangePassword from "./components/profile/ChangePassword";
import ManageSalary from "./components/payroll-specialist/ManageSalary";
import QueryList from "./components/employee/QueryList";
import ManageQueries from "./components/payroll-specialist/ManageQueries";
import TeamAttendance from "./components/team/TeamAttendance";
import HrReport from "./components/report/HrReport";
import TeamRating from "./components/team/TeamRating";
import TaxReport from "./components/report/TaxReport";

function App() {
  const contextPath = process.env.REACT_APP_CONTEXT_PATH || "";

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path={`${contextPath}/`} element={<LandingPage />} />
          <Route path={`${contextPath}/login`} element={<Login />} />
          <Route path={`${contextPath}/admin-home`} element={<AdminHome />} />
          <Route path={`${contextPath}/hr-home`} element={<HrManagerHome />} />
          <Route
            path={`${contextPath}/payroll-home`}
            element={<PayrollSpecialistHome />}
          />
          <Route
            path={`${contextPath}/manager-home`}
            element={<ManagerHome />}
          />
          <Route
            path={`${contextPath}/employee-home`}
            element={<EmployeeHome />}
          />
          <Route path={`${contextPath}/add-role`} element={<AddRole />} />
          <Route
            path={`${contextPath}/add-designation`}
            element={<AddDesignation />}
          />
          <Route
            path={`${contextPath}/add-leave-type`}
            element={<LeaveType />}
          />
          <Route path={`${contextPath}/add-employee`} element={<Register />} />
          <Route
            path={`${contextPath}/hr-view-employee`}
            element={<HrEmployeeList />}
          />
          <Route path={`${contextPath}/attendance`} element={<Attendance />} />
          <Route
            path={`${contextPath}/manage-employee`}
            element={<ManageEmployees />}
          />
          <Route
            path={`${contextPath}/manage-employee-roles`}
            element={<ManageEmployeeRoles />}
          />
          <Route path={`${contextPath}/apply-leave`} element={<Leave />} />
          <Route
            path={`${contextPath}/leave-requests`}
            element={<LeaveRequestList />}
          />
          <Route path={`${contextPath}/my-leaves`} element={<MyLeavesList />} />
          <Route path={`${contextPath}/my-queries`} element={<QueryList />} />
          <Route
            path={`${contextPath}/view-employee`}
            element={<EmployeeList />}
          />
          <Route path={`${contextPath}/my-salary`} element={<Salary />} />
          <Route path={`${contextPath}/profile`} element={<Profile />} />
          <Route path={`${contextPath}/team-rating`} element={<TeamRating />} />
          <Route path={`${contextPath}/report`} element={<HrReport />} />
          <Route path={`${contextPath}/tax-report`} element={<TaxReport />} />
          <Route
            path={`${contextPath}/manage-salary`}
            element={<ManageSalary />}
          />
          <Route
            path={`${contextPath}/manage-queries`}
            element={<ManageQueries />}
          />
          <Route
            path={`${contextPath}/team-attendance`}
            element={<TeamAttendance />}
          />
          <Route
            path={`${contextPath}/change-password`}
            element={<ChangePassword />}
          />
          <Route
            path={`${contextPath}/employee-leave-requests`}
            element={<PendingLeaveRequests />}
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
