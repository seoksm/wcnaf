package com.winitech.system.infrastructure.notice;

import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.notice.NoticeReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeReaderImpl implements NoticeReader {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice findById(UUID noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow();
    }

    @Override
    public Page<Notice> readNoticeList(int page, int pageSize, String title, String content, String author) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createAt").descending());
        return noticeRepository.getNoticeList(pageRequest, title, content, author);
    }
}
