import { useEffect, useState } from "react";
import api from "../api";
import { can } from "../utils";

const empty = { fullName: "", email: "", password: "", roleName: "" };

export default function Users() {
  const [list, setList] = useState([]);
  const [roles, setRoles] = useState([]);
  const [f, setF] = useState(empty);
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      const [u, r] = await Promise.all([api.get("/users"), api.get("/roles")]);
      setList(u.data);
      setRoles(r.data);
      setF((prev) => ({ ...prev, roleName: prev.roleName || r.data[0] || "" }));
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.clear();
        window.location.href = "/login";
      } else {
        setMsg("You do not have permission to manage users");
      }
    }
  };

  useEffect(() => { load(); }, []);

  const change = (e) => setF({ ...f, [e.target.name]: e.target.value });

  const add = async (e) => {
    e.preventDefault();
    try {
      await api.post("/users", f);
      setF({ ...empty, roleName: roles[0] || "" });
      setMsg("");
      load();
    } catch (err) {
      setMsg(err.response?.data?.error || "Could not create user");
    }
  };

  if (!can("USER_MANAGE")) return <p>You do not have permission to manage users.</p>;

  return (
    <div>
      <h2>Users</h2>
      {msg && <p style={{ color: "red" }}>{msg}</p>}

      <form onSubmit={add} style={{ display: "grid", gap: 8, maxWidth: 360, marginBottom: 20 }}>
        <input name="fullName" placeholder="Full name" value={f.fullName} onChange={change} required />
        <input name="email" type="email" placeholder="Email" value={f.email} onChange={change} required />
        <input name="password" type="password" placeholder="Password" value={f.password} onChange={change} required />
        <select name="roleName" value={f.roleName} onChange={change}>
          {roles.map((r) => <option key={r} value={r}>{r}</option>)}
        </select>
        <button type="submit">Create user</button>
      </form>

      <table border="1" cellPadding="6" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr><th>Name</th><th>Email</th><th>Roles</th><th>Active</th></tr>
        </thead>
        <tbody>
          {list.map((u) => (
            <tr key={u.id}>
              <td>{u.fullName}</td>
              <td>{u.email}</td>
              <td>{u.roles.join(", ")}</td>
              <td>{u.active ? "Yes" : "No"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}