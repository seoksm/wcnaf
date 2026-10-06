package com.winitech.smartAsset.domain.tangibleAsset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentReader;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentStore;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryReader;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryStore;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationReader;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetReader;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 자산 등록/수정과 이력(asset_history) 저장이 하나의 트랜잭션 경계 안에서 일관되게 실패/성공해야
 * 한다는 요구사항을 검증한다. 이 클래스는 순수 Mockito 단위 테스트로, 리포지토리 경계(Reader/Store)는
 * 모두 모킹하고 TangibleAsset 엔티티 자체의 도메인 로직(modify/diffableFields)은 실제로 수행한다.
 * <p>
 * 여기서 "롤백" 관련 테스트가 검증하는 것은 정확히는 "storeHistory()가 실패를 삼키지 않고
 * 서비스 메서드 밖으로 예외를 전파하는가"이다 - 실제 DB 트랜잭션 롤백 자체는 이 메서드들에 걸린
 * {@code @Transactional}과 Spring의 기본 롤백 정책(언체크 예외 전파 시 롤백)이 보장하는 영역이라
 * 이 단위 테스트의 책임 범위 밖이다. 예외가 서비스 메서드 밖으로 전파되기만 하면 실제 운영 환경에서는
 * 그 트랜잭션 전체가 롤백된다.
 */
@ExtendWith(MockitoExtension.class)
class TangibleAssetServiceImplTest {

    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private TangibleAssetStore tangibleAssetStore;
    @Mock private AssetCategoryReader assetCategoryReader;
    @Mock private AssetLocationReader assetLocationReader;
    @Mock private AssetAssignmentReader assetAssignmentReader;
    @Mock private AssetAssignmentStore assetAssignmentStore;
    @Mock private AssetHistoryReader assetHistoryReader;
    @Mock private AssetHistoryStore assetHistoryStore;
    @Mock private LoginUserContext loginUserContext;
    @Mock private AssetCodeGenerator assetCodeGenerator;
    @Mock private DisposalAssetReader disposalAssetReader;
    @Mock private DisposalAssetStore disposalAssetStore;

    private final ObjectMapper realObjectMapper = new ObjectMapper();

