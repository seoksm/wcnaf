package com.winitech.system.domain.user;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.userOrganization.UserOrganization;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ User.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:41
* @see : None
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;
    @NonNull
    private String username;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String fullName;
    @NonNull
    private String email;
    @NonNull
    private String phoneNumber;
    private String employeeNo;
    private String dutyName;
    @NonNull
    private String hashedPassword;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @Enumerated(EnumType.STRING)
    @NonNull
    private JoinStatus joinStatus;
    @Enumerated(EnumType.STRING)
    @NonNull
    private Status status;
    private OffsetDateTime lastPasswordChangedAt; 
    
    public void setDepartmentId(UUID departmentId) {
        if (departmentId != null) {
            this.department = new Department();
            this.department.setId(departmentId);
        } else {
            this.department = null;
        }
    }
    
    @OneToMany(mappedBy = "user")
    Set<UserOrganization> userOrganizations;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("활성화"), DISABLE("비활성화");
        private final String description;
    }
    @Getter
    @RequiredArgsConstructor
    public enum UserRole {
        ADMIN("관리자"), SUB_ADMIN("서브 관리자"), CRDNS("유관 기관"), USER("일반 사용자"), API("API");
        private final String description;
    }
    @Getter
    @RequiredArgsConstructor
    public enum JoinStatus {
        REQUIRED("요청"), ACCEPTED("승인"), RESIGNED("퇴사"), ABSENCE("휴직");
        private final String description;
    }
    @Builder
    public User(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phoneNumber,
        String employeeNo,
        String dutyName,
        String hashedPassword,
        Department department
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.employeeNo = employeeNo;
        this.dutyName = dutyName;
        this.hashedPassword = hashedPassword;
        this.department = department;
        this.status = Status.ENABLE;
        this.joinStatus = JoinStatus.REQUIRED;
        this.lastPasswordChangedAt = OffsetDateTime.now();
    }
    public void enable() {
        this.status = Status.ENABLE;
    }
    public void disable() {this.status = Status.DISABLE;}
    public void acceptUser() {this.joinStatus = JoinStatus.ACCEPTED;}

    public User(
            UUID id,
            String username,
            String firstName,
            String lastName,
            String fullName,
            String email,
            String phoneNumber,
            String employeeNo,
            String dutyName,
            Department department,
            JoinStatus joinStatus,
            Status status
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.employeeNo = employeeNo;
        this.dutyName = dutyName;
        this.department = department;
        this.joinStatus = joinStatus;
        this.status = status;
    }
}
