ALTER TABLE inventory_result
    ADD COLUMN photo_file_id     uuid,
    ADD COLUMN photo_captured_at timestamp without time zone,
    ADD COLUMN photo_uploaded_at timestamp without time zone;

COMMENT ON COLUMN inventory_result.photo_file_id IS 'S-311 라벨 없음/훼손 예외 경로 - 스캔 없이 촬영한 사진(commonFile 서비스의 fileId). 이 값이 있으면 검수자 승인이 강제된다(status는 항상 PENDING_APPROVAL)';
COMMENT ON COLUMN inventory_result.photo_captured_at IS 'C-201: 브라우저가 촬영 결과 파일을 받은 시각(클라이언트 기준) - 일부 브라우저가 capture=environment를 무시할 수 있어 업로드 시각과 함께 사후 판별 근거로 남긴다';
COMMENT ON COLUMN inventory_result.photo_uploaded_at IS 'C-201: 실제 업로드(S3 PUT) 완료 시각(클라이언트 기준)';
