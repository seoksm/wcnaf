package com.winitech.system.domain.userMfaBackupCode;

import com.winitech.common.domain.AbstractUuidEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userMfaBackupCode
 * └ UserMfaBackupCode.java
 * </pre>
 * TOTP 기기 분실 대비 1회용 백업코드. 평문은 발급 시 1회만 노출하고, 이후에는 해시만 보관한다.
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "user_mfa_backup_code")
public class UserMfaBackupCode extends AbstractUuidEntity {
    @NonNull
    private UUID userId;
    @NonNull
    private String codeHash;
    private OffsetDateTime usedAt;

    @Builder
    public UserMfaBackupCode(UUID id, UUID userId, String codeHash, OffsetDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.codeHash = codeHash;
        this.usedAt = usedAt;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed() {
        this.usedAt = OffsetDateTime.now();
    }
}
