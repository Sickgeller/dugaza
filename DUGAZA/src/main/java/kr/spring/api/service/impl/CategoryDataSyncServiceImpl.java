package kr.spring.api.service.impl;

import com.project.dugaza.api.client.CategoryApiClient;
import com.project.dugaza.api.dto.CategoryCodeApiDto;
import com.project.dugaza.api.mapper.CategoryCodeMapper;
import com.project.dugaza.api.service.CategoryDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDataSyncServiceImpl implements CategoryDataSyncService {

    private final CategoryApiClient categoryApiClient;
    private final CategoryCodeMapper categoryCodeMapper;

    @Override
    @Transactional
    public int syncAllCategoryCodes() {
        log.info("모든 카테고리 코드 동기화 시작");
        
        AtomicInteger totalCount = new AtomicInteger(0);
        
        // 대분류 동기화
        int cat1Count = syncCategoryCode1();
        totalCount.addAndGet(cat1Count);
        
        // 중분류 동기화
        int cat2Count = syncCategoryCode2();
        totalCount.addAndGet(cat2Count);
        
        // 소분류 동기화
        int cat3Count = syncCategoryCode3();
        totalCount.addAndGet(cat3Count);
        
        log.info("모든 카테고리 코드 동기화 완료. 총 {} 항목 동기화됨", totalCount.get());
        return totalCount.get();
    }

    @Override
    @Transactional
    public int syncCategoryCode1() {
        log.info("대분류 카테고리 코드 동기화 시작");
        
        List<CategoryCodeApiDto> cat1List = categoryApiClient.getCategoryCode1();
        AtomicInteger count = new AtomicInteger(0);
        
        if (cat1List != null && !cat1List.isEmpty()) {
            cat1List.forEach(dto -> {
                try {
                    categoryCodeMapper.insertCategoryCode(dto);
                    count.incrementAndGet();
                } catch (Exception e) {
                    log.error("대분류 카테고리 코드 저장 중 오류 발생: {}", e.getMessage(), e);
                }
            });
        }
        
        log.info("대분류 카테고리 코드 동기화 완료. {} 항목 동기화됨", count.get());
        return count.get();
    }

    @Override
    @Transactional
    public int syncCategoryCode2() {
        log.info("중분류 카테고리 코드 동기화 시작");
        AtomicInteger count = new AtomicInteger(0);
        
        // 먼저 대분류 코드 목록을 가져옴
        List<CategoryCodeApiDto> cat1List = categoryCodeMapper.findByCodeLevel(1);
        
        if (cat1List != null && !cat1List.isEmpty()) {
            cat1List.forEach(cat1 -> {
                try {
                    // 각 대분류 코드에 대한 중분류 코드 조회 및 저장
                    List<CategoryCodeApiDto> cat2List = categoryApiClient.getCategoryCode2(cat1.getCategoryCode());
                    
                    if (cat2List != null && !cat2List.isEmpty()) {
                        cat2List.forEach(dto -> {
                            try {
                                categoryCodeMapper.insertCategoryCode(dto);
                                count.incrementAndGet();
                            } catch (Exception e) {
                                log.error("중분류 카테고리 코드 저장 중 오류 발생: {}", e.getMessage(), e);
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("대분류 [{}]에 대한 중분류 카테고리 코드 조회 중 오류 발생: {}", 
                            cat1.getCategoryCode(), e.getMessage(), e);
                }
            });
        }
        
        log.info("중분류 카테고리 코드 동기화 완료. {} 항목 동기화됨", count.get());
        return count.get();
    }

    @Override
    @Transactional
    public int syncCategoryCode3() {
        log.info("소분류 카테고리 코드 동기화 시작");
        AtomicInteger count = new AtomicInteger(0);
        
        // 먼저 중분류 코드 목록을 가져옴
        List<CategoryCodeApiDto> cat2List = categoryCodeMapper.findByCodeLevel(2);
        
        if (cat2List != null && !cat2List.isEmpty()) {
            cat2List.forEach(cat2 -> {
                try {
                    // 각 중분류 코드의 부모 코드(대분류) 조회
                    String cat1Code = cat2.getParentCode();
                    
                    // 각 중분류 코드에 대한 소분류 코드 조회 및 저장
                    List<CategoryCodeApiDto> cat3List = categoryApiClient.getCategoryCode3(cat1Code, cat2.getCategoryCode());
                    
                    if (cat3List != null && !cat3List.isEmpty()) {
                        cat3List.forEach(dto -> {
                            try {
                                categoryCodeMapper.insertCategoryCode(dto);
                                count.incrementAndGet();
                            } catch (Exception e) {
                                log.error("소분류 카테고리 코드 저장 중 오류 발생: {}", e.getMessage(), e);
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("중분류 [{}]에 대한 소분류 카테고리 코드 조회 중 오류 발생: {}", 
                            cat2.getCategoryCode(), e.getMessage(), e);
                }
            });
        }
        
        log.info("소분류 카테고리 코드 동기화 완료. {} 항목 동기화됨", count.get());
        return count.get();
    }
} 