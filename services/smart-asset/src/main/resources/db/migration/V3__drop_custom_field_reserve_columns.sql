-- Q-13 재확정 (2026-09-16): 사용자정의 필드(custom field) 관리 기능 자체를 만들지 않기로 최종 결정.
-- V2에서 만든 예비 컬럼은 백엔드 엔티티·프론트엔드 어디에도 매핑/참조가 없어 안전하게 제거한다.
-- 향후 추가 정보가 필요해지면 이 예비 컬럼을 되살리지 않고, 그때 필요한 항목을 정식 컬럼으로 변경 설계한다.
ALTER TABLE tangible_asset
    DROP COLUMN ext_txt1, DROP COLUMN ext_txt2, DROP COLUMN ext_txt3, DROP COLUMN ext_txt4, DROP COLUMN ext_txt5,
    DROP COLUMN ext_txt6, DROP COLUMN ext_txt7, DROP COLUMN ext_txt8, DROP COLUMN ext_txt9, DROP COLUMN ext_txt10,
    DROP COLUMN ext_num1, DROP COLUMN ext_num2, DROP COLUMN ext_num3, DROP COLUMN ext_num4, DROP COLUMN ext_num5,
    DROP COLUMN ext_dt1, DROP COLUMN ext_dt2, DROP COLUMN ext_dt3;
