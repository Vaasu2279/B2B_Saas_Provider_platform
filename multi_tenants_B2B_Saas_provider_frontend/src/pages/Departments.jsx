import { useEffect, useState } from "react";
import api from "../api";
import { can } from "../utils";

export default function Departments() {
  const [list, setList] = useState([]);
  const [name, setName] = useState("");
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      const { data } = await api.get("/departments");
      setList(data);
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.clear();
        window.location.href = "/login";
      } else {
        setMsg("Could not load departments");
      }
    }
  };

  useEffect(() => { load(); }, []);

  const add = async (e) => {
    e.preventDefault();
    try {
      await api.post("/departments", { name });
      setName("");
      setMsg("");
      load();
    } catch (err) {
      setMsg(err.response?.data?.error || "Could not add department");
    }
  };

  const remove = async (id) => {
    try {
      await api.delete(`/departments/${id}`);
      load();
    } catch {
      setMsg("Could not delete");
    }
  };

  return (
    <div>
      <h2>Departments</h2>
      {msg && <p style={{ color: "red" }}>{msg}</p>}

      {can("DEPARTMENT_MANAGE") && (
        <form onSubmit={add} style={{ display: "flex", gap: 8, marginBottom: 20 }}>
          <input placeholder="Department name" value={name}
                 onChange={(e) => setName(e.target.value)} required />
          <button type="submit">Add</button>
        </form>
      )}

      <table border="1" cellPadding="6" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead><tr><th>Name</th><th></th></tr></thead>
        <tbody>
          {list.map((d) => (
            <tr key={d.id}>
              <td>{d.name}</td>
              <td>{can("DEPARTMENT_MANAGE") &&
                <button onClick={() => remove(d.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}