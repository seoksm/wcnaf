-- Q-16/D8: 자산별 활성 배정(released_at IS NULL)이 동시에 여러 건 생길 수 있던 문제를 DB에서
-- 원천 차단한다. 지금까지는 일반 partial index(idx_asset_assignment_tangible_asset_current)만
-- 있어 "현재 배정 조회"는 빨랐지만 유일성을 보장하지는 않았다.
--
-- 먼저 이미 중복이 있는지 확인하고, 있다면 이 마이그레이션 자체를 실패시켜 운영팀이 수동으로
-- 보정하게 한다 - 어떤 행을 지우거나 임의로 종료 처리하는 판단을 마이그레이션이 대신하지 않는다.
DO $$
DECLARE
    dup_count integer;
    dup_assets text;
BEGIN
    SELECT COUNT(*) INTO dup_count
    FROM (
        SELECT tangible_asset_id
        FROM asset_assignment
        WHERE released_at IS NULL
        GROUP BY tangible_asset_id
        HAVING COUNT(*) > 1
    ) dups;

    IF dup_count > 0 THEN
        SELECT string_agg(tangible_asset_id::text || '(' || cnt::text || '건)', ', ')
        INTO dup_assets
        FROM (
            SELECT tangible_asset_id, COUNT(*) AS cnt
            FROM asset_assignment
            WHERE released_at IS NULL
            GROUP BY tangible_asset_id
            HAVING COUNT(*) > 1
            ORDER BY COUNT(*) DESC
            LIMIT 20
        ) t;

        RAISE EXCEPTION '자산별 활성 배정(released_at IS NULL) 중복이 %건 발견되었습니다. 유니크 인덱스 적용 전 수동 보정이 필요합니다(최대 20건 표시): %',
            dup_count, dup_assets;
    END IF;
END $$;

DROP INDEX idx_asset_assignment_tangible_asset_current;

CREATE UNIQUE INDEX idx_asset_assignment_tangible_asset_active ON asset_assignment (tangible_asset_id) WHERE released_at IS NULL;

COMMENT ON INDEX idx_asset_assignment_tangible_asset_active IS 'D8: 자산별 활성 배정(released_at IS NULL)은 항상 최대 1건 - DB에서 강제';

-- 낙관적 잠금(JPA @Version) - 배정 변경/회수/불용 전환 등 tangible_asset을 갱신하는 모든 경로가
-- 공유하는 버전 카운터. 두 요청이 같은 자산을 동시에 수정하면, 먼저 커밋한 쪽이 버전을 올리고
-- 나중 요청은 버전 불일치로 실패한다(ObjectOptimisticLockingFailureException).
ALTER TABLE tangible_asset ADD COLUMN version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN tangible_asset.version IS '낙관적 잠금 버전 - 동시 수정 충돌 감지(JPA @Version)';
