package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * keyword/status 둘 다 선택 필터라, IntangibleAssetRepository와 동일한 이유로 조합별 쿼리를
 * 나눈다 - PostgreSQL이 {@code :param IS NULL} 단독 위치의 파라미터 타입을 추론하지 못해
 * 실패하는 문제(42P18)를 원천 차단한다.
 */
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("select t from Ticket t where t.status <> 'DONE' or t.completedAt >= :doneSince")
    List<Ticket> findKanbanBoard(OffsetDateTime doneSince);

    List<Ticket> findAllByRequestedByOrderByCreateAtDesc(UUID requestedBy);

    @Query(
            value = "select t from Ticket t",
            countQuery = "select count(t) from Ticket t"
    )
    Page<Ticket> findAllTickets(Pageable pageable);

    @Query(
            value = "select t from Ticket t where t.title like %:keyword%",
            countQuery = "select count(t) from Ticket t where t.title like %:keyword%"
    )
    Page<Ticket> findAllByKeyword(String keyword, Pageable pageable);

    @Query(
            value = "select t from Ticket t where t.status = :status",
            countQuery = "select count(t) from Ticket t where t.status = :status"
    )
    Page<Ticket> findAllByStatus(Ticket.Status status, Pageable pageable);

    @Query(
            value = "select t from Ticket t where t.title like %:keyword% and t.status = :status",
            countQuery = "select count(t) from Ticket t where t.title like %:keyword% and t.status = :status"
    )
    Page<Ticket> findAllByKeywordAndStatus(String keyword, Ticket.Status status, Pageable pageable);

    /** S-700 티켓 현황 KPI - 접수대기(WAITING+RECEIVED)/처리중(IN_PROGRESS) 등 상태 묶음별 건수 */
    long countByStatusIn(Collection<Ticket.Status> statuses);

    /** S-700 티켓 현황 KPI - Q-51 SLA 초과(완료 전이면서 목표일이 지남) */
    @Query("select count(t) from Ticket t where t.status <> 'DONE' and t.targetDueDate < :today")
    long countOverdue(LocalDate today);

    /** S-700 티켓 현황 KPI - 이번 주 완료 건수 */
    @Query("select count(t) from Ticket t where t.status = 'DONE' and t.completedAt >= :since")
    long countCompletedSince(OffsetDateTime since);
}
