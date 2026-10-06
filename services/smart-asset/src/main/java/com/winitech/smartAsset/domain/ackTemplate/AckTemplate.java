package com.winitech.smartAsset.domain.ackTemplate;

import com.winitech.common.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

/**
 * S-433 확인서 문구 - RECEIPT(수령)/RETURN(반납) 2종 고정, V19 마이그레이션이 시드한다.
 * 편집은 이후 요청에만 적용된다(K7) - 이미 발급된 acknowledgement.bodySnapshot은 이 템플릿을
 * 참조하지 않고 요청 시점 렌더링 결과를 그대로 들고 있어 영향받지 않는다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AckTemplate extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "ack_template_id")
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NonNull
    private String bodyTpl;

    private UUID updatedBy;

    @Getter
    public enum Type {
        RECEIPT("수령"),
        RETURN("반납");
        private final String description;
        Type(String description) { this.description = description; }
    }

    public void updateBody(String bodyTpl, UUID updatedBy) {
        this.bodyTpl = bodyTpl;
        this.updatedBy = updatedBy;
    }
}
