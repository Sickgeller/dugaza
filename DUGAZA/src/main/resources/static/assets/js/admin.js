// 관리자 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 기능
    const menuToggle = document.querySelector('.menu-toggle');
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');

    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            mainContent.classList.toggle('sidebar-hidden');
        });
    }

    // 탭 버튼 기능
    const tabButtons = document.querySelectorAll('.tab-buttons button');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 같은 탭 그룹 내의 모든 버튼에서 active 클래스 제거
            const tabGroup = this.closest('.tab-buttons');
            tabGroup.querySelectorAll('button').forEach(btn => {
                btn.classList.remove('active');
            });
            // 클릭된 버튼에 active 클래스 추가
            this.classList.add('active');
        });
    });

    // 기간 선택 버튼 기능
    const periodButtons = document.querySelectorAll('.period-selector button');
    
    periodButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 같은 기간 선택기 내의 모든 버튼에서 active 클래스 제거
            const periodGroup = this.closest('.period-selector');
            periodGroup.querySelectorAll('button').forEach(btn => {
                btn.classList.remove('active');
            });
            // 클릭된 버튼에 active 클래스 추가
            this.classList.add('active');
        });
    });

    // 실시간 데이터 업데이트 시뮬레이션
    function updateRealtimeData() {
        const realtimeNumbers = document.querySelectorAll('.realtime-number');
        
        realtimeNumbers.forEach(number => {
            // 현재 값을 가져와서 숫자만 추출
            let currentValue = parseInt(number.textContent.replace(/[^0-9]/g, ''));
            // -5에서 +5 사이의 랜덤한 값을 더함
            let change = Math.floor(Math.random() * 11) - 5;
            let newValue = Math.max(0, currentValue + change);
            
            // 값의 형식에 따라 적절한 단위를 붙여서 업데이트
            if (number.textContent.includes('명')) {
                number.textContent = newValue + '명';
            } else if (number.textContent.includes('건')) {
                number.textContent = newValue + '건';
            } else if (number.textContent.includes('객실')) {
                number.textContent = newValue + '객실';
            } else {
                number.textContent = newValue;
            }
        });

        // 업데이트 시간 표시 갱신
        const refreshTimes = document.querySelectorAll('.refresh-time span');
        refreshTimes.forEach(time => {
            time.textContent = '마지막 업데이트: 방금 전';
        });
    }

    // 5분(300초)마다 실시간 데이터 업데이트
    setInterval(updateRealtimeData, 300000);

    // 알림 카드 클릭 이벤트
    const alertCards = document.querySelectorAll('.alert-card');
    
    alertCards.forEach(card => {
        card.addEventListener('click', function() {
            // 알림 상세 정보를 표시하는 모달이나 다른 UI 요소를 여기에 구현
            console.log('알림 카드 클릭됨:', this.querySelector('h4').textContent);
        });
    });

    // 테이블 행 호버 효과
    const tableRows = document.querySelectorAll('tbody tr');
    
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f8f9fa';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });

    // 버튼 클릭 효과
    const buttons = document.querySelectorAll('button');
    
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // 버튼에 ripple 효과 추가
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');
            this.appendChild(ripple);
            
            // ripple 위치 계산
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            
            // ripple 제거
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });

    // 차트 플레이스홀더 클릭 이벤트
    const chartPlaceholders = document.querySelectorAll('.placeholder-chart');
    
    chartPlaceholders.forEach(placeholder => {
        placeholder.addEventListener('click', function() {
            // 차트 라이브러리 초기화 또는 데이터 로드 로직을 여기에 구현
            console.log('차트 영역 클릭됨');
        });
    });

    // 페이지 로드 시 초기 데이터 로드 시뮬레이션
    function initializeData() {
        console.log('페이지 데이터 초기화 완료');
        // 여기에 실제 데이터 로드 로직 구현
    }

    initializeData();

    // 반응형 사이드바
    function handleResize() {
        if (window.innerWidth <= 768) {
            sidebar.classList.remove('active');
            mainContent.classList.remove('sidebar-hidden');
        }
    }

    window.addEventListener('resize', handleResize);
    handleResize();
});

// 차트 기본 설정
Chart.defaults.font.family = "'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif";
Chart.defaults.font.size = 13;
Chart.defaults.animation = {
    duration: 0 // 애니메이션 비활성화
};

// 매출 추이 차트
const initSalesChart = () => {
    const salesChart = document.getElementById('salesChart');
    if (!salesChart) return;

    new Chart(salesChart.getContext('2d'), {
        type: 'line',
        data: {
            labels: ['1일', '2일', '3일', '4일', '5일', '6일', '7일'],
            datasets: [{
                label: '매출',
                data: [1200000, 1500000, 1800000, 1600000, 2000000, 1900000, 1700000],
                borderColor: '#4CAF50',
                tension: 0.1,
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: true,
                    callbacks: {
                        label: function(context) {
                            return context.raw.toLocaleString() + '원';
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return value.toLocaleString() + '원';
                        },
                        maxTicksLimit: 6
                    },
                    grid: {
                        color: '#e5e7eb'
                    }
                }
            }
        }
    });
};

