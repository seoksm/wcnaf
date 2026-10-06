package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelRowSigner;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * S-213 엑셀 업서트 preview 결과 위변조 방지.
 * <p>
 * preview()가 각 행을 만들 때 이 서명을 함께 붙여 클라이언트에 내려주고, commit()은 클라이언트가
 * 그대로 돌려준 행의 서명을 서버가 다시 계산해 비교한다. 서명 대상 canonical 문자열에는 실제로
 * 저장에 쓰이는 필드(action/카테고리·위치·사용자 ID/상태/금액 등)와 preview 시점 자산의
 * entityVersion(JPA @Version)까지 포함하므로, 클라이언트가 이 중 하나라도 바꾸면 서명이 더 이상
 * 일치하지 않아 즉시 걸러진다. 서버만 아는 키로 서명하므로 클라이언트는 유효한 서명을 위조할 수 없다.
 * <p>
 * 서명 키는 {@code winitech.smart-asset.excel-upsert.signing-key} 프로퍼티로 주입되며, 실제 값은
 * 환경변수 {@code SMART_ASSET_EXCEL_SIGNING_KEY}로 전달된다(코드/설정 파일에는 값 자체를 두지 않는다).
 * 이 값이 없거나(빈 문자열 포함) {@value #MIN_KEY_BYTES}바이트 미만이면 빈약한 키로 조용히 뜨는 것을
 * 막기 위해 애플리케이션 기동 자체를 즉시 실패시킨다 - 키 값이나 그 일부는 어떤 로그에도 남기지 않고
 * 길이 조건 위반 사실만 알린다.
 */
@Slf4j
@Component
public class ExcelRowSignerImpl implements ExcelRowSigner {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MIN_KEY_BYTES = 32;

    private final SecretKeySpec key;

    public ExcelRowSignerImpl(@Value("${winitech.smart-asset.excel-upsert.signing-key:}") String signingKey) {
        String safeKey = signingKey == null ? "" : signingKey;
        if (safeKey.getBytes(StandardCharsets.UTF_8).length < MIN_KEY_BYTES) {
            throw new IllegalStateException(
                    "winitech.smart-asset.excel-upsert.signing-key(환경변수 SMART_ASSET_EXCEL_SIGNING_KEY)가 "
                            + "설정되지 않았거나 너무 짧습니다. 최소 " + MIN_KEY_BYTES + "바이트 이상의 무작위 키가 필요합니다.");
        }
        this.key = new SecretKeySpec(safeKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    @Override
    public String sign(TangibleAssetExcelRow row) {
        return Base64.getEncoder().encodeToString(computeMac(row));
    }

    @Override
    public boolean verify(TangibleAssetExcelRow row) {
        if (row.getSignature() == null) {
            return false;
        }

        byte[] actual;
        try {
            actual = Base64.getDecoder().decode(row.getSignature());
        } catch (IllegalArgumentException e) {
            return false;
        }

        // 문자열 equals 대신 MessageDigest.isEqual로 비교한다 - equals는 앞에서부터 다른 바이트를
        // 만나는 즉시 반환해 비교 소요 시간이 일치하는 접두 길이에 비례하므로, 이 시간차를 재는
        // timing side-channel 공격으로 서명을 한 바이트씩 추측당할 수 있다. isEqual은 항상 같은
        // 시간이 걸리도록 전체를 비교해 이 공격을 막는다.
        return MessageDigest.isEqual(computeMac(row), actual);
    }

    private byte[] computeMac(TangibleAssetExcelRow row) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(canonicalize(row).getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            log.error("엑셀 업서트 행 서명 계산 실패 (rowNum={})", row.getRowNum(), e);
            throw new IllegalStateException("엑셀 업서트 행 서명 중 오류가 발생했습니다.", e);
        }
    }

    private String canonicalize(TangibleAssetExcelRow row) {
        return String.join("|",
                String.valueOf(row.getRowNum()),
                s(row.getAction()),
                s(row.getTangibleAssetId()),
                s(row.getAssetName()),
                s(row.getCategoryId()),
                s(row.getLocationId()),
                s(row.getLifeStatus()),
                s(row.getAssignType()),
                s(row.getCurrentMemberId()),
                s(row.getAcquisitionDate()),
                s(row.getAcquisitionAmount()),
                s(row.getModelName()),
                s(row.getManufacturer()),
                s(row.getSerialNo()),
                s(row.getMemo()),
                s(row.getEntityVersion())
        );
    }

    private String s(Object value) {
        return value == null ? "" : value.toString();
    }
}
