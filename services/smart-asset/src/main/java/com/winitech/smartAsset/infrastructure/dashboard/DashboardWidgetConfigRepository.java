package com.winitech.smartAsset.infrastructure.dashboard;

import com.winitech.smartAsset.domain.dashboard.DashboardWidgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DashboardWidgetConfigRepository extends JpaRepository<DashboardWidgetConfig, UUID> {

    List<DashboardWidgetConfig> findAllByMemberId(UUID memberId);

    @Modifying
    @Query("delete from DashboardWidgetConfig c where c.memberId = :memberId")
    void deleteAllByMemberId(UUID memberId);
}
