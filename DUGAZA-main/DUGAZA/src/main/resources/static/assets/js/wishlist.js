document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.like-btn, .small-like-btn').forEach(button => {
        button.addEventListener('click', function () {
            const contentId = button.getAttribute('data-content-id');
            const contentType = button.getAttribute('data-content-type');

            console.log("찜 버튼 클릭됨", contentId, contentType); // 디버깅용

            fetch('/wish/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    contentId: contentId,
                    contentType: contentType
                })
            })
            .then(response => response.json()) 
			.then(data => {
			    const icon = button.querySelector('i');
			    const textSpan = button.querySelector('span');

				if (data.liked) {
                   // 찜된 상태
                   icon.className = 'fas fa-heart text-danger'; // 꽉 찬 빨간 하트
                   textSpan.textContent = '찜 취소';
                   button.classList.add('liked');
               } else {
                   // 찜되지 않은 상태
                   icon.className = 'far fa-heart text-secondary'; // 빈 회색 하트
                   textSpan.textContent = '찜하기';
                   button.classList.remove('liked');
               }
			})
            .catch(error => {
                console.error('찜하기 실패:', error);
                alert("찜하기 처리 중 오류가 발생했습니다.");
            });
        });
    });
});
