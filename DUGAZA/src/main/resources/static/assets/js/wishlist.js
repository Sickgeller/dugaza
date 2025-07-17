document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.like-btn, .small-like-btn').forEach(button => {
        button.addEventListener('click', function () {
            const contentId = button.getAttribute('data-content-id');
            const contentType = button.getAttribute('data-content-type');

            console.log("찜 버튼 클릭됨", contentId, contentType); // 디버깅용

            fetch('/wishlist/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `contentId=${contentId}&contentTypeId=${contentType}`
            })
            .then(response => response.json()) 
			.then(data => {
			    const icon = button.querySelector('i');

				if (data.wished) {
                   // 찜된 상태
                   icon.classList.remove('far');
                   icon.classList.add('fas');
                   icon.style.color = 'red';
               } else {
                   // 찜되지 않은 상태
                   icon.classList.remove('fas');
                   icon.classList.add('far');
                   icon.style.color = '';
               }
			})
            .catch(error => {
                console.error('찜하기 실패:', error);
                alert("찜하기 처리 중 오류가 발생했습니다.");
            });
        });
    });
});
