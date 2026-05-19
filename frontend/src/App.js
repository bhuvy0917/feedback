
import React, { useState } from "react";
import axios from "axios";

function App() {
  const [name, setName] = useState("");
  const [feedback, setFeedback] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    await axios.post("http://localhost:8080/api/feedback", {
      name,
      feedback
    });
    alert("Feedback submitted!");
    setName("");
    setFeedback("");
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Program Feedback</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Name:</label><br/>
          <input value={name} onChange={(e) => setName(e.target.value)} required />
        </div>
        <div>
          <label>Feedback:</label><br/>
          <textarea value={feedback} onChange={(e) => setFeedback(e.target.value)} required />
        </div>
        <button type="submit">Submit</button>
      </form>
    </div>
  );
}

export default App;
