package com.winitech.smartAsset.domain.intangibleAsset;

import com.winitech.common.bean.LoginUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IntangibleAssetServiceImpl extends EgovAbstractServiceImpl implements IntangibleAssetService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "expiryDate");

    private final IntangibleAssetReader intangibleAssetReader;
    private final IntangibleAssetStore intangibleAssetStore;
    private final IntangibleAssetActionLogReader intangibleAssetActionLogReader;
    private final IntangibleAssetActionLogStore intangibleAssetActionLogStore;
    private final LoginUserContext loginUserContext;

    @Transactional
    @Override
    public UUID createIntangibleAsset(IntangibleAssetCommand command) {
        return intangibleAssetStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateIntangibleAsset(IntangibleAssetCommand.UpdateCommand updateCommand) {
        IntangibleAsset asset = intangibleAssetReader.findById(updateCommand.getIntangibleAssetId());
        intangibleAssetStore.modify(asset, updateCommand);
    }

    @Transactional
    @Override
    public void deleteIntangibleAsset(UUID intangibleAssetId) {
        IntangibleAsset asset = intangibleAssetReader.findById(intangibleAssetId);
        intangibleAssetStore.delete(asset);
    }

    @Override
    public Page<IntangibleAssetInfo> loadList(String keyword, Integer withinDays, Integer page, Integer size) {
        LocalDate expiryBefore = withinDays != null ? LocalDate.now().plusDays(withinDays) : null;
        return intangibleAssetReader.findAll(keyword, expiryBefore, toPageable(page, size)).map(IntangibleAssetInfo::new);
    }

    @Override
    public IntangibleAssetInfo loadIntangibleAsset(UUID intangibleAssetId) {
        return new IntangibleAssetInfo(intangibleAssetReader.findById(intangibleAssetId));
    }

    @Override
    public List<IntangibleAssetActionLogInfo> loadActionLog(UUID intangibleAssetId) {
        return intangibleAssetActionLogReader.findAllByIntangibleAssetId(intangibleAssetId).stream()
                .map(IntangibleAssetActionLogInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void renew(UUID intangibleAssetId, LocalDate newExpiryDate, String note) {
        IntangibleAsset asset = intangibleAssetReader.findById(intangibleAssetId);
        LocalDate previous = asset.renew(newExpiryDate);

        intangibleAssetActionLogStore.store(IntangibleAssetActionLog.builder()
                .intangibleAsset(asset)
                .actionType(IntangibleAssetActionLog.ActionType.RENEW)
                .previousExpiryDate(previous)
                .newExpiryDate(newExpiryDate)
                .note(note)
                .actedBy(loginUserContext.getUserId())
                .build());
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
