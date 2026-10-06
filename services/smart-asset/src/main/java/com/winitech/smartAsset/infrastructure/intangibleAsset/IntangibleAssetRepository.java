package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.UUID;

/**
 * keyword/expiryBefore 둘 다 선택 필터라, 예전에는 {@code (:param IS NULL OR ...)} 한 개의
 * JPQL로 처리했다. 그런데 PostgreSQL은 파라미터가 오직 {@code IS NULL} 위치에서만 쓰이면(다른
 * 곳에서 타입을 유추할 문맥이 없으면) prepared statement 파싱 단계에서 타입을 정하지 못해
 * {@code could not determine data type of parameter} (SQLState 42P18)로 실패한다
 * (Hibernate 5 + pgjdbc 조합에서 재현 - AssetHistoryRepository.findRecentByFilter의 주석과
 * 동일한 근본 원인). "항상 매칭되는 안전한 기본값"으로 대체하는 방법 대신, 조건 조합별로 쿼리를
 * 4개로 나눠 필터가 없을 때는 아예 그 Predicate 자체가 쿼리에 나타나지 않도록 한다 - 선택 필터가
 * 2개뿐이라 조합 폭발 없이 감당 가능하고, null 바인딩에 다시 의존할 여지 자체가 없어진다.
 */
public interface IntangibleAssetRepository extends JpaRepository<IntangibleAsset, UUID> {

    @Query(
            value = "select a from IntangibleAsset a where a.status = 'ENABLE'",
            countQuery = "select count(a) from IntangibleAsset a where a.status = 'ENABLE'"
    )
    Page<IntangibleAsset> findAllEnabled(Pageable pageable);

    @Query(
            value = "select a from IntangibleAsset a where a.status = 'ENABLE' and a.name like %:keyword%",
            countQuery = "select count(a) from IntangibleAsset a where a.status = 'ENABLE' and a.name like %:keyword%"
    )
    Page<IntangibleAsset> findAllEnabledByKeyword(String keyword, Pageable pageable);

    @Query(
            value = "select a from IntangibleAsset a where a.status = 'ENABLE' and a.expiryDate <= :expiryBefore",
            countQuery = "select count(a) from IntangibleAsset a where a.status = 'ENABLE' and a.expiryDate <= :expiryBefore"
    )
    Page<IntangibleAsset> findAllEnabledByExpiryBefore(LocalDate expiryBefore, Pageable pageable);

    @Query(
            value = "select a from IntangibleAsset a where a.status = 'ENABLE' " +
                    "and a.name like %:keyword% and a.expiryDate <= :expiryBefore",
            countQuery = "select count(a) from IntangibleAsset a where a.status = 'ENABLE' " +
                    "and a.name like %:keyword% and a.expiryDate <= :expiryBefore"
    )
    Page<IntangibleAsset> findAllEnabledByKeywordAndExpiryBefore(
            String keyword, LocalDate expiryBefore, Pageable pageable);
}
