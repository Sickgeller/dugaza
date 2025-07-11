document.querySelector('.like-btn').addEventListener('click', function () {
    const button = this;
    const contentId = button.getAttribute('data-content-id');
    const contentType = button.getAttribute('data-content-type');

    fetch('/wish/toggle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            /*'X-CSRF-TOKEN': [[${_csrf.token}]] '' // Spring Security 사용할 경우*/
        },
        body: JSON.stringify({
            contentId: contentId,
            contentType: contentType
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.liked) {
            button.classList.add('liked');
            button.innerHTML = '<i class="fas fa-heart"></i> 찜 취소';
        } else {
            button.classList.remove('liked');
            button.innerHTML = '<i class="fas fa-heart"></i> 찜하기';
        }
    })
    .catch(error => {
        console.error('찜하기 실패:', error);
        alert("찜하기 처리 중 오류가 발생했습니다.");
    });
});