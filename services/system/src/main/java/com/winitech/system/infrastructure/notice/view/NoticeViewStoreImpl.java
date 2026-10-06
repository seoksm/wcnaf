package com.winitech.system.infrastructure.notice.view;

import com.winitech.system.domain.notice.view.NoticeView;
import com.winitech.system.domain.notice.view.NoticeViewStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeViewStoreImpl implements NoticeViewStore {

    private final NoticeViewRepository noticeViewRepository;

    @Override
    public void store(NoticeView view) {
        noticeViewRepository.save(view);
    }
}
