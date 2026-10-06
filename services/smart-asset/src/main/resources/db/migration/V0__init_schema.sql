CREATE TABLE common_authorization_group_user (
	authorization_group_id uuid NOT NULL,
	user_id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	CONSTRAINT common_authorization_group_user_pkey PRIMARY KEY (authorization_group_id, user_id)
);
COMMENT ON TABLE common_authorization_group_user IS '공통 권한그룹';
COMMENT ON COLUMN common_authorization_group_user.authorization_group_id IS 'IDX';
COMMENT ON COLUMN common_authorization_group_user.user_id IS '유저ID';
COMMENT ON COLUMN common_authorization_group_user.create_at IS '생성일사';
COMMENT ON COLUMN common_authorization_group_user.update_at IS '수정일시';

CREATE TABLE common_authorization_group_permission (
	authorization_group_id uuid NOT NULL,
	menu_id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	custom1status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	custom2status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	custom3status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	delete_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	down_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	group_code varchar(255) NULL,
	insert_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	manage_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	print_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	select_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	update_status varchar(5) DEFAULT 'NONE'::character varying NOT NULL,
	CONSTRAINT common_authorization_group_permission_pkey PRIMARY KEY (authorization_group_id, menu_id)
);
COMMENT ON TABLE common_authorization_group_permission IS '공통 권한그룹 권한';
COMMENT ON COLUMN common_authorization_group_permission.authorization_group_id IS 'IDX';
COMMENT ON COLUMN common_authorization_group_permission.menu_id IS '메뉴ID';
COMMENT ON COLUMN common_authorization_group_permission.create_at IS '생성일사';
COMMENT ON COLUMN common_authorization_group_permission.update_at IS '수정일시';

