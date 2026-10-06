package com.winitech.system.infrastructure.notice;

import com.winitech.system.domain.notice.Notice;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    @NonNull
    @EntityGraph(attributePaths = {"creator", "updater"})
    Optional<Notice> findById(@NonNull UUID id);

    @Modifying
    @Query("update Notice n set n.status = 'DISABLE' where n.id = :noticeId")
    void softDelete(UUID noticeId);

    @EntityGraph(attributePaths = {"creator", "updater"})
    @Query(value = "SELECT n FROM Notice n " +
            "WHERE n.status = 'ENABLE' " +
            "AND (:title IS NULL OR n.title LIKE %:title%) " +
            "AND (:content IS NULL OR n.content LIKE %:content%) " +
            "AND (:author IS NULL OR n.creator.fullName LIKE %:author%)",
            countQuery = "SELECT COUNT(n) FROM Notice n " +
                    "WHERE n.status = 'ENABLE' " +
                    "AND (:title IS NULL OR n.title LIKE %:title%) " +
                    "AND (:content IS NULL OR n.content LIKE %:content%) " +
                    "AND (:author IS NULL OR n.creator.fullName LIKE %:author%)")
    Page<Notice> getNoticeList(Pageable pageable, String title, String content, String author);
}