// 차종별 매출 차트
const initCarTypeChart = () => {
    const carTypeChart = document.getElementById('carTypeChart');
    if (!carTypeChart) return;

    new Chart(carTypeChart.getContext('2d'), {
        type: 'bar',
        data: {
            labels: ['경차', '소형', '중형', '대형', 'SUV', '승합'],
            datasets: [{
                data: [1500000, 2500000, 3800000, 4200000, 5100000, 2800000],
                backgroundColor: [
                    '#4CAF50',
                    '#2196F3',
                    '#FFC107',
                    '#9C27B0',
                    '#FF5722',
                    '#607D8B'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: true,
                    callbacks: {
                        label: function(context) {
                            return context.raw.toLocaleString() + '원';
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return value.toLocaleString() + '원';
                        },
                        maxTicksLimit: 6
                    },
                    grid: {
                        color: '#e5e7eb'
                    }
                }
            }
        }
    });
};

// 차트 초기화
document.addEventListener('DOMContentLoaded', () => {
    initSalesChart();
    initCarTypeChart();
});

function initializeTableFeatures() {
    // 예약 관리 버튼들
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('view-btn')) {
            const row = e.target.closest('tr');
            const bookingId = row.cells[0].textContent;
            alert(`예약 상세보기: ${bookingId}`);
        }
        
        if (e.target.classList.contains('edit-btn')) {
            const row = e.target.closest('tr');
            const bookingId = row.cells[0].textContent;
            alert(`예약 수정: ${bookingId}`);
        }
        
        if (e.target.classList.contains('cancel-btn')) {
            const row = e.target.closest('tr');
            const bookingId = row.cells[0].textContent;
            if (confirm(`예약을 취소하시겠습니까? (${bookingId})`)) {
                // 취소 처리 로직
                alert('예약이 취소되었습니다.');
            }
        }
        
        if (e.target.classList.contains('block-btn')) {
            const row = e.target.closest('tr');
            const userName = row.cells[1].textContent;
            if (confirm(`${userName} 사용자를 차단하시겠습니까?`)) {
                // 차단 처리 로직
                alert('사용자가 차단되었습니다.');
            }
        }
        
        if (e.target.classList.contains('delete-btn')) {
            if (confirm('상품을 삭제하시겠습니까?')) {
                // 삭제 처리 로직
                e.target.closest('.product-card').remove();
                alert('상품이 삭제되었습니다.');
            }
        }
    });
}

function initializeFilters() {
    // 필터 적용 버튼
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const section = this.closest('.admin-section');
            const filterSelect = section.querySelector('.filter-select');
            const filterDate = section.querySelector('.filter-date');
            
            let message = '필터 적용: ';
            if (filterSelect) {
                message += `상태: ${filterSelect.value}`;
            }
            if (filterDate) {
                message += `기간: ${filterDate.value}`;
            }
            console.log(message);
        });
    });
}

// 테이블 정렬
document.addEventListener('DOMContentLoaded', function() {
    const tables = document.querySelectorAll('table');
    
    tables.forEach(table => {
        const headers = table.querySelectorAll('th');
        
        headers.forEach((header, index) => {
            if (header.classList.contains('sortable')) {
                header.addEventListener('click', () => {
                    const direction = header.classList.contains('asc') ? -1 : 1;
                    const rows = Array.from(table.querySelectorAll('tbody tr'));
                    
                    // 정렬 방향 표시 업데이트
                    headers.forEach(h => h.classList.remove('asc', 'desc'));
                    header.classList.toggle('asc', direction === 1);
                    header.classList.toggle('desc', direction === -1);
                    
                    // 데이터 정렬
                    const sortedRows = rows.sort((a, b) => {
                        const aValue = a.children[index].textContent.trim();
                        const bValue = b.children[index].textContent.trim();
                        
                        // 숫자 정렬
                        if (!isNaN(aValue) && !isNaN(bValue)) {
                            return (Number(aValue) - Number(bValue)) * direction;
                        }
                        
                        // 문자열 정렬
                        return aValue.localeCompare(bValue) * direction;
                    });
                    
                    // 정렬된 행 적용
                    const tbody = table.querySelector('tbody');
                    tbody.innerHTML = '';
                    sortedRows.forEach(row => tbody.appendChild(row));
                });
            }
        });
    });
});

// 검색 기능
document.addEventListener('DOMContentLoaded', function() {
    const searchInputs = document.querySelectorAll('.search-box input');
    
    searchInputs.forEach(input => {
        input.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const table = this.closest('.content-section').querySelector('table');
            const rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchTerm) ? '' : 'none';
            });
        });
    });
});