CREATE TABLE common_file (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	disabled_at timestamp NULL,
	entity_id uuid NULL,
	entity_name varchar(30) NULL,
	extra_info varchar(255) NULL,
	file_ext varchar(10) NULL,
	file_name varchar(255) NULL,
	file_origin varchar(20) NULL,
	file_size int8 NULL,
	height int4 NULL,
	mime_type varchar(255) NULL,
	sort_seq int4 NULL,
	status varchar(10) NULL,
	storage_type varchar(10) NULL,
	sub_key varchar(50) NULL,
	url varchar(255) NULL,
	user_id uuid NULL,
	width int4 NULL,
	CONSTRAINT common_file_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_file IS '공통 파일';
COMMENT ON COLUMN common_file.id IS 'IDX';
COMMENT ON COLUMN common_file.create_at IS '생성일사';
COMMENT ON COLUMN common_file.update_at IS '수정일시';
COMMENT ON COLUMN common_file.disabled_at IS '사용여부';

CREATE TABLE common_job (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	applied_at timestamp NULL,
	apply_status varchar(10) DEFAULT 'PENDING'::character varying NOT NULL,
	class_name varchar(255) NULL,
	job_type varchar(10) NOT NULL,
	"name" varchar(255) NULL,
	remark varchar(255) NULL,
	"sql" varchar(255) NULL,
	status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	common_job_group_id uuid NULL,
	CONSTRAINT common_job_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job IS '작업 (백그라운드작업)';
COMMENT ON COLUMN common_job.id IS 'IDX';
COMMENT ON COLUMN common_job.create_at IS '생성일사';
COMMENT ON COLUMN common_job.update_at IS '수정일시';
COMMENT ON COLUMN common_job.system_status IS '시스템상태';

CREATE TABLE common_job_group (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	"name" varchar(255) NULL,
	remark varchar(255) NULL,
	status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	CONSTRAINT common_job_group_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_group IS '작업 그룹 (백그라운드작업)';
COMMENT ON COLUMN common_job_group.id IS 'IDX';
COMMENT ON COLUMN common_job_group.create_at IS '생성일사';
COMMENT ON COLUMN common_job_group.update_at IS '수정일시';
COMMENT ON COLUMN common_job_group.system_status IS '시스템상태';

CREATE TABLE common_job_trigger (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	applied_at timestamp NULL,
	apply_status varchar(10) DEFAULT 'PENDING'::character varying NOT NULL,
	"name" varchar(255) NULL,
	status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	trigger_cron varchar(255) NULL,
	trigger_seconds int8 NULL,
	trigger_type varchar(10) NOT NULL,
	common_job_id uuid NULL,
	CONSTRAINT common_job_trigger_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_trigger IS '작업 트리거 (백그라운드작업)';
COMMENT ON COLUMN common_job_trigger.id IS 'IDX';
COMMENT ON COLUMN common_job_trigger.create_at IS '생성일사';
COMMENT ON COLUMN common_job_trigger.update_at IS '수정일시';

CREATE TABLE common_job_run (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	ended_at timestamp NULL,
	job_status varchar(10) DEFAULT 'INVALID'::character varying NOT NULL,
	message varchar(255) NULL,
	started_at timestamp NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	common_job_id uuid NULL,
	common_job_trigger_id uuid NULL,
	CONSTRAINT common_job_run_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_run IS '작업 실행 (백그라운드작업)';
COMMENT ON COLUMN common_job_run.id IS 'IDX';
COMMENT ON COLUMN common_job_run.create_at IS '생성일사';
COMMENT ON COLUMN common_job_run.update_at IS '수정일시';
COMMENT ON COLUMN common_job_run.system_status IS '시스템상태';

CREATE TABLE common_job_state (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	err_cnt int4 NULL,
	last_ended_at timestamp NULL,
	last_failed_at timestamp NULL,
	last_message varchar(255) NULL,
	last_started_at timestamp NULL,
	last_success_at timestamp NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	common_job_id uuid NULL,
	current_run_id uuid NULL,
	last_run_id uuid NULL,
	CONSTRAINT common_job_state_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_job_state IS '작업 상태 (백그라운드작업)';
COMMENT ON COLUMN common_job_state.id IS 'IDX';
COMMENT ON COLUMN common_job_state.create_at IS '생성일사';
COMMENT ON COLUMN common_job_state.update_at IS '수정일시';
COMMENT ON COLUMN common_job_state.system_status IS '시스템상태';

CREATE TABLE common_menu_action (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	action_type varchar(255) NULL,
	auth_type varchar(255) NULL,
	menu_id uuid NULL,
	program_code varchar(255) NULL,
	program_id uuid NULL,
	uri varchar(255) NULL,
	CONSTRAINT common_menu_action_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_menu_action IS '공통 메뉴 액션';
COMMENT ON COLUMN common_menu_action.id IS 'IDX';
COMMENT ON COLUMN common_menu_action.create_at IS '생성일사';
COMMENT ON COLUMN common_menu_action.update_at IS '수정일시';

CREATE TABLE common_public_key (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	encrypt_key varchar(255) NULL,
	key_gen_date timestamp NULL,
	private_key varchar(4000) NULL,
	public_key varchar(1000) NULL,
	CONSTRAINT common_public_key_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_public_key IS '공통 공개키';
COMMENT ON COLUMN common_public_key.id IS 'IDX';
COMMENT ON COLUMN common_public_key.create_at IS '생성일사';
COMMENT ON COLUMN common_public_key.update_at IS '수정일시';

CREATE TABLE common_user (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	department_name varchar(255) NULL,
	duty_name varchar(255) NULL,
	email varchar(255) NULL,
	employee_no varchar(255) NULL,
	first_name varchar(255) NULL,
	full_name varchar(255) NULL,
	last_name varchar(255) NULL,
	phone_number varchar(255) NULL,
	status varchar(255) NULL,
	username varchar(255) NULL,
	CONSTRAINT common_user_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE common_user IS '공통 유저';
COMMENT ON COLUMN common_user.id IS 'IDX';
COMMENT ON COLUMN common_user.create_at IS '생성일사';
COMMENT ON COLUMN common_user.update_at IS '수정일시';


-- PostgreSQL 초기 스키마
-- Quartz Scheduler + 비즈니스 테이블

-- =============================================================================
-- Quartz Scheduler Tables
-- =============================================================================.
CREATE TABLE IF NOT EXISTS qrtz_job_details (
    sched_name VARCHAR(120) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250),
    job_class_name VARCHAR(250) NOT NULL,
    is_durable BOOLEAN NOT NULL,
    is_nonconcurrent BOOLEAN NOT NULL,
    is_update_data BOOLEAN NOT NULL,
    requests_recovery BOOLEAN NOT NULL,
    job_data BYTEA,
    PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE TABLE IF NOT EXISTS qrtz_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250),
    next_fire_time BIGINT,
    prev_fire_time BIGINT,
    priority INTEGER,
    trigger_state VARCHAR(16) NOT NULL,
    trigger_type VARCHAR(8) NOT NULL,
    start_time BIGINT NOT NULL,
    end_time BIGINT,
    calendar_name VARCHAR(200),
    misfire_instr SMALLINT,
    job_data BYTEA,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_triggers_job_details
        FOREIGN KEY (sched_name, job_name, job_group)
        REFERENCES qrtz_job_details (sched_name, job_name, job_group)
);

CREATE TABLE IF NOT EXISTS qrtz_simple_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    repeat_count BIGINT NOT NULL,
    repeat_interval BIGINT NOT NULL,
    times_triggered BIGINT NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simple_triggers
        FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE IF NOT EXISTS qrtz_cron_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    cron_expression VARCHAR(120) NOT NULL,
    time_zone_id VARCHAR(80),
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_cron_triggers
        FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE IF NOT EXISTS qrtz_simprop_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    str_prop_1 VARCHAR(512),
    str_prop_2 VARCHAR(512),
    str_prop_3 VARCHAR(512),
    int_prop_1 INTEGER,
    int_prop_2 INTEGER,
    long_prop_1 BIGINT,
    long_prop_2 BIGINT,
    dec_prop_1 NUMERIC(13, 4),
    dec_prop_2 NUMERIC(13, 4),
    bool_prop_1 BOOLEAN,
    bool_prop_2 BOOLEAN,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simprop_triggers
        FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE IF NOT EXISTS qrtz_blob_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    blob_data BYTEA,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_blob_triggers
        FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE IF NOT EXISTS qrtz_calendars (
    sched_name VARCHAR(120) NOT NULL,
    calendar_name VARCHAR(200) NOT NULL,
    calendar BYTEA NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
);

CREATE TABLE IF NOT EXISTS qrtz_paused_trigger_grps (
    sched_name VARCHAR(120) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
);

CREATE TABLE IF NOT EXISTS qrtz_fired_triggers (
    sched_name VARCHAR(120) NOT NULL,
    entry_id VARCHAR(95) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    instance_name VARCHAR(200) NOT NULL,
    fired_time BIGINT NOT NULL,
    sched_time BIGINT NOT NULL,
    priority INTEGER NOT NULL,
    state VARCHAR(16) NOT NULL,
    job_name VARCHAR(200),
    job_group VARCHAR(200),
    is_nonconcurrent BOOLEAN,
    requests_recovery BOOLEAN,
    PRIMARY KEY (sched_name, entry_id)
);

CREATE TABLE IF NOT EXISTS qrtz_scheduler_state (
    sched_name VARCHAR(120) NOT NULL,
    instance_name VARCHAR(200) NOT NULL,
    last_checkin_time BIGINT NOT NULL,
    checkin_interval BIGINT NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
);

CREATE TABLE IF NOT EXISTS qrtz_locks (
    sched_name VARCHAR(120) NOT NULL,
    lock_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
);

-- Quartz 인덱스
CREATE INDEX IF NOT EXISTS idx_qrtz_j_req_recovery ON qrtz_job_details(sched_name, requests_recovery);
CREATE INDEX IF NOT EXISTS idx_qrtz_j_grp ON qrtz_job_details(sched_name, job_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_j ON qrtz_triggers(sched_name, job_name, job_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_jg ON qrtz_triggers(sched_name, job_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_c ON qrtz_triggers(sched_name, calendar_name);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_g ON qrtz_triggers(sched_name, trigger_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_state ON qrtz_triggers(sched_name, trigger_state);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_n_state ON qrtz_triggers(sched_name, trigger_name, trigger_group, trigger_state);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_n_g_state ON qrtz_triggers(sched_name, trigger_group, trigger_state);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_next_fire_time ON qrtz_triggers(sched_name, next_fire_time);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_nft_st ON qrtz_triggers(sched_name, trigger_state, next_fire_time);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_nft_misfire ON qrtz_triggers(sched_name, misfire_instr, next_fire_time);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_nft_st_misfire ON qrtz_triggers(sched_name, misfire_instr, next_fire_time, trigger_state);
CREATE INDEX IF NOT EXISTS idx_qrtz_t_nft_st_misfire_grp ON qrtz_triggers(sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers(sched_name, instance_name);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_inst_job_req_rcvry ON qrtz_fired_triggers(sched_name, instance_name, requests_recovery);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_j_g ON qrtz_fired_triggers(sched_name, job_name, job_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_jg ON qrtz_fired_triggers(sched_name, job_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_t_g ON qrtz_fired_triggers(sched_name, trigger_name, trigger_group);
CREATE INDEX IF NOT EXISTS idx_qrtz_ft_tg ON qrtz_fired_triggers(sched_name, trigger_group);