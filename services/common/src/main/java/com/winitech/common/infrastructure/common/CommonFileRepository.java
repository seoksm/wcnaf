package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonFileRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:49
 **/
public interface CommonFileRepository extends JpaRepository<CommonFile, UUID> {
	@Query("" +
			"SELECT commonFile" +
			"  FROM CommonFile commonFile" +
			" WHERE commonFile.id IN (:idList)" +
			"   AND commonFile.status = :status" +
			" ORDER BY commonFile.sortSeq, commonFile.fileName, commonFile.createAt") 
	List<CommonFile> findByIdListAndStatus(@Param("idList") List<UUID> idList, @Param("status") CommonFile.Status status);

	@Query("" +
			"SELECT commonFile" +
			"  FROM CommonFile commonFile" +
			" WHERE commonFile.entityName = :entityName" +
			"   AND commonFile.entityId = :entityId" +
			"   AND ((:subKey IS NULL AND commonFile.subKey IS NULL) OR commonFile.subKey = :subKey)" +
			"   AND commonFile.status = :status" +
			" ORDER BY commonFile.sortSeq, commonFile.fileName, commonFile.createAt")
	List<CommonFile> findByEntityAndStatus(@Param("entityName") String entityName, @Param("entityId") UUID entityId, @Param("subKey") String subKey, @Param("status") CommonFile.Status status);

	@Query("" +
			"SELECT commonFile" +
			"  FROM CommonFile commonFile" +
			" WHERE commonFile.entityName = :entityName" +
			"   AND commonFile.entityId IN (:entityIdList)" +
			"   AND ((:subKey IS NULL AND commonFile.subKey IS NULL) OR commonFile.subKey = :subKey)" +
			"   AND commonFile.status = :status" +
			" ORDER BY commonFile.sortSeq, commonFile.fileName, commonFile.createAt")
	List<CommonFile> findByEntityListAndStatus(@Param("entityName") String entityName, @Param("entityIdList") List<UUID> entityIdList, @Param("subKey") String subKey, @Param("status") CommonFile.Status status);
}
