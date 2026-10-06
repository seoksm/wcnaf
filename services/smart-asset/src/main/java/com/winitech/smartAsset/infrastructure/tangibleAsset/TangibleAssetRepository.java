package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TangibleAssetRepository extends JpaRepository<TangibleAsset, UUID> {

    /**
     * category/location은 지연 로딩(LAZY)이라 DTO 변환(TangibleAssetInfo) 중 그대로 접근하면
     * 행마다 추가 쿼리가 나가는 N+1이 생긴다 - @EntityGraph로 이 목록 조회 한 번에 함께 즉시 로딩한다.
     * 정렬은 이 쿼리에 고정하지 않고 호출자가 넘기는 Pageable의 Sort를 그대로 따른다
     * (Spring Data JPA가 JPQL 뒤에 자동으로 ORDER BY를 붙여준다).
     */
    @EntityGraph(attributePaths = {"category", "location"})
    @Query(
            value = "select t from TangibleAsset t " +
                    "where t.status = 'ENABLE' " +
                    "and (:keyword IS NULL " +
                    "     OR t.assetCode LIKE %:keyword% " +
                    "     OR t.assetName LIKE %:keyword% " +
                    "     OR t.serialNo LIKE %:keyword%)",
            countQuery = "select count(t) from TangibleAsset t " +
                    "where t.status = 'ENABLE' " +
                    "and (:keyword IS NULL " +
                    "     OR t.assetCode LIKE %:keyword% " +
                    "     OR t.assetName LIKE %:keyword% " +
                    "     OR t.serialNo LIKE %:keyword%)"
    )
    Page<TangibleAsset> findAllByContainsKeyword(String keyword, Pageable pageable);

    Optional<TangibleAsset> findByAssetCodeAndStatus(String assetCode, TangibleAsset.Status status);

    @Query("select count(t) from TangibleAsset t where t.status = 'ENABLE' and t.category.id = :categoryId")
    long countByCategoryId(UUID categoryId);

    @Query("select count(t) from TangibleAsset t where t.status = 'ENABLE' and t.location.id = :locationId")
    long countByLocationId(UUID locationId);

    /** S-240 불용자산 목록 - 생애상태로 필터링. category/location N+1 방지는 목록 조회와 동일하게 EntityGraph로 처리 */
    @EntityGraph(attributePaths = {"category", "location"})
    @Query(
            value = "select t from TangibleAsset t where t.status = 'ENABLE' and t.lifeStatus in :lifeStatuses",
            countQuery = "select count(t) from TangibleAsset t where t.status = 'ENABLE' and t.lifeStatus in :lifeStatuses"
    )
    Page<TangibleAsset> findByLifeStatusIn(Collection<TangibleAsset.LifeStatus> lifeStatuses, Pageable pageable);

    /** S-301/302 전수조사 대상 산정 - location은 InventoryTarget 스냅샷(expectedLocationId)에 필요해 함께 즉시 로딩 */
    @EntityGraph(attributePaths = {"location"})
    List<TangibleAsset> findAllByAssignTypeInAndLifeStatusAndStatus(
            Collection<TangibleAsset.AssignType> assignTypes, TangibleAsset.LifeStatus lifeStatus, TangibleAsset.Status status);

    /** S-420 대여 가능 자산 - LOANABLE(대여가능)·ON_LOAN(대여중, 대여자 표시용으로 목록에 남긴다) */
    @EntityGraph(attributePaths = {"category"})
    List<TangibleAsset> findAllByAssignTypeInAndStatus(
            Collection<TangibleAsset.AssignType> assignTypes, TangibleAsset.Status status);

    /** S-700 종류별 현황 - 처분완료(폐기)는 더 이상 보유중인 자산이 아니므로 제외. [0]=종류명, [1]=건수, 건수 내림차순 */
    @Query("select t.category.categoryName, count(t) from TangibleAsset t " +
            "where t.status = 'ENABLE' and t.lifeStatus <> 'DISPOSED' " +
            "group by t.category.categoryName order by count(t) desc")
    List<Object[]> countActiveGroupByCategory();

    /** S-700 위치별 현황 - countActiveGroupByCategory와 동일한 스코프(처분완료 제외) */
    @Query("select t.location.locationName, count(t) from TangibleAsset t " +
            "where t.status = 'ENABLE' and t.lifeStatus <> 'DISPOSED' " +
            "group by t.location.locationName order by count(t) desc")
    List<Object[]> countActiveGroupByLocation();

    /** S-700 상태별 현황 - 처분완료도 포함(생애 상태 분포 자체가 목적이므로). [0]=life_status, [1]=건수 */
    @Query("select t.lifeStatus, count(t) from TangibleAsset t where t.status = 'ENABLE' group by t.lifeStatus")
    List<Object[]> countGroupByLifeStatus();
}
