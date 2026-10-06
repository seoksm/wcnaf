package com.winitech.smartAsset.domain.dashboard;

import com.winitech.common.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

/** S-701 대시보드 설정 - 개인별 위젯 표시여부·순서. */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DashboardWidgetConfig extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "dashboard_widget_config_id")
    private UUID id;

    @NonNull
    private UUID memberId;

    @NonNull
    @Enumerated(EnumType.STRING)
    private DashboardWidgetKey widgetKey;

    @NonNull
    private Boolean visible;

    @NonNull
    private Integer sortOrder;

    @Builder
    public DashboardWidgetConfig(UUID memberId, DashboardWidgetKey widgetKey, Boolean visible, Integer sortOrder) {
        this.memberId = memberId;
        this.widgetKey = widgetKey;
        this.visible = visible;
        this.sortOrder = sortOrder;
    }
}
