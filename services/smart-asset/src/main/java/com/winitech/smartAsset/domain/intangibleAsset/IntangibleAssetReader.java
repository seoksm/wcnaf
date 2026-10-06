package com.winitech.smartAsset.domain.intangibleAsset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface IntangibleAssetReader {

    IntangibleAsset findById(UUID intangibleAssetId);

    /** S-500 목록 - expiryBefore가 있으면 그 날짜 이전(이미 만료된 것 포함) 만료건만, keyword는 이름 검색 */
    Page<IntangibleAsset> findAll(String keyword, LocalDate expiryBefore, Pageable pageable);
}
