// 인증 관련 기능을 담당하는 클래스
class Auth {
    // 사용자 정보를 세션 스토리지에 저장
    static setUserInfo(userInfo) {
        sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
    }

    // 저장된 사용자 정보 가져오기
    static getUserInfo() {
        const userInfo = sessionStorage.getItem('userInfo');
        return userInfo ? JSON.parse(userInfo) : null;
    }

    // 로그인 상태 확인
    static isLoggedIn() {
        return !!this.getUserInfo();
    }

    // 사용자 정보 삭제 (로그아웃 시 사용)
    static clearUserInfo() {
        sessionStorage.removeItem('userInfo');
    }

    // 로그인 처리
    static async login(id, password) {
        try {
            const response = await fetch('/auth/member-login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id, password })
            });
            
            const data = await response.json();
            
            if (data.status === 'success') {
                // 로그인 성공 시 사용자 정보 저장
                this.setUserInfo({
                    memberId: data.memberId,
                    name: data.name
                });
                return {
                    success: true,
                    message: '로그인 성공'
                };
            }
            return {
                success: false,
                message: data.message || '로그인 실패'
            };
        } catch (error) {
            console.error('로그인 중 오류:', error);
            return {
                success: false,
                message: '로그인 처리 중 오류가 발생했습니다.'
            };
        }
    }

    // 로그아웃 처리
    static async logout() {
        try {
            const response = await fetch('/member/logout', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                this.clearUserInfo();
                return {
                    success: true,
                    message: '로그아웃 되었습니다.'
                };
            }
            return {
                success: false,
                message: '로그아웃 처리 중 오류가 발생했습니다.'
            };
        } catch (error) {
            console.error('로그아웃 중 오류:', error);
            return {
                success: false,
                message: '로그아웃 처리 중 오류가 발생했습니다.'
            };
        }
    }

  /*  // UI 업데이트
    static updateUI() {
        const userInfo = this.getUserInfo();
        
        if (userInfo) {
            // 로그인 상태 UI
            document.querySelectorAll('.logged-in').forEach(el => el.style.display = 'flex');
            document.querySelectorAll('.logged-out').forEach(el => el.style.display = 'none');
            
            // 사용자 이름 표시
            const userNameElements = document.querySelectorAll('.user-name');
            userNameElements.forEach(el => el.textContent = userInfo.name);
        } else {
            // 로그아웃 상태 UI
            document.querySelectorAll('.logged-in').forEach(el => el.style.display = 'none');
            document.querySelectorAll('.logged-out').forEach(el => el.style.display = 'flex');
        }
        
        // 사업자 로그인 버튼은 항상 표시
        document.querySelectorAll('.btn-outline-secondary').forEach(el => {
            if (el.textContent.includes('사업자 로그인')) {
                el.style.display = 'inline-block';
            }
        });
    }
}

// 페이지 로드 시 UI 업데이트
document.addEventListener('DOMContentLoaded', () => {
    Auth.updateUI();
}); 
*/