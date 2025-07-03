function filterFaq() {
  const keyword = document.getElementById("faqSearchInput").value.toLowerCase();
  const faqItems = document.querySelectorAll("#faqAccordion .accordion-item");

  faqItems.forEach(item => {
    const question = item.querySelector(".accordion-button").innerText.toLowerCase();
    const answer = item.querySelector(".accordion-body").innerText.toLowerCase();

    if (question.includes(keyword) || answer.includes(keyword)) {
      item.style.display = "block";
    } else {
      item.style.display = "none";
    }
  });
}

document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("faqSearchInput");
  if (input) {
    input.addEventListener("keydown", function (event) {
      if (event.key === "Enter") {
        filterFaq();
      }
    });
  }
});