    private UUID categoryId;
    private UUID locationId;
    private AssetCategory category;
    private AssetLocation location;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        locationId = UUID.randomUUID();
        category = mock(AssetCategory.class);
        location = mock(AssetLocation.class);
        lenient().when(category.getCategoryName()).thenReturn("전자제품");
        lenient().when(location.getLocationName()).thenReturn("본사");
        lenient().when(assetCategoryReader.findById(categoryId)).thenReturn(category);
        lenient().when(assetLocationReader.findById(locationId)).thenReturn(location);
        // TangibleAssetStore.modify()는 실제 구현(TangibleAssetStoreImpl)처럼 엔티티 자신의
        // modify()를 그대로 위임 호출하도록 흉내낸다 - 여기서 검증하려는 것은 엔티티의 실제
        // diff/변경 로직이지, 인프라 계층의 얇은 위임 자체가 아니다.
        lenient().when(tangibleAssetStore.modify(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    TangibleAsset asset = invocation.getArgument(0);
                    TangibleAssetCommand.UpdateCommand cmd = invocation.getArgument(1);
                    AssetCategory cat = invocation.getArgument(2);
                    AssetLocation loc = invocation.getArgument(3);
                    return asset.modify(cmd, cat, loc);
                });
    }

    private TangibleAssetServiceImpl serviceWith(ObjectMapper objectMapper) {
        lenient().when(assetCodeGenerator.generate()).thenReturn("AST-2026-0001");
        return new TangibleAssetServiceImpl(
                tangibleAssetReader, tangibleAssetStore, assetCategoryReader, assetLocationReader,
                assetAssignmentReader, assetAssignmentStore, assetHistoryReader, assetHistoryStore,
                loginUserContext, objectMapper, assetCodeGenerator, disposalAssetReader, disposalAssetStore);
    }

    private TangibleAssetCommand registerCommand() {
        return TangibleAssetCommand.builder()
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
    }

    private TangibleAsset existingAsset() {
        return TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .category(category)
                .location(location)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
    }

    private List<Map<String, String>> parseChanges(String json) throws JsonProcessingException {
        return realObjectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {});
    }

    /** 종류(category)를 지정하지 않아 상각 기준이 없는(D6/NO_BASIS) 불용 상태 자산 - 처분 시
     * 장부가 계산이 취득가액 그대로가 되므로 disuse/restore/dispose 테스트에서 간단히 재사용한다. */
    private TangibleAsset disuseAsset() {
        return TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.DISUSE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
    }

    @Test
    void 정상_등록_시_REGISTER_이력이_생성된다() throws Exception {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        when(tangibleAssetStore.store(any())).thenReturn(UUID.randomUUID());

        service.createTangibleAsset(registerCommand());

        ArgumentCaptor<AssetHistory> captor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(captor.capture());
        AssetHistory history = captor.getValue();

        assertThat(history.getHistoryType()).isEqualTo(AssetHistory.HistoryType.REGISTER);
        List<Map<String, String>> changes = parseChanges(history.getChangedFields());
        assertThat(changes).isNotEmpty();
        assertThat(changes).allSatisfy(change -> assertThat(change.get("before")).isNull());
    }

    @Test
    void 정상_수정_시_실제_변경된_필드만_기록된다() throws Exception {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existingAsset());

        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("데스크탑") // 유일하게 바뀌는 필드
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        service.updateTangibleAsset(updateCommand);

        ArgumentCaptor<AssetHistory> captor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(captor.capture());
        List<Map<String, String>> changes = parseChanges(captor.getValue().getChangedFields());

        assertThat(changes).hasSize(1);
        assertThat(changes.get(0)).containsEntry("field", "자산명")
                .containsEntry("before", "노트북")
                .containsEntry("after", "데스크탑");
        assertThat(captor.getValue().getHistoryType()).isEqualTo(AssetHistory.HistoryType.MODIFY);
    }

    @Test
    void 변경_사항이_없으면_이력을_생성하지_않는다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existingAsset());

        TangibleAssetCommand.UpdateCommand sameCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        service.updateTangibleAsset(sameCommand);

        verify(assetHistoryStore, never()).store(any());
    }

    @Test
    void 이력_직렬화_실패_시_예외가_전파되어_트랜잭션이_롤백된다() throws Exception {
        ObjectMapper brokenObjectMapper = mock(ObjectMapper.class);
        when(brokenObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
        TangibleAssetServiceImpl service = serviceWith(brokenObjectMapper);
        when(tangibleAssetStore.store(any())).thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> service.createTangibleAsset(registerCommand()))
                .isInstanceOf(RuntimeException.class);

        // 이력이 저장되지 않았음을 확인 - 실제 운영에서는 이 예외가 @Transactional 메서드 밖으로
        // 전파되어, 방금 store()로 시도된 자산 등록 자체도 함께 롤백된다.
        verify(assetHistoryStore, never()).store(any());
    }

    @Test
    void 이력_저장_실패_시_예외가_전파되어_트랜잭션이_롤백된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        when(tangibleAssetStore.store(any())).thenReturn(UUID.randomUUID());
        doThrow(new RuntimeException("DB 저장 실패")).when(assetHistoryStore).store(any());

        assertThatThrownBy(() -> service.createTangibleAsset(registerCommand()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB 저장 실패");
    }

    @Test
    void 일괄_변경_중_하나가_실패하면_예외가_전파되어_전체가_롤백_대상이_된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId1 = UUID.randomUUID();
        UUID assetId2 = UUID.randomUUID();
        when(tangibleAssetReader.findById(assetId1)).thenReturn(existingAsset());
        when(tangibleAssetReader.findById(assetId2)).thenReturn(existingAsset());

        // 첫 번째 자산의 이력 저장은 성공하고, 두 번째에서 실패하는 상황을 흉내낸다.
        doNothing().doThrow(new RuntimeException("두 번째 자산 이력 저장 실패"))
                .when(assetHistoryStore).store(any());

        TangibleAssetCommand.BatchUpdateCommand batchCommand = TangibleAssetCommand.BatchUpdateCommand.builder()
                .tangibleAssetIds(Arrays.asList(assetId1, assetId2))
                .lifeStatus(TangibleAsset.LifeStatus.STORAGE)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .build();

        assertThatThrownBy(() -> service.batchUpdateTangibleAsset(batchCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("두 번째 자산 이력 저장 실패");

        // 두 번째 항목에서 실패하기 전까지 첫 번째 항목은 이미 처리를 시도했다는 것을 확인한다 -
        // 하지만 batchUpdateTangibleAsset() 전체가 하나의 @Transactional 경계이므로, 이 예외가
        // 밖으로 전파되면 이미 처리된 첫 번째 자산의 변경분까지 포함해 배치 전체가 롤백된다
        // (일부만 반영되는 부분 성공은 없다).
        verify(assetHistoryStore, times(2)).store(any());
    }

    // ------------------------------------------------------------------
    // 배정 형태(assignType)-배정 사용자(currentMemberId) 불변식 - 서비스 진입점 레벨 검증.
    // 실제 규칙 자체는 TangibleAsset.resolveMemberId() 한 곳에만 있고(TangibleAssetTest 참고),
    // 여기서는 등록/단건 수정/일괄 변경이 전부 그 단일 규칙을 거쳐 "같은 결과"를 내는지 확인한다.
    // ------------------------------------------------------------------

    @Test
    void 등록에서_PERSONAL인데_사용자가_없으면_InvalidParamException() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        TangibleAssetCommand command = TangibleAssetCommand.builder()
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        assertThatThrownBy(() -> service.createTangibleAsset(command))
                .isInstanceOf(InvalidParamException.class);
        verify(tangibleAssetStore, never()).store(any());
    }

    @Test
    void 단건_수정과_일괄_변경은_PERSONAL_사용자_누락에_대해_동일하게_실패한다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existingAsset());

        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL) // currentMemberId 지정 안 함
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        assertThatThrownBy(() -> service.updateTangibleAsset(updateCommand))
                .isInstanceOf(InvalidParamException.class);

        // 동일한 자산·동일한 요청을 일괄 변경 경로로 보내도 같은 예외가 난다 -
        // applyUpdate()/modify()라는 같은 코드를 공유하기 때문에 구조적으로 결과가 같을 수밖에 없다.
        when(tangibleAssetReader.findById(assetId)).thenReturn(existingAsset());
        TangibleAssetCommand.BatchUpdateCommand batchCommand = TangibleAssetCommand.BatchUpdateCommand.builder()
                .tangibleAssetIds(List.of(assetId))
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .build();

        assertThatThrownBy(() -> service.batchUpdateTangibleAsset(batchCommand))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void SHARED로_수정하면서_사용자를_지정해도_저장시_null로_정규화된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        TangibleAsset existing = existingAsset(); // USE/UNASSIGNED, currentMemberId 없음
        when(tangibleAssetReader.findById(assetId)).thenReturn(existing);

        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.SHARED)
                .currentMemberId(UUID.randomUUID()) // SHARED에는 무의미한 값 - 무시돼야 한다
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        service.updateTangibleAsset(updateCommand);

        assertThat(existing.getAssignType()).isEqualTo(TangibleAsset.AssignType.SHARED);
        assertThat(existing.getCurrentMemberId()).isNull();
    }

    @Test
    void 불용_전환_시_배정이_자동_해제되고_배정_이력이_종료된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        TangibleAsset existing = TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .category(category)
                .location(location)
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existing);

        AssetAssignment currentAssignment = mock(AssetAssignment.class);
        when(assetAssignmentReader.findCurrentByTangibleAssetId(any())).thenReturn(java.util.Optional.of(currentAssignment));

        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("노트북")
                .categoryId(categoryId)
                .locationId(locationId)
                .lifeStatus(TangibleAsset.LifeStatus.DISUSE)
                .assignType(TangibleAsset.AssignType.PERSONAL) // 클라이언트가 그대로 보내도 강제로 UNASSIGNED가 돼야 한다
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        service.updateTangibleAsset(updateCommand);

        assertThat(existing.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
        assertThat(existing.getCurrentMemberId()).isNull();
        // 기존 배정 이력을 종료(release)하고, UNASSIGNED는 사용자를 필요로 하지 않으므로 새 배정은 열지 않는다.
        verify(assetAssignmentStore).release(currentAssignment);
        verify(assetAssignmentStore, never()).store(any());
    }

    // ------------------------------------------------------------------
    // 유형자산 목록 페이지네이션 - page/size clamp, 정렬 화이트리스트 검증 (성능 개선 작업).
    // 실제 검색/정렬/페이지 경계·N+1 제거는 TangibleAssetRepositoryTest(통합 테스트)가 검증하고,
    // 여기서는 TangibleAssetServiceImpl이 요청값을 안전한 Pageable로 변환하는 로직만 검증한다.
    // ------------------------------------------------------------------

    private Pageable capturePageable(TangibleAssetServiceImpl service, String keyword, Integer page, Integer size, String sort) {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(tangibleAssetReader.findAllByContainsKeyword(any(), captor.capture()))
                .thenReturn(new PageImpl<>(List.of()));

        service.loadTangibleAssetList(keyword, page, size, sort);

        return captor.getValue();
    }

    @Test
    void size_미지정시_기본값_20이_적용된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, 0, null, null);

        assertThat(pageable.getPageSize()).isEqualTo(20);
    }

    @Test
    void size가_최대치를_넘으면_200으로_clamp된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, 0, 99999, null);

        assertThat(pageable.getPageSize()).isEqualTo(200);
    }

    @Test
    void size가_1보다_작으면_1로_clamp된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, 0, 0, null);

        assertThat(pageable.getPageSize()).isEqualTo(1);
    }

    @Test
    void page가_음수면_0으로_보정된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, -5, null, null);

        assertThat(pageable.getPageNumber()).isEqualTo(0);
    }

    @Test
    void sort_미지정시_createAt_내림차순이_기본값이다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, 0, null, null);

        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createAt"));
    }

    @Test
    void 허용된_정렬필드는_그대로_적용된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        Pageable pageable = capturePageable(service, null, 0, null, "assetName,asc");

        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "assetName"));
    }

    @Test
    void 화이트리스트에_없는_정렬필드는_기본정렬로_대체된다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);

        // status는 엔티티에 실재하는 필드이지만 정렬 허용 목록에는 없다 - 임의 필드로 정렬을
        // 시도해도 PropertyReferenceException 없이 조용히 기본 정렬로 대체돼야 한다.
        Pageable pageable = capturePageable(service, null, 0, null, "status,asc");

        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createAt"));
    }

    // ------------------------------------------------------------------
    // 불용/복귀/처분 처리 (S-240~242) - 엔티티 자체의 상태 전이 불변식은 TangibleAssetTest가
    // 검증하므로, 여기서는 서비스가 배정 해제·이력 기록·disposal_asset 저장을 올바르게 오케스트레이션
    // 하는지만 확인한다.
    // ------------------------------------------------------------------

    @Test
    void disuseTangibleAsset_배정된_자산을_불용처리하면_배정이_해제되고_이력이_남는다() throws Exception {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        TangibleAsset existing = TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existing);
        AssetAssignment currentAssignment = mock(AssetAssignment.class);
        when(assetAssignmentReader.findCurrentByTangibleAssetId(assetId)).thenReturn(java.util.Optional.of(currentAssignment));

        service.disuseTangibleAsset(assetId, "고장");

        assertThat(existing.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISUSE);
        verify(assetAssignmentStore).release(currentAssignment);

        ArgumentCaptor<AssetHistory> captor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(captor.capture());
        assertThat(captor.getValue().getHistoryType()).isEqualTo(AssetHistory.HistoryType.DISUSE);

        List<Map<String, String>> changes = parseChanges(captor.getValue().getChangedFields());
        assertThat(changes).extracting(c -> c.get("field")).containsExactly("생애상태", "불용사유");
        assertThat(changes.get(1)).containsEntry("after", "고장");
    }

    @Test
    void disuseTangibleAsset_사유없이_호출하면_생애상태_변경만_기록된다() throws Exception {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existingAsset()); // USE/UNASSIGNED

        service.disuseTangibleAsset(assetId, null);

        verify(assetAssignmentStore, never()).release(any());
        ArgumentCaptor<AssetHistory> captor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(captor.capture());
        List<Map<String, String>> changes = parseChanges(captor.getValue().getChangedFields());
        assertThat(changes).hasSize(1);
        assertThat(changes.get(0)).containsEntry("field", "생애상태");
    }

    @Test
    void restoreTangibleAsset_복귀하면_사용상태로_바뀌고_STATUS_CHANGE_이력이_남는다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        TangibleAsset existing = disuseAsset();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existing);

        service.restoreTangibleAsset(assetId);

        assertThat(existing.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.USE);
        ArgumentCaptor<AssetHistory> captor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(captor.capture());
        assertThat(captor.getValue().getHistoryType()).isEqualTo(AssetHistory.HistoryType.STATUS_CHANGE);
    }

    @Test
    void disposeTangibleAsset_처리하면_disposal_asset이_저장되고_DISPOSAL_이력이_남는다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        UUID assetId = UUID.randomUUID();
        TangibleAsset existing = disuseAsset();
        when(tangibleAssetReader.findById(assetId)).thenReturn(existing);
        when(loginUserContext.getUserId()).thenReturn(UUID.randomUUID()); // DisposalAsset.disposedBy는 @NonNull

        TangibleAssetDisposalCommand command = TangibleAssetDisposalCommand.builder()
                .disposalReasonCode(DisposalAsset.DisposalReason.SALE)
                .disposalAmount(new BigDecimal("300000"))
                .counterparty("중고나라")
                .memo("정상 매각")
                .build();

        service.disposeTangibleAsset(assetId, command);

        assertThat(existing.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISPOSED);

        ArgumentCaptor<DisposalAsset> disposalCaptor = ArgumentCaptor.forClass(DisposalAsset.class);
        verify(disposalAssetStore).store(disposalCaptor.capture());
        DisposalAsset stored = disposalCaptor.getValue();
        assertThat(stored.getTangibleAsset()).isEqualTo(existing);
        // 자산에 종류(category)가 없어 상각 기준이 없으므로(D6) 취득가액을 그대로 장부가로 쓴다
        assertThat(stored.getBookValueAtDisposal()).isEqualByComparingTo(existing.getAcquisitionAmount());
        assertThat(stored.getDisposalReasonCode()).isEqualTo(DisposalAsset.DisposalReason.SALE);
        assertThat(stored.getCounterparty()).isEqualTo("중고나라");

        ArgumentCaptor<AssetHistory> historyCaptor = ArgumentCaptor.forClass(AssetHistory.class);
        verify(assetHistoryStore).store(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getHistoryType()).isEqualTo(AssetHistory.HistoryType.DISPOSAL);
    }

    @SuppressWarnings("unchecked")
    @Test
    void loadDisposalList_필터가_없으면_불용과_처분완료를_함께_조회한다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        ArgumentCaptor<List<TangibleAsset.LifeStatus>> statusCaptor = ArgumentCaptor.forClass(List.class);
        when(tangibleAssetReader.findByLifeStatusIn(statusCaptor.capture(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of());

        service.loadDisposalList(null, 0, 20, null);

        assertThat(statusCaptor.getValue())
                .containsExactlyInAnyOrder(TangibleAsset.LifeStatus.DISUSE, TangibleAsset.LifeStatus.DISPOSED);
    }

    @SuppressWarnings("unchecked")
    @Test
    void loadDisposalList_필터가_있으면_해당_상태만_조회한다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        ArgumentCaptor<List<TangibleAsset.LifeStatus>> statusCaptor = ArgumentCaptor.forClass(List.class);
        when(tangibleAssetReader.findByLifeStatusIn(statusCaptor.capture(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of());

        service.loadDisposalList(TangibleAsset.LifeStatus.DISPOSED, 0, 20, null);

        assertThat(statusCaptor.getValue()).containsExactly(TangibleAsset.LifeStatus.DISPOSED);
    }

    @Test
    void loadDisposalList_처분완료_행은_disposal_asset에_고정된_장부가를_그대로_쓴다() {
        TangibleAssetServiceImpl service = serviceWith(realObjectMapper);
        TangibleAsset disposed = disuseAsset();
        disposed.dispose(); // 엔티티의 실제 불변식을 그대로 거쳐 DISPOSED로 전이시킨다
        when(tangibleAssetReader.findByLifeStatusIn(any(), any())).thenReturn(new PageImpl<>(List.of(disposed)));
        when(assetCategoryReader.findAllByContainsKeyword(null)).thenReturn(List.of());

        DisposalAsset disposalRecord = DisposalAsset.builder()
                .tangibleAsset(disposed)
                .bookValueAtDisposal(new BigDecimal("123456"))
                .disposalReasonCode(DisposalAsset.DisposalReason.SCRAP)
                .disposedBy(UUID.randomUUID())
                .build();
        when(disposalAssetReader.findByTangibleAssetId(disposed.getId())).thenReturn(java.util.Optional.of(disposalRecord));

        Page<DisposalAssetRowInfo> page = service.loadDisposalList(TangibleAsset.LifeStatus.DISPOSED, 0, 20, null);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getBookValue()).isEqualByComparingTo(new BigDecimal("123456"));
    }
}