// 필터 기능
document.addEventListener('DOMContentLoaded', function() {
    const filterSelects = document.querySelectorAll('.filter-box select');
    
    filterSelects.forEach(select => {
        select.addEventListener('change', function() {
            const table = this.closest('.content-section').querySelector('table');
            const rows = table.querySelectorAll('tbody tr');
            const filters = Array.from(this.closest('.filter-box').querySelectorAll('select'))
                .map(select => ({
                    column: select.getAttribute('data-column'),
                    value: select.value
                }))
                .filter(filter => filter.value !== '');
            
            rows.forEach(row => {
                const visible = filters.every(filter => {
                    const cell = row.querySelector(`td:nth-child(${filter.column})`);
                    return cell.textContent.includes(filter.value);
                });
                row.style.display = visible ? '' : 'none';
            });
        });
    });
});

// 페이지네이션
document.addEventListener('DOMContentLoaded', function() {
    const itemsPerPage = 10;
    const tables = document.querySelectorAll('table');
    
    tables.forEach(table => {
        const rows = table.querySelectorAll('tbody tr');
        const pageCount = Math.ceil(rows.length / itemsPerPage);
        const pagination = table.closest('.content-section').querySelector('.pagination');
        
        if (pagination && pageCount > 1) {
            // 페이지네이션 버튼 생성
            const prevButton = document.createElement('button');
            prevButton.classList.add('btn-icon');
            prevButton.innerHTML = '<i class="fas fa-chevron-left"></i>';
            prevButton.disabled = true;
            pagination.appendChild(prevButton);
            
            for (let i = 1; i <= pageCount; i++) {
                const button = document.createElement('button');
                button.textContent = i;
                if (i === 1) button.classList.add('active');
                pagination.appendChild(button);
            }
            
            const nextButton = document.createElement('button');
            nextButton.classList.add('btn-icon');
            nextButton.innerHTML = '<i class="fas fa-chevron-right"></i>';
            pagination.appendChild(nextButton);
            
            // 페이지 변경 이벤트
            let currentPage = 1;
            
            function showPage(page) {
                rows.forEach((row, index) => {
                    row.style.display = 
                        index >= (page - 1) * itemsPerPage && 
                        index < page * itemsPerPage ? '' : 'none';
                });
                
                // 버튼 상태 업데이트
                pagination.querySelectorAll('button').forEach(button => {
                    if (!button.classList.contains('btn-icon')) {
                        button.classList.toggle('active', Number(button.textContent) === page);
                    }
                });
                
                prevButton.disabled = page === 1;
                nextButton.disabled = page === pageCount;
            }
            
            // 이벤트 리스너
            pagination.addEventListener('click', e => {
                if (e.target.tagName === 'BUTTON') {
                    if (e.target === prevButton && currentPage > 1) {
                        currentPage--;
                    } else if (e.target === nextButton && currentPage < pageCount) {
                        currentPage++;
                    } else if (!e.target.classList.contains('btn-icon')) {
                        currentPage = Number(e.target.textContent);
                    }
                    showPage(currentPage);
                }
            });
            
            // 초기 페이지 표시
            showPage(1);
        }
    });
});

// 알림 시스템
class NotificationSystem {
    constructor() {
        this.container = document.createElement('div');
        this.container.className = 'notification-container';
        document.body.appendChild(this.container);
    }

    show(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${this.getIcon(type)}"></i>
                <span>${message}</span>
            </div>
            <button class="notification-close">
                <i class="fas fa-times"></i>
            </button>
        `;

        this.container.appendChild(notification);

        // 닫기 버튼 이벤트
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });

        // 자동 제거
        setTimeout(() => {
            notification.remove();
        }, 5000);
    }

    getIcon(type) {
        switch (type) {
            case 'success': return 'fa-check-circle';
            case 'error': return 'fa-exclamation-circle';
            case 'warning': return 'fa-exclamation-triangle';
            default: return 'fa-info-circle';
        }
    }
}

// 전역 알림 시스템 인스턴스 생성
const notifications = new NotificationSystem();

// 차트 데이터 업데이트 (예시)
function updateCharts() {
    // Chart.js 또는 다른 차트 라이브러리를 사용하여 구현
    console.log('차트 업데이트');
}

// 실시간 데이터 업데이트 (예시)
function startRealtimeUpdates() {
    setInterval(() => {
        // API 호출 또는 WebSocket을 통한 실시간 데이터 업데이트
        console.log('실시간 데이터 업데이트');
    }, 30000); // 30초마다 업데이트
}

// 문서 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 차트 초기화
    updateCharts();
    
    // 실시간 업데이트 시작
    startRealtimeUpdates();
});