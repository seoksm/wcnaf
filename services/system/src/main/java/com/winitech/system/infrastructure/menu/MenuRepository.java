package com.winitech.system.infrastructure.menu;

import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.organization.Organization;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.menu
 * └ MenuRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:49
 **/
public interface MenuRepository extends JpaRepository<Menu, UUID> {
	List<Menu> findAllBySystemStatus(Menu.SystemStatus systemStatus);

	Optional<Menu> findByMenuCodeAndSystemStatus(String menuCode, Menu.SystemStatus systemStatus);

	boolean existsByMenuCodeAndIdNotAndSystemStatus(String menuCode, UUID id, Menu.SystemStatus systemStatus);

	@EntityGraph(attributePaths = {"program", "childrenMenu", "childrenMenu.program"})
	List<Menu> findAllBySystemStatus(Menu.SystemStatus systemStatus, Sort sort);

	@EntityGraph(attributePaths = {"childrenMenu"})
	List<Menu> findAllByIdAndSystemStatus(UUID id, Menu.SystemStatus systemStatus);

	@Modifying(clearAutomatically = true)
	@Query("" +
			"UPDATE Menu m " +
			"   SET m.parentMenu = :parentMenu, " +
			"       m.sortSeq = :sortSeq " +
			" WHERE m.id = :id" +
			"   AND m.systemStatus = 'ENABLE'")
	int updateParentAndSortSeq(@Param("id") UUID id, @Param("parentMenu") Menu parentMenu, @Param("sortSeq") Integer sortSeq);
	
	boolean existsByProgramIdAndSystemStatus(UUID programId, Menu.SystemStatus systemStatus);

	@Query("select m from Menu m where m.id IN ?1 and m.systemStatus = ?2")
	List<Menu> findAllByIdAndSystemStatus(List<UUID> menuIdList, Menu.SystemStatus systemStatus);

	Optional<Menu> findByIdAndSystemStatus(UUID menuId, Menu.SystemStatus systemStatus);

	@Modifying
	@Query("" +
			"UPDATE Menu m " +
			"   SET m.program = NULL " +
			" WHERE m.program.id = :programId" +
			"   AND m.systemStatus = 'DISABLE'")
	void unlinkDeletedMenuWithProgramId(@Param("programId") UUID programId);
}
