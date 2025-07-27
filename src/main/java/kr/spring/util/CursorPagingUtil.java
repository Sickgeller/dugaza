package kr.spring.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 커서 기반 페이지네이션 유틸리티
 * - OFFSET/LIMIT 방식 대신 커서를 사용하여 성능 최적화
 * - 대용량 데이터에서 안정적인 페이지네이션 제공
 */
@Getter
@Setter
public class CursorPagingUtil {
    
    private Long cursor;           // 현재 커서 (마지막으로 조회한 ID)
    private int pageSize;          // 페이지 크기
    private boolean hasNext;       // 다음 페이지 존재 여부
    private String keyword;        // 검색 키워드
    private String category;       // 카테고리
    private String sort;           // 정렬 기준
    private String status;         // 상태 필터
    
    public CursorPagingUtil() {
        this.pageSize = 10; // 기본 페이지 크기
    }
    
    public CursorPagingUtil(Long cursor, int pageSize) {
        this.cursor = cursor;
        this.pageSize = pageSize;
    }
    
    public CursorPagingUtil(Long cursor, int pageSize, String keyword) {
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.keyword = keyword;
    }
    
    public CursorPagingUtil(Long cursor, int pageSize, String keyword, String category) {
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.keyword = keyword;
        this.category = category;
    }
    
    public CursorPagingUtil(Long cursor, int pageSize, String keyword, String category, String sort) {
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.keyword = keyword;
        this.category = category;
        this.sort = sort;
    }
    
    /**
     * 다음 페이지 URL 생성
     */
    public String getNextPageUrl(String baseUrl, Long nextCursor) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?cursor=").append(nextCursor);
        url.append("&pageSize=").append(pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }
        if (category != null && !category.isEmpty()) {
            url.append("&category=").append(category);
        }
        if (sort != null && !sort.isEmpty()) {
            url.append("&sort=").append(sort);
        }
        if (status != null && !status.isEmpty()) {
            url.append("&status=").append(status);
        }
        
        return url.toString();
    }
    
    /**
     * 이전 페이지 URL 생성 (커서 기반에서는 제한적)
     */
    public String getPrevPageUrl(String baseUrl, Long prevCursor) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?cursor=").append(prevCursor);
        url.append("&pageSize=").append(pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }
        if (category != null && !category.isEmpty()) {
            url.append("&category=").append(category);
        }
        if (sort != null && !sort.isEmpty()) {
            url.append("&sort=").append(sort);
        }
        if (status != null && !status.isEmpty()) {
            url.append("&status=").append(status);
        }
        
        return url.toString();
    }
    
    /**
     * 첫 페이지 URL 생성
     */
    public String getFirstPageUrl(String baseUrl) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?pageSize=").append(pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            url.append("&keyword=").append(keyword);
        }
        if (category != null && !category.isEmpty()) {
            url.append("&category=").append(category);
        }
        if (sort != null && !sort.isEmpty()) {
            url.append("&sort=").append(sort);
        }
        if (status != null && !status.isEmpty()) {
            url.append("&status=").append(status);
        }
        
        return url.toString();
    }
    
    /**
     * 페이지네이션 HTML 생성 (간단한 이전/다음 버튼)
     */
    public String generatePaginationHtml(String baseUrl, Long nextCursor, Long prevCursor) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='pagination'>");
        
        // 이전 버튼
        if (prevCursor != null) {
            html.append("<a href='").append(getPrevPageUrl(baseUrl, prevCursor)).append("' class='btn btn-secondary'>이전</a>");
        } else {
            html.append("<span class='btn btn-secondary disabled'>이전</span>");
        }
        
        // 다음 버튼
        if (hasNext && nextCursor != null) {
            html.append("<a href='").append(getNextPageUrl(baseUrl, nextCursor)).append("' class='btn btn-primary'>다음</a>");
        } else {
            html.append("<span class='btn btn-primary disabled'>다음</span>");
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    /**
     * 검색 조건이 있는지 확인
     */
    public boolean hasSearchConditions() {
        return (keyword != null && !keyword.isEmpty()) ||
               (category != null && !category.isEmpty()) ||
               (sort != null && !sort.isEmpty()) ||
               (status != null && !status.isEmpty());
    }
    
    /**
     * 현재 페이지 정보를 Map으로 반환
     */
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("cursor", cursor);
        map.put("pageSize", pageSize);
        map.put("hasNext", hasNext);
        map.put("keyword", keyword);
        map.put("category", category);
        map.put("sort", sort);
        map.put("status", status);
        return map;
    }
} 