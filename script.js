document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("point-form");
  const errorMsg = document.getElementById("error-msg");
  const results = document.getElementById("results");

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    errorMsg.textContent = "";

    const xValues = Array.from(form.querySelectorAll("input[name='x']:checked"))
                         .map(cb => parseFloat(cb.value));
    const yInput = document.getElementById("y").value.replace(",", ".");
    const y = parseFloat(yInput);
    const rValue = form.querySelector("input[name='r']:checked");
    const r = rValue ? parseFloat(rValue.value) : null;

    if (xValues.length === 0) {
      errorMsg.textContent = "Выберите хотя бы одно значение X.";
      return;
    }
    if (isNaN(y) || y < -3 || y > 3) {
      errorMsg.textContent = "Y должно быть числом от -3 до 3.";
      return;
    }
    if (r === null) {
      errorMsg.textContent = "Выберите значение R.";
      return;
    }

    // Отправляем POST-запрос для каждого X
    xValues.forEach((x) => {
      const formData = new URLSearchParams();
      formData.append("x", x);
      formData.append("y", y);
      formData.append("r", r);

      fetch("/fcgi-bin/wlab1.jar", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData.toString()
      })
      .then(resp => {
        if (!resp.ok) throw new Error("Сервер вернул ошибку " + resp.status);
        return resp.json();
      })
      .then(data => {
        if (data.reason) {
          errorMsg.textContent = "Ошибка: " + data.reason;
        } else {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${data.x}</td>
            <td>${data.y}</td>
            <td>${data.r}</td>
            <td>${data.result ? "Да" : "Нет"}</td>
            <td>${data.now}</td>
            <td>${data.duration_ns} нс</td>
          `;
          results.appendChild(row);
        }
      })
      .catch(err => {
        console.error(err);
        errorMsg.textContent = "Не удалось связаться с сервером.";
      });
    });
  });
});
