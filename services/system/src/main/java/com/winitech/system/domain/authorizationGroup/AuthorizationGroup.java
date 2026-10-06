package com.winitech.system.domain.authorizationGroup;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.userOrganization.UserOrganization;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.User.Status;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.group
* ㄴ AuthorizationGroup.java
* @author : 차은혜 대리 (부설연구소)
* @since : 2025-01-22 오후 1:53
* @see : None
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "authorization_group")
public class AuthorizationGroup extends AbstractEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;
    @NonNull
    private String groupCode;
    @NonNull
    private String groupName;
    
    private String remark;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    @ColumnDefault("'ENABLE'")
    private AdminStatus adminStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    @ColumnDefault("'ENABLE'")
    private Status status;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    @ColumnDefault("'ENABLE'")
    private SystemStatus systemStatus;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "parent_authorization_group_id")
    private AuthorizationGroup parentAuthorizationGroup;

    @JsonBackReference
    @OneToMany(mappedBy = "parentAuthorizationGroup")
    @OrderBy("groupName ASC, groupCode ASC, id ASC")
    @Where(clause = "system_status = 'ENABLE'")
    private List<AuthorizationGroup> childrenAuthorizationGroup;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("ENABLE"),
        DISABLE("DISABLE");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum SystemStatus {
        ENABLE("ENABLE"),
        DISABLE("DISABLE");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AdminStatus {
        ENABLE("ENABLE"),
        DISABLE("DISABLE");
        private final String description;
    }

    @Builder
    public AuthorizationGroup(
            UUID id,
            String groupCode,
            String groupName,
            String remark,
            Status status,
            AdminStatus adminStatus,
            SystemStatus systemStatus,
            AuthorizationGroup parentAuthorizationGroup
    ) {
        this.id = id;
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.remark = remark;
        this.status = status;
        this.adminStatus = adminStatus;
        this.systemStatus = systemStatus;
        this.parentAuthorizationGroup = parentAuthorizationGroup;
    }


    public void enable() {
        this.systemStatus = SystemStatus.ENABLE;
    }
    public void disable() {
        this.systemStatus = SystemStatus.DISABLE;
    }
}
