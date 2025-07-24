package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.CategoryApiClient;
import kr.spring.api.dto.CategoryCodeApiDto;
import kr.spring.api.mapper.CategoryCodeMapper;
import kr.spring.api.service.CategoryDataSyncService;
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
    @LogExecutionTime(category = "CategorySync")
    public int syncAllCategoryCodes() {
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
        
        return totalCount.get();
    }

    @Override
    @Transactional
    @LogExecutionTime(category = "CategorySync")
    public int syncCategoryCode1() {
        List<CategoryCodeApiDto> cat1List = categoryApiClient.getCategoryCode1();
        AtomicInteger count = new AtomicInteger(0);
        
        if (cat1List != null && !cat1List.isEmpty()) {
            cat1List.forEach(dto -> {
                try {
                    // 이미 존재하는지 확인
                    CategoryCodeApiDto existing = categoryCodeMapper.findByCategoryCode(dto.getCategoryCode());
                    
                    if (existing == null) {
                        // 새로운 카테고리 코드 추가
                        dto.setIsActive(1L); // 활성화 상태로 설정
                        categoryCodeMapper.insertCategoryCode(dto);
                        count.incrementAndGet();
                    } else {
                        // 기존 카테고리 코드 업데이트
                        existing.setCategoryName(dto.getCategoryName());
                        // isActive 상태 유지
                        categoryCodeMapper.updateCategoryCode(existing);
                        count.incrementAndGet();
                    }
                } catch (Exception e) {
                    // AOP에서 예외 처리
                }
            });
        }
        
        return count.get();
    }

    @Override
    @Transactional
    @LogExecutionTime(category = "CategorySync")
    public int syncCategoryCode2() {
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
                                // 이미 존재하는지 확인
                                CategoryCodeApiDto existing = categoryCodeMapper.findByCategoryCode(dto.getCategoryCode());
                                
                                if (existing == null) {
                                    // 새로운 카테고리 코드 추가
                                    dto.setIsActive(1L); // 활성화 상태로 설정
                                    categoryCodeMapper.insertCategoryCode(dto);
                                    count.incrementAndGet();
                                } else {
                                    // 기존 카테고리 코드 업데이트
                                    existing.setCategoryName(dto.getCategoryName());
                                    // isActive 상태 유지
                                    categoryCodeMapper.updateCategoryCode(existing);
                                    count.incrementAndGet();
                                }
                            } catch (Exception e) {
                                // AOP에서 예외 처리
                            }
                        });
                    }
                } catch (Exception e) {
                    // AOP에서 예외 처리
                }
            });
        }
        
        return count.get();
    }

    @Override
    @Transactional
    @LogExecutionTime(category = "CategorySync")
    public int syncCategoryCode3() {
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
                                // 이미 존재하는지 확인
                                CategoryCodeApiDto existing = categoryCodeMapper.findByCategoryCode(dto.getCategoryCode());
                                
                                if (existing == null) {
                                    // 새로운 카테고리 코드 추가
                                    dto.setIsActive(1L); // 활성화 상태로 설정
                                    categoryCodeMapper.insertCategoryCode(dto);
                                    count.incrementAndGet();
                                } else {
                                    // 기존 카테고리 코드 업데이트
                                    existing.setCategoryName(dto.getCategoryName());
                                    // isActive 상태 유지
                                    categoryCodeMapper.updateCategoryCode(existing);
                                    count.incrementAndGet();
                                }
                            } catch (Exception e) {
                                // AOP에서 예외 처리
                            }
                        });
                    }
                } catch (Exception e) {
                    // AOP에서 예외 처리
                }
            });
        }
        
        return count.get();
    }
} 