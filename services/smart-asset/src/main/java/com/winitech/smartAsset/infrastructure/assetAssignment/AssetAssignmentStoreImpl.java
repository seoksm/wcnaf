package com.winitech.smartAsset.infrastructure.assetAssignment;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AssetAssignmentStoreImpl implements AssetAssignmentStore {

    private final AssetAssignmentRepository assetAssignmentRepository;

    @Override
    public AssetAssignment store(AssetAssignment assetAssignment) {
        return assetAssignmentRepository.save(assetAssignment);
    }

    /**
     * Hibernate는 같은 flush 안에서 등록 순서와 무관하게 INSERT를 UPDATE보다 먼저 실행한다.
     * 그대로 두면 "기존 배정 release → 새 배정 insert" 순서로 호출해도 실제로는 새 배정 INSERT가
     * 먼저 나가, 기존 배정이 아직 released_at IS NULL인 상태에서 새 활성 배정이 끼어들어
     * idx_asset_assignment_tangible_asset_active(자산당 활성 배정 최대 1건) 유니크 인덱스를
     * 위반할 수 있다. saveAndFlush로 이 release UPDATE를 즉시 커밋 전 flush해 반드시 다음에 올
     * 신규 배정 INSERT보다 먼저 DB에 반영되도록 순서를 강제한다.
     */
    @Override
    public void release(AssetAssignment assetAssignment) {
        assetAssignment.release();
        assetAssignmentRepository.saveAndFlush(assetAssignment);
    }
}
