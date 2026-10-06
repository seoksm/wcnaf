package com.winitech.smartAsset.infrastructure.software;

import com.winitech.smartAsset.domain.software.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoftwareRepository extends JpaRepository<Software, UUID> {

    Optional<Software> findByNameAndStatus(String name, Software.Status status);

    @Query("select s from Software s where s.status = 'ENABLE' " +
            "and (:keyword IS NULL OR s.name LIKE %:keyword%) order by s.name asc")
    List<Software> findAllByContainsKeyword(String keyword);
}
