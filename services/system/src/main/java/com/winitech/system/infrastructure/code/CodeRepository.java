package com.winitech.system.infrastructure.code;

import com.winitech.system.domain.code.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CodeRepository extends JpaRepository<Code, UUID> {

    @Modifying
    @Query("update Code c set c.status = 'DISABLE' where c.id = :codeId")
    void softDelete(UUID codeId);

    @Query("select c from Code c " +
            "where c.parent.id = :parentId " +
            "and (:keyword IS NULL OR c.name LIKE %:keyword%) " +
            "and c.status = 'ENABLE' " +
            "order by c.orderNo asc")
    List<Code> findAllByParentIdAndContainsKeyword(UUID parentId, String keyword);

    @Query("SELECT c FROM Code c " +
            "WHERE c.depthNo = 1 " +
            "AND (:keyword IS NULL OR c.name LIKE %:keyword%) " +
            "AND c.status = 'ENABLE' " +
            "ORDER BY c.orderNo ASC")
    List<Code> findLevel1Code(String keyword);
}
