"use strict";
(() => {
  const filters = document.querySelectorAll("[data-filter]");
  const search = document.getElementById("coffee-search");
  if (!search) return;
  let category = "All";
  function filter() {
    const query = search.value.trim().toLowerCase();
    let count = 0;
    for (const entry of document.querySelectorAll(".menu-entry")) {
      const visible =
        (category === "All" || entry.dataset.category === category) &&
        entry.dataset.name.toLowerCase().includes(query);
      entry.hidden = !visible;
      if (visible) count++;
    }
    document.getElementById("no-coffees").hidden = count > 0;
  }
  for (const button of filters)
    button.addEventListener("click", () => {
      category = button.dataset.filter;
      for (const candidate of filters) {
        candidate.classList.toggle("active", candidate === button);
        candidate.setAttribute("aria-pressed", String(candidate === button));
      }
      filter();
    });
  search.addEventListener("input", filter);
})();
