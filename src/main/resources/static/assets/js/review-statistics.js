// 리뷰 통계 관련 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // 기간 선택 버튼 이벤트
    const periodButtons = document.querySelectorAll('.period-selector button');
    periodButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 활성 버튼 변경
            periodButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // 여기에 기간별 통계 조회 로직 추가
            const period = this.textContent;
            console.log('선택된 기간:', period);
            // loadStatisticsByPeriod(period);
        });
    });
    
    // 별점별 분포 차트 애니메이션
    animateRatingBars();
    
    // 검색 및 필터 기능
    initializeSearchAndFilter();
});

// 별점별 분포 차트 애니메이션
function animateRatingBars() {
    const bars = document.querySelectorAll('.bar-fill');
    bars.forEach(bar => {
        const width = bar.style.width;
        bar.style.width = '0%';
        
        setTimeout(() => {
            bar.style.width = width;
        }, 100);
    });
}

// 검색 및 필터 기능 초기화
function initializeSearchAndFilter() {
    const searchInput = document.querySelector('.search-box input');
    const filterSelects = document.querySelectorAll('.filter-options select');
    const dateInputs = document.querySelectorAll('.filter-options input[type="date"]');
    
    // 검색 기능
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filterReviews(searchTerm);
        });
    }
    
    // 필터 기능
    filterSelects.forEach(select => {
        select.addEventListener('change', function() {
            applyFilters();
        });
    });
    
    // 날짜 필터
    dateInputs.forEach(input => {
        input.addEventListener('change', function() {
            applyFilters();
        });
    });
}

// 리뷰 검색 필터링
function filterReviews(searchTerm) {
    const reviewCards = document.querySelectorAll('.review-card');
    
    reviewCards.forEach(card => {
        const content = card.textContent.toLowerCase();
        const isVisible = content.includes(searchTerm);
        card.style.display = isVisible ? 'block' : 'none';
    });
}

// 필터 적용
function applyFilters() {
    const ratingFilter = document.querySelector('.filter-options select:first-child').value;
    const statusFilter = document.querySelector('.filter-options select:nth-child(2)').value;
    const roomFilter = document.querySelector('.filter-options select:nth-child(3)').value;
    const startDate = document.querySelector('.filter-options input[type="date"]:first-of-type').value;
    const endDate = document.querySelector('.filter-options input[type="date"]:last-of-type').value;
    
    console.log('필터 적용:', {
        rating: ratingFilter,
        status: statusFilter,
        room: roomFilter,
        startDate: startDate,
        endDate: endDate
    });
    
    // 여기에 실제 필터링 로직 구현
    // filterReviewsByCriteria(ratingFilter, statusFilter, roomFilter, startDate, endDate);
}

// 통계 새로고침
function refreshStatistics() {
    // 로딩 표시
    showLoading();
    
    // 통계 데이터 새로고침 API 호출
    fetch('/api/review/statistics/refresh', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // CSRF 토큰 추가 필요
        }
    })
    .then(response => response.json())
    .then(data => {
        console.log('통계 새로고침 완료:', data);
        hideLoading();
        // 페이지 새로고침 또는 데이터 업데이트
        location.reload();
    })
    .catch(error => {
        console.error('통계 새로고침 실패:', error);
        hideLoading();
        showError('통계 새로고침에 실패했습니다.');
    });
}

// 로딩 표시
function showLoading() {
    const loadingDiv = document.createElement('div');
    loadingDiv.className = 'loading-overlay';
    loadingDiv.innerHTML = `
        <div class="loading-spinner">
            <i class="fas fa-spinner fa-spin"></i>
            <p>통계를 새로고침하고 있습니다...</p>
        </div>
    `;
    document.body.appendChild(loadingDiv);
}

// 로딩 숨김
function hideLoading() {
    const loadingDiv = document.querySelector('.loading-overlay');
    if (loadingDiv) {
        loadingDiv.remove();
    }
}

// 에러 메시지 표시
function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.innerHTML = `
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-triangle"></i>
            <span>${message}</span>
            <button type="button" class="close" onclick="this.parentElement.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    // 페이지 상단에 에러 메시지 추가
    const container = document.querySelector('.main-content');
    container.insertBefore(errorDiv, container.firstChild);
    
    // 5초 후 자동 제거
    setTimeout(() => {
        if (errorDiv.parentElement) {
            errorDiv.remove();
        }
    }, 5000);
}

// 리뷰 답변 모달
function openReplyModal(reviewId) {
    // 답변 모달 구현
    console.log('답변 모달 열기:', reviewId);
    // 여기에 모달 구현
}

// 리뷰 신고 모달
function openReportModal(reviewId) {
    // 신고 모달 구현
    console.log('신고 모달 열기:', reviewId);
    // 여기에 모달 구현
}

// 리뷰 삭제 확인
function deleteReview(reviewId) {
    if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
        console.log('리뷰 삭제:', reviewId);
        // 여기에 삭제 API 호출
    }
}

// CSS 스타일 추가
const style = document.createElement('style');
style.textContent = `
    .loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    }
    
    .loading-spinner {
        background: white;
        padding: 2rem;
        border-radius: 8px;
        text-align: center;
    }
    
    .loading-spinner i {
        font-size: 2rem;
        color: #007bff;
        margin-bottom: 1rem;
    }
    
    .error-message {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        max-width: 400px;
    }
    
    .alert {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 1rem;
        border-radius: 4px;
        margin-bottom: 1rem;
    }
    
    .alert-danger {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
    
    .alert .close {
        background: none;
        border: none;
        color: inherit;
        cursor: pointer;
        margin-left: auto;
    }
`;
document.head.appendChild(style); 