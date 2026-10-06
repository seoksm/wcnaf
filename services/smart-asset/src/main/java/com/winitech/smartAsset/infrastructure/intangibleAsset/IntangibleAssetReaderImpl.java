package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAsset;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IntangibleAssetReaderImpl implements IntangibleAssetReader {

    private final IntangibleAssetRepository intangibleAssetRepository;

    @Override
    public IntangibleAsset findById(UUID intangibleAssetId) {
        return intangibleAssetRepository.findById(intangibleAssetId).orElseThrow();
    }

    @Override
    public Page<IntangibleAsset> findAll(String keyword, LocalDate expiryBefore, Pageable pageable) {
        String normalizedKeyword = normalizeKeyword(keyword);

        if (normalizedKeyword != null && expiryBefore != null) {
            return intangibleAssetRepository.findAllEnabledByKeywordAndExpiryBefore(normalizedKeyword, expiryBefore, pageable);
        }
        if (normalizedKeyword != null) {
            return intangibleAssetRepository.findAllEnabledByKeyword(normalizedKeyword, pageable);
        }
        if (expiryBefore != null) {
            return intangibleAssetRepository.findAllEnabledByExpiryBefore(expiryBefore, pageable);
        }
        return intangibleAssetRepository.findAllEnabled(pageable);
    }

    /** null·빈 문자열·공백 문자열을 모두 "검색어 없음"으로 취급한다 */
    private String normalizeKeyword(String keyword) {
        return (keyword == null || keyword.isBlank()) ? null : keyword;
    }
}
