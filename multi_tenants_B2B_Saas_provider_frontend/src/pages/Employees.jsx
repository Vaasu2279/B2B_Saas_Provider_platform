import { useEffect, useState } from "react";
import api from "../api";
import { can } from "../utils";

const empty = {
  departmentId: "", employeeCode: "", fullName: "", email: "",
  jobTitle: "", salary: "", joinDate: "",
};

export default function Employees() {
  const [list, setList] = useState([]);
  const [depts, setDepts] = useState([]);
  const [f, setF] = useState(empty);
  const [msg, setMsg] = useState("");

  const deptName = (id) => depts.find((d) => d.id === id)?.name || "-";

  const load = async () => {
    try {
      const { data } = await api.get("/employees");
      setList(data);
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.clear();
        window.location.href = "/login";
      } else if (err.response?.status === 403) {
        setMsg("You do not have permission to view employees");
      } else {
        setMsg("Failed to load employees");
      }
      return;
    }
    try {
      const { data } = await api.get("/departments");
      setDepts(data);
    } catch {
      /* departments are optional for display */
    }
  };

  useEffect(() => { load(); }, []);

  const change = (e) => setF({ ...f, [e.target.name]: e.target.value });

  const add = async (e) => {
    e.preventDefault();
    try {
      await api.post("/employees", {
        ...f,
        departmentId: f.departmentId ? Number(f.departmentId) : null,
        salary: f.salary ? Number(f.salary) : null,
        joinDate: f.joinDate || null,
      });
      setF(empty);
      setMsg("");
      load();
    } catch (err) {
      setMsg(err.response?.data?.error || "Could not add employee");
    }
  };

  const remove = async (id) => {
    try {
      await api.delete(`/employees/${id}`);
      load();
    } catch {
      setMsg("Could not delete");
    }
  };

  return (
    <div>
      <h2>Employees</h2>
      {msg && <p style={{ color: "red" }}>{msg}</p>}

      {can("EMPLOYEE_WRITE") && (
        <form onSubmit={add} style={{ display: "grid", gap: 8, maxWidth: 360, marginBottom: 20 }}>
          <select name="departmentId" value={f.departmentId} onChange={change}>
            <option value="">-- Select department --</option>
            {depts.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
          </select>
          <input name="employeeCode" placeholder="Code (E002)" value={f.employeeCode} onChange={change} required />
          <input name="fullName" placeholder="Full name" value={f.fullName} onChange={change} required />
          <input name="email" placeholder="Email" value={f.email} onChange={change} />
          <input name="jobTitle" placeholder="Job title" value={f.jobTitle} onChange={change} />
          <input name="salary" type="number" placeholder="Salary" value={f.salary} onChange={change} />
          <input name="joinDate" type="date" value={f.joinDate} onChange={change} />
          <button type="submit">Add employee</button>
        </form>
      )}

      <table border="1" cellPadding="6" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>Code</th><th>Name</th><th>Department</th><th>Email</th>
            <th>Title</th><th>Salary</th><th>Status</th><th></th>
          </tr>
        </thead>
        <tbody>
          {list.map((e) => (
            <tr key={e.id}>
              <td>{e.employeeCode}</td>
              <td>{e.fullName}</td>
              <td>{deptName(e.departmentId)}</td>
              <td>{e.email}</td>
              <td>{e.jobTitle}</td>
              <td>{e.salary}</td>
              <td>{e.status}</td>
              <td>{can("EMPLOYEE_DELETE") &&
                <button onClick={() => remove(e.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}