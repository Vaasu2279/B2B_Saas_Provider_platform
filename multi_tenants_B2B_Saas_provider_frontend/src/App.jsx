import { Routes, Route, Navigate, NavLink, useNavigate } from "react-router-dom";
import { can } from "./utils";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Employees from "./pages/Employees";
import Departments from "./pages/Departments";
import Leaves from "./pages/Leaves";
import Users from "./pages/Users";

function Protected({ children }) {
  return localStorage.getItem("token") ? children : <Navigate to="/login" />;
}

export default function App() {
  const navigate = useNavigate();
  const loggedIn = !!localStorage.getItem("token");

  const logout = () => {
    localStorage.clear();
    navigate("/login");
    window.location.reload();
  };

  return (
    <>
      <nav className="navbar">
        {!loggedIn && <NavLink to="/login">Login</NavLink>}
        {!loggedIn && <NavLink to="/register">Register Company</NavLink>}
        {loggedIn && <NavLink to="/employees">Employees</NavLink>}
        {loggedIn && (can("DEPARTMENT_MANAGE") || can("EMPLOYEE_READ")) && (
          <NavLink to="/departments">Departments</NavLink>
        )}
        {loggedIn && <NavLink to="/leaves">Leaves</NavLink>}
        {loggedIn && can("USER_MANAGE") && <NavLink to="/users">Users</NavLink>}
        {loggedIn && (
          <button className="logout" onClick={logout}>
            Logout ({localStorage.getItem("email")})
          </button>
        )}
      </nav>

      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/employees" element={<Protected><Employees /></Protected>} />
          <Route path="/departments" element={<Protected><Departments /></Protected>} />
          <Route path="/leaves" element={<Protected><Leaves /></Protected>} />
          <Route path="/users" element={<Protected><Users /></Protected>} />
          <Route path="*" element={<Navigate to={loggedIn ? "/employees" : "/login"} />} />
        </Routes>
      </div>
    </>
  );
}