import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

export default function Login() {
  const [f, setF] = useState({ subdomain: "", email: "", password: "" });
  const [msg, setMsg] = useState("");
  const navigate = useNavigate();

  const change = (e) => setF({ ...f, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    try {
      const { data } = await api.post("/auth/login", f);
      localStorage.setItem("token", data.accessToken);
      localStorage.setItem("authorities", JSON.stringify(data.authorities));
      localStorage.setItem("email", data.email);
      navigate("/employees");
      window.location.reload();
    } catch (err) {
      setMsg(err.response?.data?.error || "Login failed");
    }
  };

  return (
    <form onSubmit={submit} style={{ display: "grid", gap: 10, maxWidth: 360 }}>
      <h2>Login</h2>
      <input name="subdomain" placeholder="Company subdomain" onChange={change} required />
      <input name="email" type="email" placeholder="Email" onChange={change} required />
      <input name="password" type="password" placeholder="Password" onChange={change} required />
      <button type="submit">Login</button>
      {msg && <p style={{ color: "red" }}>{msg}</p>}
    </form>
  );
}