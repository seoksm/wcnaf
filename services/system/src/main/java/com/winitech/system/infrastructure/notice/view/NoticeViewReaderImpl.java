package com.winitech.system.infrastructure.notice.view;

import com.winitech.system.domain.notice.view.NoticeViewReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeViewReaderImpl implements NoticeViewReader {

    private final NoticeViewRepository noticeViewRepository;

    @Override
    public boolean checkIfUserVisitedBefore(UUID noticeId, UUID viewerId) {
        return noticeViewRepository.findByNoticeIdAndUserId(noticeId, viewerId).isPresent();
    }
}
