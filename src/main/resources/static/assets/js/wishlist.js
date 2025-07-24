document.addEventListener('DOMContentLoaded', function () {
    // CSRF 토큰과 헤더 이름을 메타 태그에서 읽어옵니다.
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    document.querySelectorAll('.like-btn, .small-like-btn').forEach(button => {
        button.addEventListener('click', function (event) { // event 파라미터 추가
            event.preventDefault(); // 버튼의 기본 동작(예: form 제출) 방지
            event.stopPropagation(); // 이벤트가 상위 엘리먼트로 전파되는 것을 중단

            const isLogin = document.body.dataset.isLogin;

            // 로그인 상태가 'true'가 아니면 알림을 띄우고 확실히 중단합니다.
            if (isLogin !== 'true') {
                alert('로그인이 필요한 서비스입니다.'); // 메시지를 살짝 변경하여 적용 여부 확인
                return;
            }

            const contentId = button.getAttribute('data-content-id');
            const contentType = button.getAttribute('data-content-type');

            // fetch 요청 헤더에 CSRF 토큰을 추가합니다.
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            fetch('/wishlist/toggle', {
                method: 'POST',
                headers: headers,
                body: `contentId=${contentId}&contentTypeId=${contentType}`
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('찜 상태 변경에 실패했습니다.');
                }
                return response.json();
            }) 
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
                alert(error.message || "찜하기 처리 중 오류가 발생했습니다.");
            });
        });
    });
});
