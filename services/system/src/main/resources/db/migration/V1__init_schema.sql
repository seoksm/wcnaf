CREATE TABLE authorization_group (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    admin_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    group_code character varying(255),
    group_name character varying(255),
    remark character varying(255),
    status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    parent_authorization_group_id uuid,
    CONSTRAINT authorization_group_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE authorization_group IS '인가그룹';
COMMENT ON COLUMN authorization_group.id IS '인가그룹 ID';
COMMENT ON COLUMN authorization_group.create_at IS '생성일시';
COMMENT ON COLUMN authorization_group.update_at IS '수정일시';
COMMENT ON COLUMN authorization_group.admin_status IS '관리자 상태 (ENABLE/DISABLE)';
COMMENT ON COLUMN authorization_group.group_code IS '그룹 코드';
COMMENT ON COLUMN authorization_group.group_name IS '그룹명';
COMMENT ON COLUMN authorization_group.remark IS '비고';
COMMENT ON COLUMN authorization_group.status IS '상태 (ENABLE/DISABLE)';
COMMENT ON COLUMN authorization_group.system_status IS '시스템 상태 (ENABLE/DISABLE)';
COMMENT ON COLUMN authorization_group.parent_authorization_group_id IS '상위 인가그룹 ID';

CREATE TABLE authorization_group_user (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    authorization_group_id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT authorization_group_user_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE authorization_group_user IS '인가그룹유저';
COMMENT ON COLUMN authorization_group_user.id IS '인가그룹유저 ID';
COMMENT ON COLUMN authorization_group_user.create_at IS '생성일시';
COMMENT ON COLUMN authorization_group_user.update_at IS '수정일시';
COMMENT ON COLUMN authorization_group_user.authorization_group_id IS '인가그룹 ID';
COMMENT ON COLUMN authorization_group_user.user_id IS '사용자 ID';

CREATE TABLE code (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    code character varying(255),
    depth_no integer,
    description character varying(255),
    name character varying(255),
    order_no integer,
    status character varying(255),
    use_status character varying(255),
    parent_id uuid,
    CONSTRAINT code_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE code IS '코드';
COMMENT ON COLUMN code.id IS '코드 ID';
COMMENT ON COLUMN code.create_at IS '생성일시';
COMMENT ON COLUMN code.update_at IS '수정일시';
COMMENT ON COLUMN code.code IS '코드값';
COMMENT ON COLUMN code.depth_no IS '깊이 번호';
COMMENT ON COLUMN code.description IS '설명';
COMMENT ON COLUMN code.name IS '코드명';
COMMENT ON COLUMN code.order_no IS '정렬 순서';
COMMENT ON COLUMN code.status IS '상태';
COMMENT ON COLUMN code.use_status IS '사용 상태';
COMMENT ON COLUMN code.parent_id IS '상위 코드 ID';

CREATE TABLE department (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    department_code character varying(255),
    department_name character varying(255),
    sort_seq integer,
    status character varying(255) DEFAULT 'ENABLE'::character varying,
    system_status character varying(255),
    parent_department_id uuid,
    CONSTRAINT department_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE department IS '부서';
COMMENT ON COLUMN department.id IS '부서 ID';
COMMENT ON COLUMN department.create_at IS '생성일시';
COMMENT ON COLUMN department.update_at IS '수정일시';
COMMENT ON COLUMN department.department_code IS '부서 코드';
COMMENT ON COLUMN department.department_name IS '부서명';
COMMENT ON COLUMN department.sort_seq IS '정렬 순서';
COMMENT ON COLUMN department.status IS '상태';
COMMENT ON COLUMN department.system_status IS '시스템 상태';
COMMENT ON COLUMN department.parent_department_id IS '상위 부서 ID';

CREATE TABLE login_log (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    err_cnt integer,
    lock_until timestamp without time zone,
    login_ip character varying(255),
    login_log_status character varying(255),
    user_session_id uuid,
    username character varying(255),
    CONSTRAINT login_log_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE login_log IS '로그인 이력';
COMMENT ON COLUMN login_log.id IS '로그인 이력 ID';
COMMENT ON COLUMN login_log.create_at IS '생성일시';
COMMENT ON COLUMN login_log.update_at IS '수정일시';
COMMENT ON COLUMN login_log.err_cnt IS '오류 횟수';
COMMENT ON COLUMN login_log.lock_until IS '잠금 종료 일시';
COMMENT ON COLUMN login_log.login_ip IS '로그인 IP';
COMMENT ON COLUMN login_log.login_log_status IS '로그인 이력 상태';
COMMENT ON COLUMN login_log.user_session_id IS '사용자 세션 ID';
COMMENT ON COLUMN login_log.username IS '사용자명';

CREATE TABLE menu (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    menu_code character varying(255),
    menu_mapping character varying(255),
    menu_name character varying(255) NOT NULL,
    menu_status character varying(10) NOT NULL,
    menu_type character varying(10) NOT NULL,
    sort_seq integer,
    status character varying(10) NOT NULL,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    parent_menu_id uuid,
    program_id uuid,
    CONSTRAINT menu_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE menu IS '메뉴';
COMMENT ON COLUMN menu.id IS '메뉴 ID';
COMMENT ON COLUMN menu.create_at IS '생성일시';
COMMENT ON COLUMN menu.update_at IS '수정일시';
COMMENT ON COLUMN menu.menu_code IS '메뉴 코드';
COMMENT ON COLUMN menu.menu_mapping IS '메뉴 매핑';
COMMENT ON COLUMN menu.menu_name IS '메뉴명';
COMMENT ON COLUMN menu.menu_status IS '메뉴 상태';
COMMENT ON COLUMN menu.menu_type IS '메뉴 유형';
COMMENT ON COLUMN menu.sort_seq IS '정렬 순서';
COMMENT ON COLUMN menu.status IS '상태';
COMMENT ON COLUMN menu.system_status IS '시스템 상태';
COMMENT ON COLUMN menu.parent_menu_id IS '상위 메뉴 ID';
COMMENT ON COLUMN menu.program_id IS '프로그램 ID';

CREATE TABLE menu_permission (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    custom1status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    custom2status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    custom3status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    delete_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    down_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    insert_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    manage_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    print_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    select_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    update_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    authorization_group_id uuid NOT NULL,
    menu_id uuid NOT NULL,
    CONSTRAINT menu_permission_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE menu_permission IS '메뉴 권한';
COMMENT ON COLUMN menu_permission.id IS '메뉴 권한 ID';
COMMENT ON COLUMN menu_permission.create_at IS '생성일시';
COMMENT ON COLUMN menu_permission.update_at IS '수정일시';
COMMENT ON COLUMN menu_permission.custom1status IS '커스텀1 권한 상태';
COMMENT ON COLUMN menu_permission.custom2status IS '커스텀2 권한 상태';
COMMENT ON COLUMN menu_permission.custom3status IS '커스텀3 권한 상태';
COMMENT ON COLUMN menu_permission.delete_status IS '삭제 권한 상태';
COMMENT ON COLUMN menu_permission.down_status IS '다운로드 권한 상태';
COMMENT ON COLUMN menu_permission.insert_status IS '등록 권한 상태';
COMMENT ON COLUMN menu_permission.manage_status IS '관리 권한 상태';
COMMENT ON COLUMN menu_permission.print_status IS '출력 권한 상태';
COMMENT ON COLUMN menu_permission.select_status IS '조회 권한 상태';
COMMENT ON COLUMN menu_permission.update_status IS '수정 권한 상태';
COMMENT ON COLUMN menu_permission.authorization_group_id IS '인가그룹 ID';
COMMENT ON COLUMN menu_permission.menu_id IS '메뉴 ID';

CREATE TABLE notice (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    content text,
    end_date timestamp without time zone,
    notice_status character varying(255),
    pw character varying(255),
    start_date timestamp without time zone,
    status character varying(255),
    title character varying(255),
    use_status character varying(255),
    view_count integer,
    visibility_status character varying(255),
    creator_id uuid,
    updater_id uuid,
    CONSTRAINT notice_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE notice IS '공지사항';
COMMENT ON COLUMN notice.id IS '공지사항 ID';
COMMENT ON COLUMN notice.create_at IS '생성일시';
COMMENT ON COLUMN notice.update_at IS '수정일시';
COMMENT ON COLUMN notice.content IS '내용';
COMMENT ON COLUMN notice.end_date IS '종료일';
COMMENT ON COLUMN notice.notice_status IS '공지사항 상태';
COMMENT ON COLUMN notice.pw IS '비밀번호';
COMMENT ON COLUMN notice.start_date IS '시작일';
COMMENT ON COLUMN notice.status IS '상태';
COMMENT ON COLUMN notice.title IS '제목';
COMMENT ON COLUMN notice.use_status IS '사용 상태';
COMMENT ON COLUMN notice.view_count IS '조회수';
COMMENT ON COLUMN notice.visibility_status IS '공개 상태';
COMMENT ON COLUMN notice.creator_id IS '작성자 ID';
COMMENT ON COLUMN notice.updater_id IS '수정자 ID';

CREATE TABLE notice_views (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    notice_id uuid,
    user_id uuid,
    CONSTRAINT notice_views_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE notice_views IS '공지사항 조회수';
COMMENT ON COLUMN notice_views.id IS '공지사항 조회수 ID';
COMMENT ON COLUMN notice_views.create_at IS '생성일시';
COMMENT ON COLUMN notice_views.update_at IS '수정일시';
COMMENT ON COLUMN notice_views.notice_id IS '공지사항 ID';
COMMENT ON COLUMN notice_views.user_id IS '사용자 ID';

CREATE TABLE organization (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    organization_code character varying(255),
    organization_name character varying(255),
    national_code character varying(2),
    approval_status character varying(255),
    organization_address character varying(255),
    organization_contact character varying(255),
    representative_id uuid,
    database_host character varying(255),
    database_message character varying(255),
    database_port integer,
    tenant_schema_version integer,
    tenant_setup_status character varying(255),
    tenant_status character varying(255),
    system_status character varying(255),
    CONSTRAINT organization_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE organization IS '조직 (기관)';
COMMENT ON COLUMN organization.id IS '기관 ID';
COMMENT ON COLUMN organization.create_at IS '생성일시';
COMMENT ON COLUMN organization.update_at IS '수정일시';
COMMENT ON COLUMN organization.organization_code IS '기관 코드';
COMMENT ON COLUMN organization.organization_name IS '기관명';
COMMENT ON COLUMN organization.national_code IS '국가 코드';
COMMENT ON COLUMN organization.approval_status IS '승인 상태';
COMMENT ON COLUMN organization.organization_address IS '기관 주소';
COMMENT ON COLUMN organization.organization_contact IS '기관 연락처';
COMMENT ON COLUMN organization.representative_id IS '대표자 ID';
COMMENT ON COLUMN organization.database_host IS '데이터베이스 호스트';
COMMENT ON COLUMN organization.database_message IS '데이터베이스 메시지';
COMMENT ON COLUMN organization.database_port IS '데이터베이스 포트';
COMMENT ON COLUMN organization.tenant_schema_version IS '테넌트 스키마 버전';
COMMENT ON COLUMN organization.tenant_setup_status IS '테넌트 설정 상태';
COMMENT ON COLUMN organization.tenant_status IS '테넌트 상태';
COMMENT ON COLUMN organization.system_status IS '시스템 상태';

CREATE TABLE program (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    menu_status character varying(10),
    mobile_status character varying(10),
    program_code character varying(255),
    program_mapping character varying(255),
    program_mapping_status character varying(10),
    program_name character varying(255),
    remark character varying(255),
    status character varying(10),
    parent_program_id uuid,
    CONSTRAINT program_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE program IS '프로그램';
COMMENT ON COLUMN program.id IS '프로그램 ID';
COMMENT ON COLUMN program.create_at IS '생성일시';
COMMENT ON COLUMN program.update_at IS '수정일시';
COMMENT ON COLUMN program.menu_status IS '메뉴 상태';
COMMENT ON COLUMN program.mobile_status IS '모바일 상태';
COMMENT ON COLUMN program.program_code IS '프로그램 코드';
COMMENT ON COLUMN program.program_mapping IS '프로그램 매핑';
COMMENT ON COLUMN program.program_mapping_status IS '프로그램 매핑 상태';
COMMENT ON COLUMN program.program_name IS '프로그램명';
COMMENT ON COLUMN program.remark IS '비고';
COMMENT ON COLUMN program.status IS '상태';
COMMENT ON COLUMN program.parent_program_id IS '상위 프로그램 ID';

CREATE TABLE program_action (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    action_type character varying(10) NOT NULL,
    auth_type character varying(10) NOT NULL,
    uri character varying(255) NOT NULL,
    program_id uuid,
    CONSTRAINT program_action_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE program_action IS '프로그램 동작 (액션)';
COMMENT ON COLUMN program_action.id IS '프로그램 동작 ID';
COMMENT ON COLUMN program_action.create_at IS '생성일시';
COMMENT ON COLUMN program_action.update_at IS '수정일시';
COMMENT ON COLUMN program_action.action_type IS '액션 유형';
COMMENT ON COLUMN program_action.auth_type IS '인증 유형';
COMMENT ON COLUMN program_action.uri IS 'URI';
COMMENT ON COLUMN program_action.program_id IS '프로그램 ID';

CREATE TABLE program_rel (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    program_id uuid,
    rel_program_id uuid,
    CONSTRAINT program_rel_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE program_rel IS '프로그램 관계';
COMMENT ON COLUMN program_rel.id IS '프로그램 관계 ID';
COMMENT ON COLUMN program_rel.create_at IS '생성일시';
COMMENT ON COLUMN program_rel.update_at IS '수정일시';
COMMENT ON COLUMN program_rel.program_id IS '프로그램 ID';
COMMENT ON COLUMN program_rel.rel_program_id IS '관련 프로그램 ID';

CREATE TABLE user_organization (
    organization_id uuid NOT NULL,
    user_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    system_status character varying(255),
    user_group_id bigint,
    CONSTRAINT user_organization_pkey PRIMARY KEY (organization_id, user_id)
);
COMMENT ON TABLE user_organization IS '사용자소속기관';
COMMENT ON COLUMN user_organization.organization_id IS '기관 ID';
COMMENT ON COLUMN user_organization.user_id IS '사용자 ID';
COMMENT ON COLUMN user_organization.create_at IS '생성일시';
COMMENT ON COLUMN user_organization.update_at IS '수정일시';
COMMENT ON COLUMN user_organization.system_status IS '시스템 상태';
COMMENT ON COLUMN user_organization.user_group_id IS '사용자그룹 ID';

CREATE TABLE user_password_log (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    hashed_password character varying(255),
    password_change_ip character varying(255),
    password_changed_at timestamp without time zone,
    user_id uuid,
    CONSTRAINT user_password_log_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE user_password_log IS '사용자비밀번호로그';
COMMENT ON COLUMN user_password_log.id IS '사용자비밀번호로그 ID';
COMMENT ON COLUMN user_password_log.create_at IS '생성일시';
COMMENT ON COLUMN user_password_log.update_at IS '수정일시';
COMMENT ON COLUMN user_password_log.hashed_password IS '해시 비밀번호';
COMMENT ON COLUMN user_password_log.password_change_ip IS '비밀번호 변경 IP';
COMMENT ON COLUMN user_password_log.password_changed_at IS '비밀번호 변경 일시';
COMMENT ON COLUMN user_password_log.user_id IS '사용자 ID';

CREATE TABLE user_session (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    ip_security_status character varying(255),
    login_at timestamp without time zone,
    login_ip character varying(255),
    login_status character varying(255),
    logout_at timestamp without time zone,
    verified_code character varying(255),
    verified_token character varying(255),
    verified_token_expires_at timestamp without time zone,
    refresh_cnt integer,
    refresh_token character varying(255),
    refresh_token_expires_at timestamp without time zone,
    refresh_token_updated_at timestamp without time zone,
    total_refresh_cnt integer,
    user_id uuid,
    CONSTRAINT user_session_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE user_session IS '사용자세션';
COMMENT ON COLUMN user_session.id IS '사용자세션 ID';
COMMENT ON COLUMN user_session.create_at IS '생성일시';
COMMENT ON COLUMN user_session.update_at IS '수정일시';
COMMENT ON COLUMN user_session.ip_security_status IS 'IP 보안 상태';
COMMENT ON COLUMN user_session.login_at IS '로그인 일시';
COMMENT ON COLUMN user_session.login_ip IS '로그인 IP';
COMMENT ON COLUMN user_session.login_status IS '로그인 상태';
COMMENT ON COLUMN user_session.logout_at IS '로그아웃 일시';
COMMENT ON COLUMN user_session.verified_code IS '인증 코드';
COMMENT ON COLUMN user_session.verified_token IS '인증 토큰';
COMMENT ON COLUMN user_session.verified_token_expires_at IS '인증 토큰 만료 일시';
COMMENT ON COLUMN user_session.refresh_cnt IS '갱신 횟수';
COMMENT ON COLUMN user_session.refresh_token IS '갱신 토큰';
COMMENT ON COLUMN user_session.refresh_token_expires_at IS '갱신 토큰 만료 일시';
COMMENT ON COLUMN user_session.refresh_token_updated_at IS '갱신 토큰 업데이트 일시';
COMMENT ON COLUMN user_session.total_refresh_cnt IS '총 갱신 횟수';
COMMENT ON COLUMN user_session.user_id IS '사용자 ID';

CREATE TABLE users (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    username character varying(255),
    hashed_password character varying(255),
    email character varying(255),
    phone_number character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    full_name character varying(255),
    department_name character varying(255),
    duty_name character varying(255),
    last_password_changed_at timestamp without time zone,
    employee_no character varying(255),
    second_auth_yn character varying(255),
    join_status character varying(255),
    status character varying(255),
    user_department_id uuid,
    department_id uuid,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE users IS '사용자';
COMMENT ON COLUMN users.id IS '사용자 ID';
COMMENT ON COLUMN users.create_at IS '생성일시';
COMMENT ON COLUMN users.update_at IS '수정일시';
COMMENT ON COLUMN users.username IS '사용자명';
COMMENT ON COLUMN users.hashed_password IS '해시 비밀번호';
COMMENT ON COLUMN users.email IS '이메일';
COMMENT ON COLUMN users.phone_number IS '전화번호';
COMMENT ON COLUMN users.first_name IS '이름';
COMMENT ON COLUMN users.last_name IS '성';
COMMENT ON COLUMN users.full_name IS '전체 이름';
COMMENT ON COLUMN users.department_name IS '부서명';
COMMENT ON COLUMN users.duty_name IS '직책명';
COMMENT ON COLUMN users.last_password_changed_at IS '마지막 비밀번호 변경 일시';
COMMENT ON COLUMN users.employee_no IS '사원번호';
COMMENT ON COLUMN users.second_auth_yn IS '2차 인증 여부';
COMMENT ON COLUMN users.join_status IS '가입 상태';
COMMENT ON COLUMN users.status IS '상태';
COMMENT ON COLUMN users.user_department_id IS '사용자 부서 ID';
COMMENT ON COLUMN users.department_id IS '부서 ID';

CREATE TABLE common_authorization_group_permission (
    authorization_group_id uuid NOT NULL,
    menu_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    custom1status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    custom2status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    custom3status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    delete_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    down_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    group_code varchar(255) NULL,
    insert_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    manage_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    print_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    select_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    update_status character varying(5) DEFAULT 'NONE'::character varying NOT NULL,
    CONSTRAINT common_authorization_group_permission_pkey PRIMARY KEY (authorization_group_id, menu_id)
);
COMMENT ON TABLE common_authorization_group_permission IS '공통 인가그룹 권한';
COMMENT ON COLUMN common_authorization_group_permission.authorization_group_id IS '인가그룹 ID';
COMMENT ON COLUMN common_authorization_group_permission.menu_id IS '메뉴 ID';
COMMENT ON COLUMN common_authorization_group_permission.create_at IS '생성일시';
COMMENT ON COLUMN common_authorization_group_permission.update_at IS '수정일시';
COMMENT ON COLUMN common_authorization_group_permission.custom1status IS '커스텀1 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.custom2status IS '커스텀2 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.custom3status IS '커스텀3 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.delete_status IS '삭제 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.down_status IS '다운로드 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.group_code IS '그룹 코드';
COMMENT ON COLUMN common_authorization_group_permission.insert_status IS '등록 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.manage_status IS '관리 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.print_status IS '출력 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.select_status IS '조회 권한 상태';
COMMENT ON COLUMN common_authorization_group_permission.update_status IS '수정 권한 상태';

CREATE TABLE common_authorization_group_user (
    authorization_group_id uuid NOT NULL,
    user_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    CONSTRAINT common_authorization_group_user_pkey PRIMARY KEY (authorization_group_id, user_id)
);
COMMENT ON TABLE common_authorization_group_user IS '공통 인가그룹 유저';
COMMENT ON COLUMN common_authorization_group_user.authorization_group_id IS '인가그룹 ID';
COMMENT ON COLUMN common_authorization_group_user.user_id IS '사용자 ID';
COMMENT ON COLUMN common_authorization_group_user.create_at IS '생성일시';
COMMENT ON COLUMN common_authorization_group_user.update_at IS '수정일시';

CREATE TABLE common_file (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    disabled_at timestamp without time zone,
    entity_id uuid,
    entity_name character varying(30),
    extra_info character varying(255),
    file_ext character varying(10),
    file_name character varying(255),
    file_origin character varying(20),
    file_size bigint,
    height integer,
    mime_type character varying(255),
    sort_seq integer,
    status character varying(10),
    storage_type character varying(10),
    sub_key character varying(50),
    url character varying(255),
    user_id uuid,
    width integer,
    CONSTRAINT common_file_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_file IS '공통 파일';
COMMENT ON COLUMN common_file.id IS '파일 ID';
COMMENT ON COLUMN common_file.create_at IS '생성일시';
COMMENT ON COLUMN common_file.update_at IS '수정일시';
COMMENT ON COLUMN common_file.disabled_at IS '비활성화 일시';
COMMENT ON COLUMN common_file.entity_id IS '엔티티 ID';
COMMENT ON COLUMN common_file.entity_name IS '엔티티명';
COMMENT ON COLUMN common_file.extra_info IS '추가 정보';
COMMENT ON COLUMN common_file.file_ext IS '파일 확장자';
COMMENT ON COLUMN common_file.file_name IS '파일명';
COMMENT ON COLUMN common_file.file_origin IS '파일 원본';
COMMENT ON COLUMN common_file.file_size IS '파일 크기';
COMMENT ON COLUMN common_file.height IS '높이';
COMMENT ON COLUMN common_file.mime_type IS 'MIME 타입';
COMMENT ON COLUMN common_file.sort_seq IS '정렬 순서';
COMMENT ON COLUMN common_file.status IS '상태';
COMMENT ON COLUMN common_file.storage_type IS '저장소 유형';
COMMENT ON COLUMN common_file.sub_key IS '서브 키';
COMMENT ON COLUMN common_file.url IS 'URL';
COMMENT ON COLUMN common_file.user_id IS '사용자 ID';
COMMENT ON COLUMN common_file.width IS '너비';

CREATE TABLE common_job (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    applied_at timestamp without time zone,
    apply_status character varying(10) DEFAULT 'PENDING'::character varying NOT NULL,
    class_name character varying(255),
    job_type character varying(10) NOT NULL,
    name character varying(255),
    remark character varying(255),
    sql character varying(255),
    status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    common_job_group_id uuid,
    CONSTRAINT common_job_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job IS '공통 작업 (배치)';
COMMENT ON COLUMN common_job.id IS '작업 ID';
COMMENT ON COLUMN common_job.create_at IS '생성일시';
COMMENT ON COLUMN common_job.update_at IS '수정일시';
COMMENT ON COLUMN common_job.applied_at IS '적용 일시';
COMMENT ON COLUMN common_job.apply_status IS '적용 상태';
COMMENT ON COLUMN common_job.class_name IS '클래스명';
COMMENT ON COLUMN common_job.job_type IS '작업 유형';
COMMENT ON COLUMN common_job.name IS '작업명';
COMMENT ON COLUMN common_job.remark IS '비고';
COMMENT ON COLUMN common_job.sql IS 'SQL';
COMMENT ON COLUMN common_job.status IS '상태';
COMMENT ON COLUMN common_job.system_status IS '시스템 상태';
COMMENT ON COLUMN common_job.common_job_group_id IS '작업 그룹 ID';

CREATE TABLE common_job_group (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    name character varying(255),
    remark character varying(255),
    status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    CONSTRAINT common_job_group_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_group IS '공통 작업 그룹';
COMMENT ON COLUMN common_job_group.id IS '작업 그룹 ID';
COMMENT ON COLUMN common_job_group.create_at IS '생성일시';
COMMENT ON COLUMN common_job_group.update_at IS '수정일시';
COMMENT ON COLUMN common_job_group.name IS '작업 그룹명';
COMMENT ON COLUMN common_job_group.remark IS '비고';
COMMENT ON COLUMN common_job_group.status IS '상태';
COMMENT ON COLUMN common_job_group.system_status IS '시스템 상태';

CREATE TABLE common_job_run (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    ended_at timestamp without time zone,
    job_status character varying(10) DEFAULT 'INVALID'::character varying NOT NULL,
    message character varying(255),
    started_at timestamp without time zone,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    common_job_id uuid,
    common_job_trigger_id uuid,
    CONSTRAINT common_job_run_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_run IS '공통 작업 실행 이력';
COMMENT ON COLUMN common_job_run.id IS '작업 실행 ID';
COMMENT ON COLUMN common_job_run.create_at IS '생성일시';
COMMENT ON COLUMN common_job_run.update_at IS '수정일시';
COMMENT ON COLUMN common_job_run.ended_at IS '종료 일시';
COMMENT ON COLUMN common_job_run.job_status IS '작업 상태';
COMMENT ON COLUMN common_job_run.message IS '메시지';
COMMENT ON COLUMN common_job_run.started_at IS '시작 일시';
COMMENT ON COLUMN common_job_run.system_status IS '시스템 상태';
COMMENT ON COLUMN common_job_run.common_job_id IS '작업 ID';
COMMENT ON COLUMN common_job_run.common_job_trigger_id IS '작업 트리거 ID';

CREATE TABLE common_job_state (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    err_cnt integer,
    last_ended_at timestamp without time zone,
    last_failed_at timestamp without time zone,
    last_message character varying(255),
    last_started_at timestamp without time zone,
    last_success_at timestamp without time zone,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    common_job_id uuid,
    current_run_id uuid,
    last_run_id uuid,
    CONSTRAINT common_job_state_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_state IS '공통 작업 상태';
COMMENT ON COLUMN common_job_state.id IS '작업 상태 ID';
COMMENT ON COLUMN common_job_state.create_at IS '생성일시';
COMMENT ON COLUMN common_job_state.update_at IS '수정일시';
COMMENT ON COLUMN common_job_state.err_cnt IS '오류 횟수';
COMMENT ON COLUMN common_job_state.last_ended_at IS '마지막 종료 일시';
COMMENT ON COLUMN common_job_state.last_failed_at IS '마지막 실패 일시';
COMMENT ON COLUMN common_job_state.last_message IS '마지막 메시지';
COMMENT ON COLUMN common_job_state.last_started_at IS '마지막 시작 일시';
COMMENT ON COLUMN common_job_state.last_success_at IS '마지막 성공 일시';
COMMENT ON COLUMN common_job_state.system_status IS '시스템 상태';
COMMENT ON COLUMN common_job_state.common_job_id IS '작업 ID';
COMMENT ON COLUMN common_job_state.current_run_id IS '현재 실행 ID';
COMMENT ON COLUMN common_job_state.last_run_id IS '마지막 실행 ID';

CREATE TABLE common_job_trigger (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    applied_at timestamp without time zone,
    apply_status character varying(10) DEFAULT 'PENDING'::character varying NOT NULL,
    name character varying(255),
    status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    system_status character varying(10) DEFAULT 'ENABLE'::character varying NOT NULL,
    trigger_cron character varying(255),
    trigger_seconds bigint,
    trigger_type character varying(10) NOT NULL,
    common_job_id uuid,
    CONSTRAINT common_job_trigger_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_trigger IS '공통 작업 트리거';
COMMENT ON COLUMN common_job_trigger.id IS '작업 트리거 ID';
COMMENT ON COLUMN common_job_trigger.create_at IS '생성일시';
COMMENT ON COLUMN common_job_trigger.update_at IS '수정일시';
COMMENT ON COLUMN common_job_trigger.applied_at IS '적용 일시';
COMMENT ON COLUMN common_job_trigger.apply_status IS '적용 상태';
COMMENT ON COLUMN common_job_trigger.name IS '트리거명';
COMMENT ON COLUMN common_job_trigger.status IS '상태';
COMMENT ON COLUMN common_job_trigger.system_status IS '시스템 상태';
COMMENT ON COLUMN common_job_trigger.trigger_cron IS '트리거 CRON';
COMMENT ON COLUMN common_job_trigger.trigger_seconds IS '트리거 초';
COMMENT ON COLUMN common_job_trigger.trigger_type IS '트리거 유형';
COMMENT ON COLUMN common_job_trigger.common_job_id IS '작업 ID';

CREATE TABLE common_menu_action (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    action_type character varying(255),
    auth_type character varying(255),
    menu_id uuid,
    program_code character varying(255),
    program_id uuid,
    uri character varying(255),
    CONSTRAINT common_menu_action_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_menu_action IS '공통 메뉴 동작';
COMMENT ON COLUMN common_menu_action.id IS '메뉴 동작 ID';
COMMENT ON COLUMN common_menu_action.create_at IS '생성일시';
COMMENT ON COLUMN common_menu_action.update_at IS '수정일시';
COMMENT ON COLUMN common_menu_action.action_type IS '액션 유형';
COMMENT ON COLUMN common_menu_action.auth_type IS '인증 유형';
COMMENT ON COLUMN common_menu_action.menu_id IS '메뉴 ID';
COMMENT ON COLUMN common_menu_action.program_code IS '프로그램 코드';
COMMENT ON COLUMN common_menu_action.program_id IS '프로그램 ID';
COMMENT ON COLUMN common_menu_action.uri IS 'URI';


CREATE TABLE common_public_key (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    encrypt_key character varying(255),
    key_gen_date timestamp without time zone,
    private_key character varying(4000),
    public_key character varying(1000),
    CONSTRAINT common_public_key_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_public_key IS '공통 공개키';
COMMENT ON COLUMN common_public_key.id IS '공개키 ID';
COMMENT ON COLUMN common_public_key.create_at IS '생성일시';
COMMENT ON COLUMN common_public_key.update_at IS '수정일시';
COMMENT ON COLUMN common_public_key.encrypt_key IS '암호화 키';
COMMENT ON COLUMN common_public_key.key_gen_date IS '키 생성일';
COMMENT ON COLUMN common_public_key.private_key IS '개인키';
COMMENT ON COLUMN common_public_key.public_key IS '공개키';


--
-- TOC entry 236 (class 1259 OID 45906)
-- Name: common_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE common_user (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    department_name character varying(255),
    duty_name character varying(255),
    email character varying(255),
    employee_no character varying(255),
    first_name character varying(255),
    full_name character varying(255),
    last_name character varying(255),
    phone_number character varying(255),
    status character varying(255),
    username character varying(255),
    CONSTRAINT common_user_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_user IS '공통 사용자';
COMMENT ON COLUMN common_user.id IS '사용자 ID';
COMMENT ON COLUMN common_user.create_at IS '생성일시';
COMMENT ON COLUMN common_user.update_at IS '수정일시';
COMMENT ON COLUMN common_user.department_name IS '부서명';
COMMENT ON COLUMN common_user.duty_name IS '직책명';
COMMENT ON COLUMN common_user.email IS '이메일';
COMMENT ON COLUMN common_user.employee_no IS '사원번호';
COMMENT ON COLUMN common_user.first_name IS '이름';
COMMENT ON COLUMN common_user.full_name IS '전체 이름';
COMMENT ON COLUMN common_user.last_name IS '성';
COMMENT ON COLUMN common_user.phone_number IS '전화번호';
COMMENT ON COLUMN common_user.status IS '상태';
COMMENT ON COLUMN common_user.username IS '사용자명';



CREATE TABLE qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea,
    CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_blob_triggers IS 'Quartz BLOB 트리거';
COMMENT ON COLUMN qrtz_blob_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_blob_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_blob_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_blob_triggers.blob_data IS 'BLOB 데이터';


CREATE TABLE qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL,
    CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name)
);
COMMENT ON TABLE qrtz_calendars IS 'Quartz 캘린더';
COMMENT ON COLUMN qrtz_calendars.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_calendars.calendar_name IS '캘린더명';
COMMENT ON COLUMN qrtz_calendars.calendar IS '캘린더 데이터';


CREATE TABLE qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80),
    CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_cron_triggers IS 'Quartz Cron 트리거';
COMMENT ON COLUMN qrtz_cron_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_cron_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_cron_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_cron_triggers.cron_expression IS 'Cron 표현식';
COMMENT ON COLUMN qrtz_cron_triggers.time_zone_id IS '타임존 ID';


CREATE TABLE qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent boolean,
    requests_recovery boolean,
    CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id)
);
COMMENT ON TABLE qrtz_fired_triggers IS 'Quartz 실행된 트리거';
COMMENT ON COLUMN qrtz_fired_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_fired_triggers.entry_id IS '엔트리 ID';
COMMENT ON COLUMN qrtz_fired_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_fired_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_fired_triggers.instance_name IS '인스턴스명';
COMMENT ON COLUMN qrtz_fired_triggers.fired_time IS '실행 시간';
COMMENT ON COLUMN qrtz_fired_triggers.sched_time IS '스케줄 시간';
COMMENT ON COLUMN qrtz_fired_triggers.priority IS '우선순위';
COMMENT ON COLUMN qrtz_fired_triggers.state IS '상태';
COMMENT ON COLUMN qrtz_fired_triggers.job_name IS '작업명';
COMMENT ON COLUMN qrtz_fired_triggers.job_group IS '작업 그룹';
COMMENT ON COLUMN qrtz_fired_triggers.is_nonconcurrent IS '비동시 실행 여부';
COMMENT ON COLUMN qrtz_fired_triggers.requests_recovery IS '복구 요청 여부';


CREATE TABLE qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable boolean NOT NULL,
    is_nonconcurrent boolean NOT NULL,
    is_update_data boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea,
    CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group)
);
COMMENT ON TABLE qrtz_job_details IS 'Quartz 작업 상세';
COMMENT ON COLUMN qrtz_job_details.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_job_details.job_name IS '작업명';
COMMENT ON COLUMN qrtz_job_details.job_group IS '작업 그룹';
COMMENT ON COLUMN qrtz_job_details.description IS '설명';
COMMENT ON COLUMN qrtz_job_details.job_class_name IS '작업 클래스명';
COMMENT ON COLUMN qrtz_job_details.is_durable IS '영구 보관 여부';
COMMENT ON COLUMN qrtz_job_details.is_nonconcurrent IS '비동시 실행 여부';
COMMENT ON COLUMN qrtz_job_details.is_update_data IS '데이터 업데이트 여부';
COMMENT ON COLUMN qrtz_job_details.requests_recovery IS '복구 요청 여부';
COMMENT ON COLUMN qrtz_job_details.job_data IS '작업 데이터';


CREATE TABLE qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL,
    CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name)
);
COMMENT ON TABLE qrtz_locks IS 'Quartz 잠금(Locks)';
COMMENT ON COLUMN qrtz_locks.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_locks.lock_name IS '잠금명';


CREATE TABLE qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group)
);
COMMENT ON TABLE qrtz_paused_trigger_grps IS 'Quartz 중지된 트리거 그룹';
COMMENT ON COLUMN qrtz_paused_trigger_grps.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_paused_trigger_grps.trigger_group IS '트리거 그룹';


CREATE TABLE qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL,
    CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name)
);
COMMENT ON TABLE qrtz_scheduler_state IS 'Quartz 스케줄러 상태';
COMMENT ON COLUMN qrtz_scheduler_state.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_scheduler_state.instance_name IS '인스턴스명';
COMMENT ON COLUMN qrtz_scheduler_state.last_checkin_time IS '마지막 체크인 시간';
COMMENT ON COLUMN qrtz_scheduler_state.checkin_interval IS '체크인 간격';


CREATE TABLE qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL,
    CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_simple_triggers IS 'Quartz 단순 트리거';
COMMENT ON COLUMN qrtz_simple_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_simple_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_simple_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_simple_triggers.repeat_count IS '반복 횟수';
COMMENT ON COLUMN qrtz_simple_triggers.repeat_interval IS '반복 간격';
COMMENT ON COLUMN qrtz_simple_triggers.times_triggered IS '실행된 횟수';


CREATE TABLE qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean,
    CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_simprop_triggers IS 'Quartz Simprop 트리거';
COMMENT ON COLUMN qrtz_simprop_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_simprop_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_simprop_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_simprop_triggers.str_prop_1 IS '문자열 속성 1';
COMMENT ON COLUMN qrtz_simprop_triggers.str_prop_2 IS '문자열 속성 2';
COMMENT ON COLUMN qrtz_simprop_triggers.str_prop_3 IS '문자열 속성 3';
COMMENT ON COLUMN qrtz_simprop_triggers.int_prop_1 IS '정수 속성 1';
COMMENT ON COLUMN qrtz_simprop_triggers.int_prop_2 IS '정수 속성 2';
COMMENT ON COLUMN qrtz_simprop_triggers.long_prop_1 IS '롱 속성 1';
COMMENT ON COLUMN qrtz_simprop_triggers.long_prop_2 IS '롱 속성 2';
COMMENT ON COLUMN qrtz_simprop_triggers.dec_prop_1 IS '소수 속성 1';
COMMENT ON COLUMN qrtz_simprop_triggers.dec_prop_2 IS '소수 속성 2';
COMMENT ON COLUMN qrtz_simprop_triggers.bool_prop_1 IS '부울 속성 1';
COMMENT ON COLUMN qrtz_simprop_triggers.bool_prop_2 IS '부울 속성 2';


CREATE TABLE qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea,
    CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_triggers IS 'Quartz 트리거';
COMMENT ON COLUMN qrtz_triggers.sched_name IS '스케줄러명';
COMMENT ON COLUMN qrtz_triggers.trigger_name IS '트리거명';
COMMENT ON COLUMN qrtz_triggers.trigger_group IS '트리거 그룹';
COMMENT ON COLUMN qrtz_triggers.job_name IS '작업명';
COMMENT ON COLUMN qrtz_triggers.job_group IS '작업 그룹';
COMMENT ON COLUMN qrtz_triggers.description IS '설명';
COMMENT ON COLUMN qrtz_triggers.next_fire_time IS '다음 실행 시간';
COMMENT ON COLUMN qrtz_triggers.prev_fire_time IS '이전 실행 시간';
COMMENT ON COLUMN qrtz_triggers.priority IS '우선순위';
COMMENT ON COLUMN qrtz_triggers.trigger_state IS '트리거 상태';
COMMENT ON COLUMN qrtz_triggers.trigger_type IS '트리거 유형';
COMMENT ON COLUMN qrtz_triggers.start_time IS '시작 시간';
COMMENT ON COLUMN qrtz_triggers.end_time IS '종료 시간';
COMMENT ON COLUMN qrtz_triggers.calendar_name IS '캘린더명';
COMMENT ON COLUMN qrtz_triggers.misfire_instr IS '미실행 처리 방식';
COMMENT ON COLUMN qrtz_triggers.job_data IS '작업 데이터';