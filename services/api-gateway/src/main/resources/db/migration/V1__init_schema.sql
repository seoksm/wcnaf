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

CREATE TABLE qrtz_calendars (
	sched_name varchar(120) NOT NULL,
	calendar_name varchar(200) NOT NULL,
	calendar bytea NOT NULL,
	CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name)
);
COMMENT ON TABLE qrtz_calendars IS 'Quartz 캘린더';

CREATE TABLE qrtz_fired_triggers (
	sched_name varchar(120) NOT NULL,
	entry_id varchar(95) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	instance_name varchar(200) NOT NULL,
	fired_time int8 NOT NULL,
	sched_time int8 NOT NULL,
	priority int4 NOT NULL,
	state varchar(16) NOT NULL,
	job_name varchar(200) NULL,
	job_group varchar(200) NULL,
	is_nonconcurrent bool NULL,
	requests_recovery bool NULL,
	CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id)
);
CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON public.qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);
CREATE INDEX idx_qrtz_ft_j_g ON public.qrtz_fired_triggers USING btree (sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_ft_jg ON public.qrtz_fired_triggers USING btree (sched_name, job_group);
CREATE INDEX idx_qrtz_ft_t_g ON public.qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);
CREATE INDEX idx_qrtz_ft_tg ON public.qrtz_fired_triggers USING btree (sched_name, trigger_group);
CREATE INDEX idx_qrtz_ft_trig_inst_name ON public.qrtz_fired_triggers USING btree (sched_name, instance_name);
COMMENT ON TABLE qrtz_fired_triggers IS 'Quartz 실행된 트리거';

CREATE TABLE qrtz_job_details (
	sched_name varchar(120) NOT NULL,
	job_name varchar(200) NOT NULL,
	job_group varchar(200) NOT NULL,
	description varchar(250) NULL,
	job_class_name varchar(250) NOT NULL,
	is_durable bool NOT NULL,
	is_nonconcurrent bool NOT NULL,
	is_update_data bool NOT NULL,
	requests_recovery bool NOT NULL,
	job_data bytea NULL,
	CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group)
);
CREATE INDEX idx_qrtz_j_grp ON public.qrtz_job_details USING btree (sched_name, job_group);
CREATE INDEX idx_qrtz_j_req_recovery ON public.qrtz_job_details USING btree (sched_name, requests_recovery);
COMMENT ON TABLE qrtz_job_details IS 'Quartz 작업 상세';

CREATE TABLE qrtz_locks (
	sched_name varchar(120) NOT NULL,
	lock_name varchar(40) NOT NULL,
	CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name)
);
COMMENT ON TABLE qrtz_locks IS 'Quartz 락';

CREATE TABLE qrtz_paused_trigger_grps (
	sched_name varchar(120) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group)
);
COMMENT ON TABLE qrtz_paused_trigger_grps IS 'Quartz 일시정지된 트리거 그룹';

CREATE TABLE qrtz_scheduler_state (
	sched_name varchar(120) NOT NULL,
	instance_name varchar(200) NOT NULL,
	last_checkin_time int8 NOT NULL,
	checkin_interval int8 NOT NULL,
	CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name)
);
COMMENT ON TABLE qrtz_scheduler_state IS 'Quartz 스케줄러 상태';

