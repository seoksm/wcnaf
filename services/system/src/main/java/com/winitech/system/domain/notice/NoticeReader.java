package com.winitech.system.domain.notice;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NoticeReader {

    Notice findById(UUID noticeId);

    Page<Notice> readNoticeList(int page, int pageSize, String title, String content, String author);
}
