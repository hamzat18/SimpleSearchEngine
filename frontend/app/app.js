//user file select
function handleFileSelect() {
  const input = document.getElementById("fileInput");
  const label = document.getElementById("uploadText");
  // Check if any files are selected
  if (input.files.length > 0) {
    let names = Array.from(input.files)
      .map((f) => f.name)
      .join(", ");
    if (names.length > 40) names = names.substring(0, 40) + "...";
    // Update label
    label.innerHTML = `<strong>${input.files.length} File(s) Selected:</strong><br><span style="font-size:0.9em; color:#333">${names}</span>`;
  } else {
    label.innerHTML = "<strong>Click to Select Files</strong>";
  }
}

//upload file to the server
function uploadFiles() {
  const input = document.getElementById("fileInput");
  const status = document.getElementById("uploadStatus");
  if (input.files.length === 0) return;
  //apprent all uploaded files
  const formData = new FormData();
  for (let file of input.files) formData.append("files", file);

  status.innerText = "⏳ Uploading...";
  //send post request to upload
  fetch("/api/upload", { method: "POST", body: formData })
    .then((res) => res.text())
    .then((text) => {
      status.innerText = "✅ " + text;
      status.style.color = "green";
      document.getElementById("uploadText").innerHTML =
        "<strong>Click to Select Files</strong>";
      input.value = "";
    })
    .catch((err) => {
      status.innerText = "❌ Error uploading";
      status.style.color = "red";
    });
}

function handleEnter(e) {
  if (e.key === "Enter") performSearch();
}

//gets search result and display on frontend
function performSearch() {
  const query = document.getElementById("searchInput").value;
  const resultsDiv = document.getElementById("results");
  if (!query) return;

  resultsDiv.innerHTML = "<p style='text-align:center'>🔍 Searching...</p>";
  //get search resuls from API
  fetch("/api/search?q=" + encodeURIComponent(query))
    .then((res) => res.json())
    .then((data) => {
      resultsDiv.innerHTML = "";
      if (data.length === 0) {
        resultsDiv.innerHTML =
          "<p style='text-align:center'>No matches found.</p>";
        return;
      }
      //render each search result
      data.forEach((item) => {
        const div = document.createElement("div");
        div.className = "result-item";
        div.innerHTML = `
                    <div class="doc-title" style="display: flex; justify-content: space-between; align-items: center;">
                        <span>📄 ${item.docName}</span>
                        <span style="background-color: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 12px; font-size: 0.85em; font-weight: bold;">
                            ${item.frequency} Matches
                        </span>
                    </div>
                    <div class="doc-snippet" style="margin-top: 8px;">${item.snippet}</div>
                `;
        //add result to display container
        resultsDiv.appendChild(div);
      });
    });
}