CREATE TABLE qrtz_triggers (
	sched_name varchar(120) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	job_name varchar(200) NOT NULL,
	job_group varchar(200) NOT NULL,
	description varchar(250) NULL,
	next_fire_time int8 NULL,
	prev_fire_time int8 NULL,
	priority int4 NULL,
	trigger_state varchar(16) NOT NULL,
	trigger_type varchar(8) NOT NULL,
	start_time int8 NOT NULL,
	end_time int8 NULL,
	calendar_name varchar(200) NULL,
	misfire_instr int2 NULL,
	job_data bytea NULL,
	CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
CREATE INDEX idx_qrtz_t_c ON public.qrtz_triggers USING btree (sched_name, calendar_name);
CREATE INDEX idx_qrtz_t_g ON public.qrtz_triggers USING btree (sched_name, trigger_group);
CREATE INDEX idx_qrtz_t_j ON public.qrtz_triggers USING btree (sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_t_jg ON public.qrtz_triggers USING btree (sched_name, job_group);
CREATE INDEX idx_qrtz_t_n_g_state ON public.qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_n_state ON public.qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_next_fire_time ON public.qrtz_triggers USING btree (sched_name, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_misfire ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st ON public.qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st_misfire ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);
CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_state ON public.qrtz_triggers USING btree (sched_name, trigger_state);
COMMENT ON TABLE qrtz_triggers IS 'Quartz 트리거';

CREATE TABLE qrtz_blob_triggers (
	sched_name varchar(120) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	blob_data bytea NULL,
	CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_blob_triggers IS 'Quartz Blob 트리거';

CREATE TABLE qrtz_cron_triggers (
	sched_name varchar(120) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	cron_expression varchar(120) NOT NULL,
	time_zone_id varchar(80) NULL,
	CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_cron_triggers IS 'Quartz 크론 트리거';

CREATE TABLE qrtz_simple_triggers (
	sched_name varchar(120) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	repeat_count int8 NOT NULL,
	repeat_interval int8 NOT NULL,
	times_triggered int8 NOT NULL,
	CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_simple_triggers IS 'Quartz 심플 트리거';

CREATE TABLE qrtz_simprop_triggers (
	sched_name varchar(120) NOT NULL,
	trigger_name varchar(200) NOT NULL,
	trigger_group varchar(200) NOT NULL,
	str_prop_1 varchar(512) NULL,
	str_prop_2 varchar(512) NULL,
	str_prop_3 varchar(512) NULL,
	int_prop_1 int4 NULL,
	int_prop_2 int4 NULL,
	long_prop_1 int8 NULL,
	long_prop_2 int8 NULL,
	dec_prop_1 numeric(13, 4) NULL,
	dec_prop_2 numeric(13, 4) NULL,
	bool_prop_1 bool NULL,
	bool_prop_2 bool NULL,
	CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group)
);
COMMENT ON TABLE qrtz_blob_triggers IS 'Quartz Blob 트리거';

CREATE TABLE route (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	"name" varchar(255) NULL,
	sort_seq int4 NOT NULL,
	status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	uri varchar(255) NULL,
	CONSTRAINT route_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE route IS '라우트';
COMMENT ON COLUMN route.id IS 'IDX';
COMMENT ON COLUMN route.create_at IS '생성일사';
COMMENT ON COLUMN route.update_at IS '수정일시';
COMMENT ON COLUMN route.system_status IS '시스템상태';

CREATE TABLE predicate (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	definition text NULL,
	"key" text NULL,
	predicate_type varchar(10) NOT NULL,
	status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	system_status varchar(10) DEFAULT 'ENABLE'::character varying NOT NULL,
	route_id uuid NOT NULL,
	CONSTRAINT predicate_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE predicate IS '프레디케이트';
COMMENT ON COLUMN predicate.id IS 'IDX';
COMMENT ON COLUMN predicate.create_at IS '생성일사';
COMMENT ON COLUMN predicate.update_at IS '수정일시';
COMMENT ON COLUMN predicate.system_status IS '시스템상태';

CREATE TABLE user_session_block (
	id uuid NOT NULL,
	create_at timestamp NULL,
	update_at timestamp NULL,
	access_token_expires_at timestamp NULL,
	user_id uuid NULL,
	user_session_id uuid NULL,
	CONSTRAINT user_session_block_ukey UNIQUE (user_session_id),
	CONSTRAINT user_session_block_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE user_session_block IS '유저 세션 차단';
COMMENT ON COLUMN user_session_block.id IS 'IDX';
COMMENT ON COLUMN user_session_block.create_at IS '생성일사';
COMMENT ON COLUMN user_session_block.update_at IS '수정일시';