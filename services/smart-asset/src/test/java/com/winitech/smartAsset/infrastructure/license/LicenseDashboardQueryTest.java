package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.License;
import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S-700 라이선스 정합성 위젯이 쓰는 라이선스별 그룹핑 쿼리(구매수량 합계·활성배정건수·미연결배정)
 * 회귀 테스트. 실제 Postgres에 붙는 통합 테스트라, 전체 목록을 훑지 않고 이번 테스트에서 만든
 * license_id만 필터링해 다른 테스트/운영 데이터의 영향을 받지 않게 한다.
 */
@SpringBootTest
@Transactional
class LicenseDashboardQueryTest {

    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    private LicensePurchaseRecordRepository licensePurchaseRecordRepository;
    @Autowired
    private LicenseAssignedUserRepository licenseAssignedUserRepository;

    private License licenseWithPurchaseAndAssignment;
    private License licenseWithoutPurchase;

    @BeforeEach
    void setUp() {
        String marker = "DASHTEST-" + UUID.randomUUID().toString().substring(0, 8);

        licenseWithPurchaseAndAssignment = licenseRepository.save(
                License.builder().name(marker + "-A").build());
        licenseWithoutPurchase = licenseRepository.save(
                License.builder().name(marker + "-B").build());

        licensePurchaseRecordRepository.save(LicensePurchaseRecord.builder()
                .license(licenseWithPurchaseAndAssignment)
                .purchaseDate(LocalDate.now())
                .quantity(10)
                .build());

        // A: 구매 10, 배정 2건(정원 이내) + 미연결 배정 1건(구매내역 없이 배정)
        licenseAssignedUserRepository.save(LicenseAssignedUser.builder()
                .license(licenseWithPurchaseAndAssignment)
                .memberId(UUID.randomUUID())
                .assignedBy(UUID.randomUUID())
                .build());
        licenseAssignedUserRepository.save(LicenseAssignedUser.builder()
                .license(licenseWithPurchaseAndAssignment)
                .memberId(UUID.randomUUID())
                .assignedBy(UUID.randomUUID())
                .build());
        licenseAssignedUserRepository.save(LicenseAssignedUser.builder()
                .license(licenseWithPurchaseAndAssignment)
                .memberId(UUID.randomUUID())
                .assignedBy(UUID.randomUUID())
                .build());

        // B: 구매내역 없이 배정 3건(전부 초과 판정 대상)
        for (int i = 0; i < 3; i++) {
            licenseAssignedUserRepository.save(LicenseAssignedUser.builder()
                    .license(licenseWithoutPurchase)
                    .memberId(UUID.randomUUID())
                    .assignedBy(UUID.randomUUID())
                    .build());
        }
    }

    @Test
    void sumQuantityGroupByLicense는_라이선스별_구매수량_합계를_반환한다() {
        Map<UUID, Long> byLicense = toMap(licensePurchaseRecordRepository.sumQuantityGroupByLicense());

        assertThat(byLicense.get(licenseWithPurchaseAndAssignment.getId())).isEqualTo(10L);
        assertThat(byLicense).doesNotContainKey(licenseWithoutPurchase.getId());
    }

    @Test
    void countActiveGroupByLicense는_라이선스별_활성배정건수를_반환한다() {
        Map<UUID, Long> byLicense = toMap(licenseAssignedUserRepository.countActiveGroupByLicense());

        assertThat(byLicense.get(licenseWithPurchaseAndAssignment.getId())).isEqualTo(3L);
        assertThat(byLicense.get(licenseWithoutPurchase.getId())).isEqualTo(3L);
    }

    @Test
    void countUnlinkedActive는_구매내역에_연결되지_않은_활성배정_건수만_센다() {
        // setUp에서 만든 배정은 전부 licensePurchaseRecord가 null인 미연결 배정이다(license_id만 있고
        // purchase_record_id는 비워둔 채 생성) - 총 6건(A 3건 + B 3건) 중 이 테스트가 만든 것만
        // 정확히 세는지는 절대값 비교가 다른 테스트 데이터에 취약하므로, 최소 6건 이상임만 확인한다.
        long unlinkedCount = licenseAssignedUserRepository.countUnlinkedActive();

        assertThat(unlinkedCount).isGreaterThanOrEqualTo(6L);
    }

    private static Map<UUID, Long> toMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (UUID) row[0],
                row -> ((Number) row[1]).longValue()));
    }
}
