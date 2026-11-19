import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

export default function Register() {
  const [f, setF] = useState({
    companyName: "",
    subdomain: "",
    adminName: "",
    adminEmail: "",
    adminPassword: "",
  });
  const [msg, setMsg] = useState("");
  const navigate = useNavigate();

  const change = (e) => setF({ ...f, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/tenants/register", f);
      navigate("/login");
    } catch (err) {
      setMsg(err.response?.data?.error || "Registration failed");
    }
  };

  return (
    <form onSubmit={submit} style={{ display: "grid", gap: 10, maxWidth: 360 }}>
      <h2>Register Company</h2>
      <input name="companyName" placeholder="Company name" onChange={change} required />
      <input name="subdomain" placeholder="Subdomain (e.g. techcorp)" onChange={change} required />
      <input name="adminName" placeholder="Admin name" onChange={change} required />
      <input name="adminEmail" type="email" placeholder="Admin email" onChange={change} required />
      <input name="adminPassword" type="password" placeholder="Password" onChange={change} required />
      <button type="submit">Register</button>
      {msg && <p style={{ color: "red" }}>{msg}</p>}
    </form>
  );
}