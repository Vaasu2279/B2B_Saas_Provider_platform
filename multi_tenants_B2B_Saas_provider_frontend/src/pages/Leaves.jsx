import { useEffect, useState } from "react";
import api from "../api";
import { can } from "../utils";

export default function Leaves() {
  const [list, setList] = useState([]);
  const [f, setF] = useState({ fromDate: "", toDate: "", reason: "" });
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      const url = can("LEAVE_APPROVE") ? "/leaves" : "/leaves/mine";
      const { data } = await api.get(url);
      setList(data);
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.clear();
        window.location.href = "/login";
      } else {
        setMsg("Could not load leaves");
      }
    }
  };

  useEffect(() => { load(); }, []);

  const change = (e) => setF({ ...f, [e.target.name]: e.target.value });

  const apply = async (e) => {
    e.preventDefault();
    try {
      await api.post("/leaves", f);
      setF({ fromDate: "", toDate: "", reason: "" });
      setMsg("");
      load();
    } catch (err) {
      setMsg(err.response?.data?.error || "Could not apply");
    }
  };

  const decide = async (id, action) => {
    try {
      await api.put(`/leaves/${id}/${action}`);
      load();
    } catch (err) {
      setMsg(err.response?.data?.error || "Action failed");
    }
  };

  return (
    <div>
      <h2>Leave Requests</h2>
      {msg && <p style={{ color: "red" }}>{msg}</p>}

      {can("LEAVE_APPLY") && (
        <form onSubmit={apply} style={{ display: "grid", gap: 8, maxWidth: 360, marginBottom: 20 }}>
          <input name="fromDate" type="date" value={f.fromDate} onChange={change} required />
          <input name="toDate" type="date" value={f.toDate} onChange={change} required />
          <input name="reason" placeholder="Reason" value={f.reason} onChange={change} />
          <button type="submit">Apply for leave</button>
        </form>
      )}

      <table border="1" cellPadding="6" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr><th>Applicant</th><th>From</th><th>To</th><th>Reason</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          {list.map((l) => (
            <tr key={l.id}>
              <td>{l.applicantEmail}</td>
              <td>{l.fromDate}</td>
              <td>{l.toDate}</td>
              <td>{l.reason}</td>
              <td>{l.status}</td>
              <td>
                {can("LEAVE_APPROVE") && l.status === "PENDING" && (
                  <>
                    <button onClick={() => decide(l.id, "approve")}>Approve</button>{" "}
                    <button onClick={() => decide(l.id, "reject")}>Reject</button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}