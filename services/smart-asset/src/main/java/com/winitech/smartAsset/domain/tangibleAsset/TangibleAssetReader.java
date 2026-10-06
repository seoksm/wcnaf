package com.winitech.smartAsset.domain.tangibleAsset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TangibleAssetReader {

    TangibleAsset findById(UUID tangibleAssetId);

    Page<TangibleAsset> findAllByContainsKeyword(String keyword, Pageable pageable);

    Optional<TangibleAsset> findByAssetCode(String assetCode);

    long countByCategoryId(UUID categoryId);

    long countByLocationId(UUID locationId);

    Page<TangibleAsset> findByLifeStatusIn(Collection<TangibleAsset.LifeStatus> lifeStatuses, Pageable pageable);

    /** S-301/302 전수조사 대상 산정 - 사용중(USE) 자산 중 배정형태로 임직원형/관리자형을 가른다(Q-32) */
    List<TangibleAsset> findAllByAssignTypeInAndLifeStatus(Collection<TangibleAsset.AssignType> assignTypes,
                                                            TangibleAsset.LifeStatus lifeStatus);

    /** S-420 대여 가능 자산 - LOANABLE·ON_LOAN */
    List<TangibleAsset> findAllByAssignTypeIn(Collection<TangibleAsset.AssignType> assignTypes);

    /** S-700 종류별 현황 - [0]=종류명(String), [1]=건수(Long), 건수 내림차순, 처분완료 제외 */
    List<Object[]> countActiveGroupByCategory();

    /** S-700 위치별 현황 - [0]=위치명(String), [1]=건수(Long), 건수 내림차순, 처분완료 제외 */
    List<Object[]> countActiveGroupByLocation();

    /** S-700 상태별 현황 - [0]=LifeStatus, [1]=건수(Long), 처분완료 포함 */
    List<Object[]> countGroupByLifeStatus();
}
