package com.winitech.system.domain.notice.view;

import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
import lombok.RequiredArgsConstructor;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeViewServiceImpl extends EgovAbstractServiceImpl implements NoticeViewService {

    private final NoticeViewReader noticeViewReader;
    private final NoticeViewStore noticeViewStore;
    private final UserReader userReader;

    @Override
    public void checkViewer(Notice notice, UUID viewerId) {

        boolean checked = noticeViewReader.checkIfUserVisitedBefore(notice.getId(), viewerId);
        if (!checked) {
            User user = userReader.getUser(viewerId);

            noticeViewStore.store(
                    NoticeView.builder()
                            .notice(notice)
                            .user(user)
                            .build()
            );

            notice.increaseViewCount();
        }
    }
}
