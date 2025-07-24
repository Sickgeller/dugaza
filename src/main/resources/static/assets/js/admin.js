// 관리자 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    
    // 드롭다운 메뉴 토글
    const dropdownToggles = document.querySelectorAll('.dropdown-toggle');
    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', function(e) {
            e.preventDefault();
            const submenu = this.nextElementSibling;
            if (submenu && submenu.classList.contains('submenu')) {
                submenu.classList.toggle('active');
                this.classList.toggle('active');
            }
        });
    });

    // 사이드바 토글
    const menuToggle = document.querySelector('.menu-toggle');
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
        });
    }

    // 로그아웃 버튼
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm('로그아웃하시겠습니까?')) {
                window.location.href = '/logout';
            }
        });
    }

    // 상태 변경 버튼들
    const statusButtons = document.querySelectorAll('.btn-icon');
    statusButtons.forEach(button => {
        button.addEventListener('click', function() {
            const action = this.getAttribute('title');
            const row = this.closest('tr');
            
            if (action === '삭제') {
                if (confirm('정말 삭제하시겠습니까?')) {
                    // 삭제 로직
                    console.log('삭제 실행');
                }
            } else if (action === '수정') {
                // 수정 로직
                console.log('수정 실행');
            } else if (action === '상세정보') {
                // 상세정보 로직
                console.log('상세정보 보기');
            }
        });
    });

    // 검색 기능
    const searchBoxes = document.querySelectorAll('.search-box input');
    searchBoxes.forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const searchBtn = this.parentElement.querySelector('button');
                if (searchBtn) {
                    searchBtn.click();
                }
            }
        });
    });

    // 필터 변경 시 자동 적용
    const filterSelects = document.querySelectorAll('.filter-options select');
    filterSelects.forEach(select => {
        select.addEventListener('change', function() {
            // 필터 적용 로직
            console.log('필터 변경:', this.value);
        });
    });

    // 페이지네이션
    const pageButtons = document.querySelectorAll('.btn-page');
    pageButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (!this.classList.contains('active')) {
                // 페이지 변경 로직
                console.log('페이지 변경');
            }
        });
    });

    // 빠른 액션 카드들
    const actionCards = document.querySelectorAll('.action-card');
    actionCards.forEach(card => {
        card.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href) {
                window.location.href = href;
            }
        });
    });

    // 테이블 행 클릭 이벤트
    const tableRows = document.querySelectorAll('tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('click', function(e) {
            // 행 클릭 시 상세정보 표시
            if (!e.target.closest('.btn-icon')) {
                console.log('행 클릭:', this);
            }
        });
    });

    // 모달 닫기
    const closeButtons = document.querySelectorAll('.close-modal, .modal-overlay');
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const modals = document.querySelectorAll('.modal');
            modals.forEach(modal => {
                if (modal.style.display === 'block') {
                    modal.style.display = 'none';
                }
            });
        }
    });

    // 알림 메시지 자동 숨김
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.remove();
            }, 300);
        }, 3000);
    });

    // 차트 초기화 (Chart.js가 있는 경우)
    if (typeof Chart !== 'undefined') {
        const chartCanvases = document.querySelectorAll('canvas');
        chartCanvases.forEach(canvas => {
            const ctx = canvas.getContext('2d');
            if (canvas.id === 'salesChart') {
                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
                        datasets: [{
                            label: '매출',
                            data: [12, 19, 3, 5, 2, 3],
                            borderColor: 'rgb(75, 192, 192)',
                            tension: 0.1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }
        });
    }
});

// 유틸리티 함수들
const AdminUtils = {
    // 날짜 포맷팅
    formatDate: function(date) {
        return new Date(date).toLocaleDateString('ko-KR');
    },
    
    // 숫자 포맷팅
    formatNumber: function(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
    
    // 상태 클래스 반환
    getStatusClass: function(status) {
        const statusMap = {
            'active': 'available',
            'pending': 'pending',
            'suspended': 'suspended',
            'deleted': 'deleted'
        };
        return statusMap[status] || 'default';
    },
    
    // AJAX 요청
    ajax: function(url, method = 'GET', data = null) {
        return fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: data ? JSON.stringify(data) : null
        }).then(response => response.json());
    },
    
    // 알림 표시
    showNotification: function(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }
};