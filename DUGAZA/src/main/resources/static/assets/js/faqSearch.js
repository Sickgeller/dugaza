document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("faqSearchInput");
  if (input) {
    input.addEventListener("keydown", function (event) {
      if (event.key === "Enter") {
        filterFaq(); // 여기서 부르는 함수는 반드시 하나여야 해
      }
    });
  }
});

// ✅ 최종 filterFaq 하나만 선언
function filterFaq() {
  const keyword = document.getElementById("faqSearchInput").value.toLowerCase();
  const faqContainer = document.getElementById("faqAccordion");
  const faqItems = faqContainer.querySelectorAll(".accordion-item");

  let matchedCategories = new Set();
  let hasResult = false;

  faqItems.forEach(item => {
    const question = item.querySelector(".accordion-button").innerText.toLowerCase();
    const answer = item.querySelector(".accordion-body").innerText.toLowerCase();
    const category = item.getAttribute("data-category");

    if (question.includes(keyword) || answer.includes(keyword)) {
      item.style.display = "block";
      matchedCategories.add(category);
      hasResult = true;
    } else {
      item.style.display = "none";
    }
  });

  // FAQ 섹션을 표시/숨김
  faqContainer.style.display = hasResult ? "block" : "none";

  // 카테고리 탭 처리
  if (matchedCategories.size === 1) {
    const onlyCategory = Array.from(matchedCategories)[0];
    document.querySelectorAll(".category-tab").forEach(tab => {
      const tabCategory = tab.getAttribute("data-category");
      tab.classList.toggle("active", tabCategory === onlyCategory);
    });
  } else {
    document.querySelectorAll(".category-tab").forEach(tab => {
      tab.classList.remove("active");
    });
  }
}

// 카테고리 클릭 이벤트
document.querySelectorAll(".category-tab").forEach(tab => {
  tab.addEventListener("click", function () {
    const selectedCategory = this.getAttribute("data-category");
    const faqContainer = document.getElementById("faqAccordion");
    const items = faqContainer.querySelectorAll(".accordion-item");

    items.forEach(item => {
      const itemCategory = item.getAttribute("data-category");
      item.style.display =
        selectedCategory === "ALL" || itemCategory === selectedCategory
          ? "block"
          : "none";
    });

    // active 탭 표시
    document.querySelectorAll(".category-tab").forEach(t => t.classList.remove("active"));
    this.classList.add("active");

    faqContainer.style.display = "block";
  });
});
