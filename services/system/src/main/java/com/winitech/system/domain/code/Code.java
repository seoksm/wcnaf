package com.winitech.system.domain.code;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Code extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;

    @NonNull
    private String code;

    @NonNull
    private String name;

    private String description;

    private Integer depthNo; //깊이

    private Integer orderNo; //정렬순서

    @NonNull
    @Enumerated(EnumType.STRING)
    private UseStatus useStatus; // 사용여부

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status { // 삭제 여부를 관리하기 위한 ENUM
        ENABLE("활성화"),
        DISABLE("비활성화");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum UseStatus {
        USED("사용"),
        UNUSED("미사용");
        private final String description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Code parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final List<Code> children = new ArrayList<>();

    @Builder
    public Code(
        @NonNull String code,
        @NonNull String name,
        String description,
        Integer depthNo,
        Integer orderNo,
        @NonNull UseStatus useStatus,
        Code parent
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.depthNo = depthNo;
        this.orderNo = orderNo;
        this.useStatus = useStatus;
        this.status = Status.ENABLE;
        this.parent = parent;
    }
}
