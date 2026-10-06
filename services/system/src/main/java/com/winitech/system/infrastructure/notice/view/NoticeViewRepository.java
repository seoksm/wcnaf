package com.winitech.system.infrastructure.notice.view;

import com.winitech.system.domain.notice.view.NoticeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeViewRepository extends JpaRepository<NoticeView, UUID> {

    @Query("select v from NoticeView v join fetch v.notice join fetch v.user where v.notice.id = :docsId and v.user.id = :userId")
    Optional<NoticeView> findByNoticeIdAndUserId(UUID docsId, UUID userId);
}
