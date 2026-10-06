INSERT INTO common_user (id,create_at,update_at,department_name,duty_name,email,employee_no,first_name,full_name,last_name,phone_number,status,username) VALUES
('10042004-3004-4004-5004-600470048004'::uuid,'2025-04-16 01:11:09.786','2025-04-16 01:11:09.786',NULL,NULL,'devlocal@devlocal',NULL,'리자','관리자','관','010-0000-1234','ENABLE','admin');

INSERT INTO users (id,create_at,update_at,duty_name,email,employee_no,first_name,full_name,hashed_password,join_status,last_name,last_password_changed_at,phone_number,status,username,department_id) VALUES
('10042004-3004-4004-5004-600470048004'::uuid,'2024-08-02 13:52:11.021','2025-04-28 00:56:13.271',NULL,'sample3@gmail.com',NULL,'리자','관리자','$2a$10$jy0fkBuosy0d4Ty5cgUyJeF88ipxgL5Kn/j6YMi9cpcchgx5g/NJ6','ACCEPTED','관','2025-04-02 13:26:12.937','010-0000-1234','ENABLE','dev',NULL);

INSERT INTO organization (id, create_at, update_at, organization_code, organization_name, system_status, national_code) VALUES('00000000-0000-0000-0000-000000000000'::uuid, NULL, NULL, 'DEFAULT', 'Default', 'ENABLE', 'KR');

INSERT INTO user_organization (organization_id, user_id, create_at, update_at, system_status) VALUES('00000000-0000-0000-0000-000000000000'::uuid, '10042004-3004-4004-5004-600470048004'::uuid, '2024-08-06 10:34:58.585', '2024-08-12 08:03:46.781', 'ENABLE');

INSERT INTO authorization_group (id,create_at,update_at,group_code,group_name,admin_status,remark,system_status,parent_authorization_group_id,status) VALUES
('019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'2025-02-17 16:21:30.308','2025-04-01 09:07:36.396','ADMIN','최상위관리자','ENABLE','비고','ENABLE',NULL,'ENABLE');

INSERT INTO authorization_group_user (id,create_at,update_at,authorization_group_id,user_id) VALUES
('01966c1c-26e1-7ffa-ab4c-6f8c6df205ef'::uuid,'2025-04-25 17:41:46.466','2025-04-25 17:41:46.466','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'10042004-3004-4004-5004-600470048004'::uuid);

INSERT INTO "program" (id,create_at,update_at,menu_status,mobile_status,program_code,program_mapping,program_mapping_status,program_name,remark,status,parent_program_id) VALUES
('0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid,'2025-02-04 02:58:45.172985','2025-02-04 03:00:19.737986','ENABLE','DISABLE','SYS_ORGANIZATION','tenant-management/ui/TenantManagementPage','DEFAULT','조직관리','조직관리','ENABLE',NULL),
('0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid,'2025-02-04 03:00:48.734193','2025-02-04 03:00:48.734201','ENABLE','DISABLE','SYS_NATIONAL','national-management/ui/NationalManagementPage','DEFAULT','국가관리','국가관리 ','ENABLE',NULL),
('0194cee7-e3d9-7a03-a271-a5d9d19c9e07'::uuid,'2025-02-04 03:01:31.226221','2025-02-04 03:01:31.226229','ENABLE','DISABLE','SYS_ORG_USER','ManagementSystem/SysOrganizationUser','DEFAULT','조직별 사용자관리','국가> 조직> 사용자','ENABLE',NULL),
('0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid,'2025-02-04 04:20:09.95298','2025-02-04 04:20:09.952991','ENABLE','DISABLE','SYS_AUT',' menu-access-permission/ui/MenuAccessPermissionPage','DEFAULT','조직별 권한관리(권한그룹)','조직별 권한관리 (권한그룹)','ENABLE',NULL),
('0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid,'2025-02-04 03:02:43.48719','2025-02-04 05:22:28.790336','ENABLE','DISABLE','SYS_AUT_M','ManagementSystem/SysAuthMaster','DEFAULT','권한관리(마스터 권한그룹)','마스터 권한관리 조직의 그룹','ENABLE',NULL),
('0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid,'2025-02-11 08:57:52.141954','2025-02-12 04:24:32.522773','ENABLE','DISABLE','SYS_ORG_USER_MNG','ManagementSystem/SysUser','DEFAULT','사용자관리(개발자포탈용)','개발자포탈용','ENABLE',NULL),
('0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid,'2025-02-04 15:16:38.328962','2025-02-12 04:26:18.642461','ENABLE','DISABLE','USER','user-management/ui/UserManagementPage','DEFAULT','사용자관리','','ENABLE',NULL),
('01950393-5265-7501-b27e-a13fe8dcf927'::uuid,'2025-02-14 08:29:01.433275','2025-02-14 08:29:09.488012','ENABLE','DISABLE','SYS_MENU_ACTION','menu-request-permission/ui/MenuRequestPermissionPage','DEFAULT','프로그램별 액션관리','','ENABLE',NULL),
('01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid,'2025-02-19 07:29:39.318943','2025-02-19 07:29:39.318964','ENABLE','DISABLE','SYS_NOTICE','notice/ui/NoticePage','DEFAULT','공지사항','','ENABLE',NULL),
('01954138-9d15-7ffe-ac63-71517807bfda'::uuid,'2025-02-26 16:46:24.162759','2025-02-26 16:46:24.162759','DISABLE','DISABLE','MAIN','/main2','DEFAULT','메인','','ENABLE',NULL);
INSERT INTO "program" (id,create_at,update_at,menu_status,mobile_status,program_code,program_mapping,program_mapping_status,program_name,remark,status,parent_program_id) VALUES
('01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid,'2025-02-28 04:51:11.878666','2025-02-28 04:51:11.8787','ENABLE','DISABLE','SYSDEPARTMENT','department-management/ui/DepartmentManagementPage','DEFAULT','부서코드','','ENABLE',NULL),
('01956e74-f6c4-7ff6-ab48-a7a0fc0b4f3f'::uuid,'2025-03-07 11:35:14.028893','2025-03-07 11:35:14.028893','DISABLE','DISABLE','DEFAULT','/','DEFAULT','기본 (x-menu-id)가 없는 메뉴','','ENABLE',NULL),
('0195932b-ca4b-7c05-b392-a43204db5cef'::uuid,'2025-03-14 05:41:15.483242','2025-03-14 05:43:47.66459','ENABLE','DISABLE','SYSJOB','schedule-management/ui/ScheduleManagementPage','DEFAULT','스케줄 관리','','ENABLE',NULL),
('0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid,'2025-03-14 05:41:46.51537','2025-03-14 05:43:57.237904','ENABLE','DISABLE','SYSJOB_SELECT','schedule-list/ui/ScheduleListPage','DEFAULT','스케줄 조회','','ENABLE',NULL),
('0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid,'2025-02-11 04:38:52.545165','2025-03-21 04:25:54.080859','ENABLE','DISABLE','SYS_MENU','menu-management/ui/MenuManagementPage','DEFAULT','메뉴등록','화면등록, 메뉴등록','ENABLE',NULL),
('0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid,'2025-02-11 08:41:21.413208','2025-04-01 18:31:59.37421','ENABLE','DISABLE','SYS_COMM','common-code/ui/CommonCodePage','DEFAULT','공통코드','','ENABLE',NULL),
('01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid,'2025-04-09 02:52:34.292724','2025-04-15 13:09:07.621318','ENABLE','DISABLE','SYS_API_GW_ROUTE','api-ingress-policy/ui/ApiIngressPolicyPage','DEFAULT','API Gateway 라우팅','','ENABLE',NULL),
('0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid,'2025-02-11 06:52:25.986365','2026-04-23 02:26:55.94828','ENABLE','DISABLE','SYS_USER_AUT','permission-group-user/ui/PermissionGroupUserPage','DEFAULT','권한별사용자관리','','ENABLE',NULL),
('01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid,'2025-02-19 07:26:59.330262','2026-04-23 02:27:37.719862','ENABLE','DISABLE','SYS_AUTH_MNG','menu-access-permission/ui/MenuAccessPermissionPage','DEFAULT','권한관리','','ENABLE',NULL),
('019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid,'2026-04-23 02:31:07.897806','2026-04-23 02:31:07.897813','ENABLE','DISABLE','MYPAGE','my-page/ui/MyPage','DEFAULT','마이페이지','','ENABLE',NULL);
INSERT INTO "program" (id,create_at,update_at,menu_status,mobile_status,program_code,program_mapping,program_mapping_status,program_name,remark,status,parent_program_id) VALUES
('019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid,'2026-04-23 06:44:44.1927','2026-04-23 06:44:44.192707','ENABLE','DISABLE','org','organization-management/ui/OrganizationManagementPage','DEFAULT','조직관리','','ENABLE',NULL);

INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955f10-79dd-7c6f-b714-5bedca5719c7'::uuid,'2025-03-04 11:51:10.173227','2025-03-04 11:51:10.173227','RESTAPI','SELECT','system/authorization-group/*/user/all','0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid),
('01955f12-0362-7672-884d-84b3a11cd1bf'::uuid,'2025-03-04 11:52:50.915792','2025-03-04 11:52:50.915792','RESTAPI','SELECT','system/code','0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid),
('0195028e-8d39-7100-8e03-7a74da95d535'::uuid,'2025-02-14 12:44:11.579531','2025-02-14 12:44:11.579531','RESTAPI','SELECT','/user/_*_/downloadc','0194d547-01b8-7ff8-814f-97c1bb60a6f4'::uuid),
('0195028f-6c4b-7d03-b94f-3922947ace22'::uuid,'2025-02-14 12:45:08.685045','2025-02-14 12:45:08.685045','RESTAPI','SELECT','/user/_*_/downloadf','0194d547-01b8-7ff8-814f-97c1bb60a6f4'::uuid),
('0195657f-f48f-7a2d-ae9e-25bb889ed696'::uuid,'2025-03-05 17:50:39.375011','2025-03-05 17:50:39.375011','RESTAPI','UPDATE','system/program/*','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01956580-40cb-7835-9cda-6eaaa023c200'::uuid,'2025-03-05 17:50:58.892495','2025-03-05 17:50:58.892495','RESTAPI','UPDATE','system/menu/*','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01956580-40e6-7f36-b820-8593ef8cfcc1'::uuid,'2025-03-05 17:50:58.918609','2025-03-05 17:50:58.920118','RESTAPI','DELETE','system/menu/*','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01950291-6437-7805-8887-5b4cb842c459'::uuid,'2025-02-14 12:47:17.690057','2025-02-14 12:47:17.690057','RESTAPI','UPDATE','/user/_*_/downloada','0194d547-01b8-7ff8-814f-97c1bb60a6f4'::uuid),
('01950291-c683-7306-9b44-4d53d0536611'::uuid,'2025-02-14 12:47:42.851809','2025-02-14 12:47:42.851809','QUERYID','SELECT','/user/_*_/downloadf','0194d547-01b8-7ff8-814f-97c1bb60a6f4'::uuid),
('01957aef-5012-7401-952f-b76060180a29'::uuid,'2025-03-09 12:44:18.835664','2025-03-09 12:44:18.835686','RESTAPI','SELECT','system/notice','01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('0195ad2a-fe64-7f03-b169-34728c6d452e'::uuid,'2025-03-19 15:50:30.885318','2025-03-19 15:50:30.885318','RESTAPI','UPDATE','*/commonJob/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-ff6f-7908-b069-1c994d9c6f96'::uuid,'2025-03-19 15:50:31.15233','2025-03-19 15:50:31.15233','RESTAPI','DELETE','*/commonJob/*/trigger/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2b-00be-7b0d-859e-2b94438c4e07'::uuid,'2025-03-19 15:50:31.486438','2025-03-19 15:50:31.486438','RESTAPI','UPDATE','*/commonJobGroup/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2c-2669-7f1a-963a-18f1f5a7e469'::uuid,'2025-03-19 15:51:46.665095','2025-03-19 15:51:46.665095','RESTAPI','SELECT','*/commonJobGroup','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('0195028e-beec-7301-9cbb-9dca10bfc933'::uuid,'2025-02-14 12:44:24.301227','2025-03-21 04:26:06.012048','RESTAPI','DELETE','user/_*_/downloadc0','0194d547-01b8-7ff8-814f-97c1bb60a6f4'::uuid),
('0195d18f-35df-7ff7-8807-4814aae2c9f2'::uuid,'2025-03-26 08:26:18.466427','2025-03-26 08:26:18.466433','RESTAPI','INSERT','system/notice','01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid),
('01954139-2de8-7fff-a6c9-1ae64d8dd6d0'::uuid,'2025-02-26 16:47:01.22522','2025-04-03 10:31:41.969444','RESTAPI','SELECT','system/menu/tree','01954138-9d15-7ffe-ac63-71517807bfda'::uuid),
('01961877-9b3a-7ffd-b18f-a8739c9b7814'::uuid,'2025-04-09 02:53:33.882611','2025-04-09 02:53:33.882631','RESTAPI','INSERT','*/route','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01961877-9c02-7f00-a463-9431ffafcf68'::uuid,'2025-04-09 02:53:34.083325','2025-04-09 02:53:34.08334','RESTAPI','SELECT','*/route/*/predicate','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01961877-9ccb-7003-bbaf-6f3c8e54ffd0'::uuid,'2025-04-09 02:53:34.284319','2025-04-09 02:53:34.284341','RESTAPI','DELETE','*/route/*/predicate/*','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01954164-28a3-7ff7-bd7b-2cd1a4ac9fb4'::uuid,'2025-02-26 17:33:57.924805','2025-02-26 17:33:57.924805','RESTAPI','SELECT','/system/user/*/menu-permission/tree','01954138-9d15-7ffe-ac63-71517807bfda'::uuid),
('0195458b-356c-7ff6-b3dd-20f1047c81a8'::uuid,'2025-02-27 12:55:05.977478','2025-02-27 17:08:15.258853','RESTAPI','DELETE','system/program/*/action/*','01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('0195412e-0807-7ff8-9650-e146fe49f6be'::uuid,'2025-02-26 16:34:50.631632','2025-02-27 17:08:17.993551','RESTAPI','INSERT','system/program/*/action','01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('0195412d-7370-7ff6-b7dc-4d7114f46c7b'::uuid,'2025-02-26 16:34:12.605944','2025-02-27 17:08:20.592595','RESTAPI','UPDATE','system/program/*/action/*','01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('01954128-e2de-7ff9-8c2d-e8aeaa1d7ffc'::uuid,'2025-02-26 16:29:13.439582','2025-02-27 17:08:23.556181','RESTAPI','SELECT','system/program','01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('01954119-d7bc-7ff6-9270-dbdf443a6381'::uuid,'2025-02-26 16:12:47.562818','2025-02-27 17:08:26.741983','RESTAPI','SELECT','system/program/*/action','01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('01955ef0-d58b-7469-8487-ef3316ecb87a'::uuid,'2025-03-04 11:16:36.491588','2025-03-04 11:16:36.491588','RESTAPI','SELECT','system/code','0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid),
('01955eec-5665-7430-b021-0155e4385976'::uuid,'2025-03-04 11:11:41.797229','2025-03-04 11:11:41.797229','RESTAPI','INSERT','system/user-group','0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid),
('01955eec-566c-7334-bbdd-3d97df452a26'::uuid,'2025-03-04 11:11:41.804227','2025-03-04 11:11:41.804227','RESTAPI','DELETE','system/user-group/*','0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid),
('01955eec-f117-7935-b500-e6c8ce3a9975'::uuid,'2025-03-04 11:12:21.400914','2025-03-04 11:12:21.400914','RESTAPI','UPDATE','system/authorization-group/*','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955eec-f11f-7538-a3a3-ece677c2988b'::uuid,'2025-03-04 11:12:21.408919','2025-03-04 11:12:21.408919','RESTAPI','SELECT','system/authorization-group/*/menu-permission/tree-list','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eec-f125-7e3b-9a08-91421bcffba0'::uuid,'2025-03-04 11:12:21.413916','2025-03-04 11:12:21.413916','RESTAPI','SELECT','system/authorization-group/tree','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eed-658c-7b3c-8975-b6f954fa12c5'::uuid,'2025-03-04 11:12:51.213378','2025-03-04 11:12:51.213378','RESTAPI','SELECT','system/organization/*/user-group','0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid),
('01955eed-6593-7540-9a32-29f5d283239a'::uuid,'2025-03-04 11:12:51.219379','2025-03-04 11:12:51.219379','RESTAPI','UPDATE','system/organization/*/user-group/*','0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid),
('01955f10-79de-7f70-8e9a-ced2cb57d1d2'::uuid,'2025-03-04 11:51:10.174227','2025-03-04 11:51:10.174227','RESTAPI','SELECT','system/authorization-group','0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid),
('01956580-40b3-7e34-bbe7-b1438ce97673'::uuid,'2025-03-05 17:50:58.868327','2025-03-05 17:50:58.868327','RESTAPI','INSERT','system/menu','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01956580-410b-7c39-b776-01587b4d1974'::uuid,'2025-03-05 17:50:58.956718','2025-03-05 17:50:58.956718','RESTAPI','SELECT','system/menu/tree','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01956580-4132-783a-a0f9-2e609f3f7551'::uuid,'2025-03-05 17:50:58.995549','2025-03-05 17:50:58.995549','RESTAPI','SELECT','system/program','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01956580-415a-7a3f-a0e0-f2f1ae94e8b0'::uuid,'2025-03-05 17:50:59.035706','2025-03-05 17:50:59.035706','RESTAPI','SELECT','system/program/*','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('0195845a-c0bc-7ff6-bb0f-f158b81c63a5'::uuid,'2025-03-11 08:38:15.058776','2025-03-11 08:38:20.252098','RESTAPI','SELECT','test1','01953705-27f6-7ffe-b490-86b380272f94'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('0195ad2a-fe98-7c04-b22e-b487792eea5c'::uuid,'2025-03-19 15:50:30.937316','2025-03-19 15:50:30.937316','RESTAPI','DELETE','*/commonJob/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-ffa6-7909-8a23-e1973a9ef6bc'::uuid,'2025-03-19 15:50:31.207345','2025-03-19 15:50:31.207345','RESTAPI','SELECT','*/commonJob/*/trigger/check-cron-Expression','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2b-00f6-730e-a863-b4b1ecb4b24b'::uuid,'2025-03-19 15:50:31.542437','2025-03-19 15:50:31.542437','RESTAPI','DELETE','*/commonJobGroup/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2c-2643-7c19-9bc3-0960b61914df'::uuid,'2025-03-19 15:51:46.628075','2025-03-19 15:51:46.628075','RESTAPI','SELECT','*/commonJob/*/state','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('0195ad2c-2685-721b-8b97-702781a3a134'::uuid,'2025-03-19 15:51:46.694073','2025-03-19 15:51:46.694073','RESTAPI','SELECT','*/commonJobGroup/*/job','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('0195db8a-5db2-720a-9807-216b02f8fd15'::uuid,'2025-03-28 15:57:13.141882','2025-03-28 15:57:13.141882','RESTAPI','UPDATE','system/notice/*','01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid),
('0195db8a-7d4a-7b0b-8b7d-af52a92e1cbe'::uuid,'2025-03-28 15:57:21.22662','2025-03-28 15:57:21.22662','RESTAPI','DELETE','system/notice/*','01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid),
('01961877-9baf-7fff-9839-27ddd3c193d1'::uuid,'2025-04-09 02:53:34.000278','2025-04-09 02:53:34.000296','RESTAPI','DELETE','*/route/*','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01955eec-5665-7131-a713-96b2165bd033'::uuid,'2025-03-04 11:11:41.797229','2025-03-04 11:11:41.797229','RESTAPI','SELECT','system/organization','0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid),
('01955eec-566c-7d33-8c39-9a364a37a7de'::uuid,'2025-03-04 11:11:41.80523','2025-03-04 11:11:41.80523','RESTAPI','UPDATE','system/user-group/*','0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955eec-f118-7f37-b230-b539509e1668'::uuid,'2025-03-04 11:12:21.400914','2025-03-04 11:12:21.400914','RESTAPI','DELETE','system/authorization-group/*','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eec-f121-783a-921d-59a7731030f5'::uuid,'2025-03-04 11:12:21.409921','2025-03-04 11:12:21.409921','RESTAPI','INSERT','system/authorization-group/*/menu-permission/batch','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eed-658d-7b3e-87d5-586c6ee016e8'::uuid,'2025-03-04 11:12:51.213378','2025-03-04 11:12:51.213378','RESTAPI','INSERT','system/organization/*/user-group','0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid),
('01955efd-bf27-7e6c-ad5a-71a1f02ade07'::uuid,'2025-03-04 11:30:42.727118','2025-03-04 11:30:42.727118','RESTAPI','SELECT','system/organization/*','0194cee7-e3d9-7a03-a271-a5d9d19c9e07'::uuid),
('019563d2-b6e8-7ff6-91aa-2b19fcf8a942'::uuid,'2025-03-05 10:01:48.709113','2025-03-05 10:01:48.709113','RESTAPI','SELECT','system/department','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('019563d2-b6f2-7ff8-a882-cd4c0721d75b'::uuid,'2025-03-05 10:01:48.709113','2025-03-05 10:01:48.709113','RESTAPI','INSERT','system/user','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('019563d2-b81e-7ffb-a877-c2a5fae5cd0b'::uuid,'2025-03-05 10:01:48.959934','2025-03-05 10:01:48.959934','RESTAPI','UPDATE','system/user/*','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('019563d2-b839-7ffd-b940-7f05cfc09f0f'::uuid,'2025-03-05 10:01:48.986968','2025-03-05 10:01:48.986968','RESTAPI','DELETE','system/user/*','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('019563d2-b847-7ffe-972f-59eb4f995160'::uuid,'2025-03-05 10:01:48.999025','2025-03-05 10:01:48.999025','RESTAPI','UPDATE','system/user/unlockLogin','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('01956580-4185-7e40-a757-24b8e0737caa'::uuid,'2025-03-05 17:50:59.07852','2025-03-05 17:50:59.07852','RESTAPI','DELETE','system/program/*','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01957aee-1686-7fff-8599-8c71142143c5'::uuid,'2025-03-09 12:42:58.570824','2025-03-09 12:42:58.570907','RESTAPI','SELECT','system/user/*/menu/*/permission','01954138-9d15-7ffe-ac63-71517807bfda'::uuid),
('01958e2a-00eb-7ff6-b6a5-231445eb9ec9'::uuid,'2025-03-13 15:21:12.311791','2025-03-13 15:21:12.311791','RESTAPI','UPDATE','system/user/*/acceptJoin','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('0195adaa-0a57-7646-a35e-a05411b90c4b'::uuid,'2025-03-19 18:09:17.01596','2025-03-19 18:09:17.01596','RESTAPI','INSERT','*/commonJob/*/fire','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('019563d2-b81c-7ff9-8e6d-03ebbab02019'::uuid,'2025-03-05 10:01:48.957807','2025-04-09 10:49:59.141587','RESTAPI','SELECT','system/user/*','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('01955f0f-be6f-7a6d-82ae-f3255a2cdea8'::uuid,'2025-03-04 11:50:22.191517','2025-03-04 11:50:22.191517','RESTAPI','SELECT','system/user','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('01955f10-79dc-726e-a440-ee8d8fb8dc05'::uuid,'2025-03-04 11:51:10.172212','2025-03-04 11:51:10.172212','RESTAPI','INSERT','system/authorization-group/*/user/batch','0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid),
('01955eec-5665-7932-878f-b7cdbbc7b907'::uuid,'2025-03-04 11:11:41.797229','2025-03-04 11:11:41.797229','RESTAPI','SELECT','system/user-group','0194cf2f-e45b-7405-8ba6-d5a1bae5372e'::uuid),
('01955eec-f118-7036-b849-fe1cab3cbfb9'::uuid,'2025-03-04 11:12:21.400914','2025-03-04 11:12:21.400914','RESTAPI','INSERT','system/authorization-group','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eec-f120-7c39-9825-5708be0ef1b8'::uuid,'2025-03-04 11:12:21.408919','2025-03-04 11:12:21.408919','RESTAPI','SELECT','system/authorization-group','01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('01955eed-658d-7b3d-8d04-ea648476af3b'::uuid,'2025-03-04 11:12:51.213378','2025-03-04 11:12:51.213378','RESTAPI','SELECT','system/organization','0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955eed-6593-763f-ae09-043c69d3eddb'::uuid,'2025-03-04 11:12:51.219379','2025-03-04 11:12:51.219379','RESTAPI','DELETE','system/organization/*/user-group/*','0194cee8-fe1e-7604-9694-b173ec3836ad'::uuid),
('01955eed-c313-7542-8c68-af16d280c963'::uuid,'2025-03-04 11:13:15.156286','2025-03-04 11:13:15.156286','RESTAPI','DELETE','system/code/*','0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid),
('01955eed-c313-7843-bf9e-84f107c7ef04'::uuid,'2025-03-04 11:13:15.156286','2025-03-04 11:13:15.156286','RESTAPI','UPDATE','system/code/*','0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid),
('01955eee-21aa-7445-ba09-f5b2e7e2a9b7'::uuid,'2025-03-04 11:13:39.370123','2025-03-04 11:13:39.370123','RESTAPI','SELECT','system/department','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-21aa-7c46-b84a-a9f2ee24af3e'::uuid,'2025-03-04 11:13:39.371123','2025-03-04 11:13:39.371123','RESTAPI','INSERT','system/department','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-21ab-7947-9904-6963fa8da009'::uuid,'2025-03-04 11:13:39.371123','2025-03-04 11:13:39.371123','RESTAPI','SELECT','system/department/*','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-21b2-7c48-9975-47be7f60de07'::uuid,'2025-03-04 11:13:39.379122','2025-03-04 11:13:39.379122','RESTAPI','DELETE','system/department/*','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-21b2-7449-b82a-b7e72cf3d4dd'::uuid,'2025-03-04 11:13:39.379122','2025-03-04 11:13:39.379122','RESTAPI','UPDATE','system/department/order','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-21b3-754a-b269-557f78412a34'::uuid,'2025-03-04 11:13:39.379122','2025-03-04 11:13:39.379122','RESTAPI','UPDATE','system/department/*','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('01955eee-4f36-7c51-bebc-fc90737e6d59'::uuid,'2025-03-04 11:13:51.03117','2025-03-04 11:13:51.03117','RESTAPI','INSERT','system/program','0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955eee-6f87-7554-84aa-03be85f4565f'::uuid,'2025-03-04 11:13:59.303049','2025-03-04 11:13:59.303049','RESTAPI','SELECT','system/national','0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid),
('01955eee-6f88-7555-b5a0-9b6232eefd41'::uuid,'2025-03-04 11:13:59.304048','2025-03-04 11:13:59.304048','RESTAPI','INSERT','system/national','0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid),
('01955eee-6f88-7756-897e-19a32ad49547'::uuid,'2025-03-04 11:13:59.304048','2025-03-04 11:13:59.304048','RESTAPI','UPDATE','system/national/*','0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid),
('01955eee-6f8e-7f57-88c6-70b8d761d60c'::uuid,'2025-03-04 11:13:59.311056','2025-03-04 11:13:59.311056','RESTAPI','DELETE','system/national/*','0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid),
('01955eef-de0a-7c58-9e3b-da4610f00e8c'::uuid,'2025-03-04 11:15:33.130002','2025-03-04 11:15:33.130002','RESTAPI','SELECT','system/organization','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955eef-de0b-7b59-9317-3c068efa3dc1'::uuid,'2025-03-04 11:15:33.131003','2025-03-04 11:15:33.131003','RESTAPI','INSERT','system/organization','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955eef-de0b-7a5a-a3b0-ec6e4a027097'::uuid,'2025-03-04 11:15:33.131003','2025-03-04 11:15:33.131003','RESTAPI','UPDATE','system/organization/*','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955eef-de10-7b5b-ad31-f84d3cffead2'::uuid,'2025-03-04 11:15:33.137005','2025-03-04 11:15:33.137005','RESTAPI','DELETE','system/organization/*','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955eef-de11-7a5c-950f-33d0462a874e'::uuid,'2025-03-04 11:15:33.137005','2025-03-04 11:15:33.137005','RESTAPI','SELECT','system/organization/*/user','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955eef-de12-715d-8185-87ca466393a0'::uuid,'2025-03-04 11:15:33.138006','2025-03-04 11:15:33.138006','RESTAPI','DELETE','system/organization/*/user/*','0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01955eef-f7fe-7e5e-8ed8-0894964dcb97'::uuid,'2025-03-04 11:15:39.774722','2025-03-04 11:15:39.774722','RESTAPI','DELETE','system/organization/*/user/*','0194cee7-e3d9-7a03-a271-a5d9d19c9e07'::uuid),
('01955eef-f7fe-7960-94a0-0a6ba17d1849'::uuid,'2025-03-04 11:15:39.775723','2025-03-04 11:15:39.775723','RESTAPI','SELECT','system/organization/*/user','0194cee7-e3d9-7a03-a271-a5d9d19c9e07'::uuid),
('01955ef0-06fe-7863-b006-df10bef57ac4'::uuid,'2025-03-04 11:15:43.615695','2025-03-04 11:15:43.615695','RESTAPI','UPDATE','system/user/*','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('01955ef0-06fe-7a62-98a4-464aa85cbf42'::uuid,'2025-03-04 11:15:43.615695','2025-03-04 11:15:43.615695','RESTAPI','INSERT','system/user','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('01955f12-0362-7571-83fc-520449392d1e'::uuid,'2025-03-04 11:52:50.914794','2025-03-04 11:52:50.914794','RESTAPI','INSERT','system/code','0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid),
('01955ef0-070b-7e65-ba51-219670287c0d'::uuid,'2025-03-04 11:15:43.627692','2025-03-04 11:15:43.627692','RESTAPI','UPDATE','system/user/*/resetPassword','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('01955ef0-070a-7064-a943-96ef324d9abd'::uuid,'2025-03-04 11:15:43.627692','2025-03-04 11:15:43.627692','RESTAPI','UPDATE','system/user/*/acceptJoin','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('01955ef0-070b-7566-bf9c-0d7e3496327e'::uuid,'2025-03-04 11:15:43.627692','2025-03-04 11:15:43.627692','RESTAPI','DELETE','system/user/*','0194f43a-a70b-7a19-a7e3-0fdffd0a88a0'::uuid),
('019563d2-b6f0-7ff7-9be3-7fd782be81e8'::uuid,'2025-03-05 10:01:48.709113','2025-03-05 10:01:48.709113','RESTAPI','SELECT','system/user-group','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('019563d2-b836-7ffc-b9fc-d4b730167caa'::uuid,'2025-03-05 10:01:48.98392','2025-03-05 10:01:48.98392','RESTAPI','UPDATE','system/user/*/resetPassword','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('01957aef-b2c3-7702-b01d-87a47733e777'::uuid,'2025-03-09 12:44:44.100985','2025-03-09 12:44:44.101023','RESTAPI','SELECT','system/notice/*','01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid),
('0195ad2a-fd59-7c02-b79d-07778d66b92e'::uuid,'2025-03-19 15:50:30.61805','2025-03-19 15:50:30.61805','RESTAPI','INSERT','*/commonJob','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-fec7-7505-ae19-142de21a2611'::uuid,'2025-03-19 15:50:30.983317','2025-03-19 15:50:30.983317','RESTAPI','SELECT','*/commonJob/*/trigger','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-fefe-7206-ac09-b4362ce19edd'::uuid,'2025-03-19 15:50:31.039316','2025-03-19 15:50:31.039316','RESTAPI','INSERT','*/commonJob/*/trigger','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-ff30-7307-a97d-128321eb6aa8'::uuid,'2025-03-19 15:50:31.088319','2025-03-19 15:50:31.088319','RESTAPI','UPDATE','*/commonJob/*/trigger/*','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2a-ffdb-790a-b1c3-b0539cf924ca'::uuid,'2025-03-19 15:50:31.259341','2025-03-19 15:50:31.259341','RESTAPI','SELECT','*/commonJob/all/sync','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2b-0024-760b-97aa-342743da17cd'::uuid,'2025-03-19 15:50:31.332335','2025-03-19 15:50:31.332335','RESTAPI','SELECT','*/commonJobGroup','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2b-0080-770c-93d7-67b75ea93079'::uuid,'2025-03-19 15:50:31.424102','2025-03-19 15:50:31.424102','RESTAPI','INSERT','*/commonJobGroup','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2b-012d-710f-b236-0ed072245a58'::uuid,'2025-03-19 15:50:31.597436','2025-03-19 15:50:31.597436','RESTAPI','SELECT','*/commonJobGroup/*/job','0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195ad2c-2628-7518-8245-f983f019afdb'::uuid,'2025-03-19 15:51:46.601087','2025-03-19 15:51:46.601087','RESTAPI','SELECT','*/commonJob/*/run','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('0195adab-0c5e-754b-b1a7-47f3330cfae0'::uuid,'2025-03-19 18:10:23.070415','2025-03-19 18:10:23.070415','RESTAPI','INSERT','*/commonJob/*/cancel','0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('01957aee-bfeb-7000-adad-da3aa65411e6'::uuid,'2025-03-09 12:43:41.931843','2025-04-01 18:32:12.808041','RESTAPI','SELECT','system/department/tree','01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('019563d2-b81c-7ffa-816d-79d6c0036488'::uuid,'2025-03-05 10:01:48.957807','2025-04-09 10:37:14.181888','RESTAPI','SELECT','system/user','0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('01961877-9a83-7ffc-8405-7c72b0671e25'::uuid,'2025-04-09 02:53:33.700807','2025-04-09 02:53:33.700836','RESTAPI','SELECT','*/route','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01961877-9b4c-7ffe-838e-b7cf1d19ae39'::uuid,'2025-04-09 02:53:33.901204','2025-04-09 02:53:33.901219','RESTAPI','UPDATE','*/route/*','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01961877-9c19-7b01-9c5e-8904473f0fdc'::uuid,'2025-04-09 02:53:34.106786','2025-04-09 02:53:34.106807','RESTAPI','INSERT','*/route/*/predicate','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('01961877-9c72-7502-9a78-987c9738eea6'::uuid,'2025-04-09 02:53:34.195295','2025-04-09 02:53:34.195315','RESTAPI','UPDATE','*/route/*/predicate/*','01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('019db8f7-55ea-7ff9-abd8-c0b966f66c56'::uuid,'2026-04-23 06:11:50.378466','2026-04-23 06:11:50.378476','RESTAPI','SELECT','*/commonFile/download/*','01956e74-f6c4-7ff6-ab48-a7a0fc0b4f3f'::uuid),
('019db8e2-8704-7004-8867-93cec0b0bab9'::uuid,'2026-04-23 05:49:06.693237','2026-04-23 06:11:55.205289','RESTAPI','INSERT','*/commonFile/prepareUpload','01956e74-f6c4-7ff6-ab48-a7a0fc0b4f3f'::uuid),
('019db8f9-2b2a-7ffc-9ab0-e5700fa39bf7'::uuid,'2026-04-23 06:13:50.507074','2026-04-23 06:13:50.507081','RESTAPI','INSERT','*/commonFile/downloadMulti','01956e74-f6c4-7ff6-ab48-a7a0fc0b4f3f'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('019db943-5fcf-7ffb-a17c-e6be10da70ce'::uuid,'2026-04-23 07:34:53.647689','2026-04-23 07:34:53.647696','RESTAPI','SELECT','system/national','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-5fe6-7ffc-81aa-36547adcd619'::uuid,'2026-04-23 07:34:53.670797','2026-04-23 07:34:53.670803','RESTAPI','INSERT','system/national','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-5ff8-7ffd-8b3b-60b42a22be08'::uuid,'2026-04-23 07:34:53.688458','2026-04-23 07:34:53.688464','RESTAPI','UPDATE','system/national/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-6009-7ffe-8db8-1cf0d1cc2258'::uuid,'2026-04-23 07:34:53.705692','2026-04-23 07:34:53.705701','RESTAPI','DELETE','system/national/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-6019-7fff-8642-fc6f36392fca'::uuid,'2026-04-23 07:34:53.721506','2026-04-23 07:34:53.721513','RESTAPI','SELECT','system/organization','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-603a-7301-96c3-f6c064ef6e79'::uuid,'2026-04-23 07:34:53.755153','2026-04-23 07:34:53.755159','RESTAPI','UPDATE','system/organization/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-604a-7b02-898d-d85941d8d26c'::uuid,'2026-04-23 07:34:53.771185','2026-04-23 07:34:53.771194','RESTAPI','DELETE','system/organization/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-605c-7003-9b18-76f4d2e0c101'::uuid,'2026-04-23 07:34:53.788695','2026-04-23 07:34:53.788703','RESTAPI','INSERT','system/organization','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-609d-7807-85e7-490aeb76162d'::uuid,'2026-04-23 07:34:53.853938','2026-04-23 07:34:53.853945','RESTAPI','SELECT','system/organization/*/user-group','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-60b0-7e08-90bd-90a1a0f59460'::uuid,'2026-04-23 07:34:53.872304','2026-04-23 07:34:53.872398','RESTAPI','UPDATE','system/organization/*/user/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('019db943-60c3-7a09-af1c-8f3eaf46efb3'::uuid,'2026-04-23 07:34:53.892075','2026-04-23 07:34:53.892082','RESTAPI','SELECT','system/organization/*/user','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-60d8-790a-964f-ef5930ca276b'::uuid,'2026-04-23 07:34:53.912579','2026-04-23 07:34:53.912588','RESTAPI','INSERT','system/organization/*/user/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-60e9-750b-bacb-362751e532bd'::uuid,'2026-04-23 07:34:53.929541','2026-04-23 07:34:53.929549','RESTAPI','DELETE','system/organization/*/user/*','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db943-60f9-7c0c-9ca6-bb005b9f7099'::uuid,'2026-04-23 07:34:53.945284','2026-04-23 07:34:53.94529','RESTAPI','SELECT','system/user','019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019db986-76ca-7ffc-8c47-94bf7e22b7bf'::uuid,'2026-04-23 08:48:10.442483','2026-04-23 08:48:10.442489','RESTAPI','SELECT','system/organization','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-76dd-7ffd-be6a-6f2fda9933d5'::uuid,'2026-04-23 08:48:10.461857','2026-04-23 08:48:10.461863','RESTAPI','INSERT','system/organization','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-76ed-7ffe-9ca1-72f8addeadc7'::uuid,'2026-04-23 08:48:10.477571','2026-04-23 08:48:10.477577','RESTAPI','UPDATE','system/organization/*','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-76ff-7fff-ad7d-680cd46e8356'::uuid,'2026-04-23 08:48:10.495431','2026-04-23 08:48:10.495436','RESTAPI','DELETE','system/organization/*','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7710-7a00-8aab-fe4495e4c999'::uuid,'2026-04-23 08:48:10.512911','2026-04-23 08:48:10.512917','RESTAPI','INSERT','system/user/logout','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7720-7601-a371-70acb44c2f7c'::uuid,'2026-04-23 08:48:10.528622','2026-04-23 08:48:10.528629','RESTAPI','SELECT','system/user','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid);
INSERT INTO program_action (id,create_at,update_at,action_type,auth_type,uri,program_id) VALUES
('019db986-7731-7702-a0ac-bb4a30cf6aa7'::uuid,'2026-04-23 08:48:10.545585','2026-04-23 08:48:10.545591','RESTAPI','INSERT','system/user','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7740-7103-a962-2733e1753a75'::uuid,'2026-04-23 08:48:10.560787','2026-04-23 08:48:10.560793','RESTAPI','SELECT','system/user-group','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7752-7504-8175-d2c425fc2722'::uuid,'2026-04-23 08:48:10.578424','2026-04-23 08:48:10.578431','RESTAPI','SELECT','system/user/*','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7761-7205-ad84-5a2ab27b722b'::uuid,'2026-04-23 08:48:10.593606','2026-04-23 08:48:10.593611','RESTAPI','UPDATE','system/user/*','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-7770-7306-aa4c-b2d48b9648b9'::uuid,'2026-04-23 08:48:10.608715','2026-04-23 08:48:10.608721','RESTAPI','DELETE','system/user/*','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-777f-7407-b41f-1bd716d10e67'::uuid,'2026-04-23 08:48:10.623204','2026-04-23 08:48:10.623209','RESTAPI','UPDATE','system/user/*/acceptJoin','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-778e-7908-b7d8-76bb28aecf52'::uuid,'2026-04-23 08:48:10.638454','2026-04-23 08:48:10.638459','RESTAPI','UPDATE','system/user/*/resetPassword','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-77a0-7f09-938c-358841b0fd09'::uuid,'2026-04-23 08:48:10.657237','2026-04-23 08:48:10.657244','RESTAPI','UPDATE','system/user/unlockLogin','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019db986-77b4-7b0a-9b92-bac1d39d4a6d'::uuid,'2026-04-23 08:48:10.676531','2026-04-23 08:48:10.676537','RESTAPI','UPDATE','system/user/profile','019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid);

INSERT INTO menu (id,create_at,update_at,menu_code,menu_mapping,menu_name,menu_status,menu_type,sort_seq,status,system_status,parent_menu_id,program_id) VALUES
('0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid,'2025-02-04 02:57:23.516843','2026-05-07 02:11:04.900105','sys/','','관리용','DISABLE','MENU',15,'ENABLE','DISABLE',NULL,NULL),
('0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'2025-02-04 04:55:27.161317','2026-05-07 08:39:25.191639','System','','시스템관리','DISABLE','MENU',1,'ENABLE','ENABLE',NULL,NULL),
('019db82d-617a-7ffd-9259-2e606c2fd7ca'::uuid,'2026-04-23 02:31:15.067225','2026-05-07 08:54:53.371247','','my-page/ui/MyPage','마이페이지','ENABLE','PROGRAM',13,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('019e01ae-5c1e-7a0b-bd37-bf7e3862f9ba'::uuid,'2026-05-07 09:04:24.606535','2026-05-07 09:04:42.242512',NULL,'my-page/ui/MyPage','마이페이지','ENABLE','PROGRAM',1,'ENABLE','DISABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'019db82d-4578-7ffc-a231-1cae51f0c0c8'::uuid),
('01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid,'2025-02-19 07:27:55.595702','2026-05-08 01:22:58.239486',NULL,'menu-access-permission/ui/MenuAccessPermissionPage','권한관리','ENABLE','PROGRAM',5,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid),
('019535a2-f209-7d09-8ace-6b8889249192'::uuid,'2025-02-24 01:47:06.134827','2025-02-24 01:47:06.134885',NULL,'notice/ui/NoticePage','공지사항','ENABLE','PROGRAM',2,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'01951d1c-c3f3-7ffc-ad7f-400adf2ce43c'::uuid),
('0194cf30-680c-7b08-96c1-54f72ad2fd85'::uuid,'2025-02-04 04:20:43.661256','2026-04-23 08:27:00.031754',NULL,'tenant-management/ui/TenantManagementPage','조직관리','ENABLE','PROGRAM',16,'ENABLE','DISABLE','0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid,'0194cee5-5b2f-7a01-a6b1-98e601a695e1'::uuid),
('01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid,'2025-03-04 05:50:24.874355','2025-03-04 05:50:24.87442',NULL,'menu-management/ui/MenuManagementPage','메뉴등록','ENABLE','PROGRAM',3,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid),
('01955fb4-9417-7a21-870d-599df6229b86'::uuid,'2025-03-04 05:50:24.874356','2025-03-04 05:50:24.874419',NULL,'menu-request-permission/ui/MenuRequestPermissionPage','프로그램별 액션관리','ENABLE','PROGRAM',4,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'01950393-5265-7501-b27e-a13fe8dcf927'::uuid),
('01951d1b-2eca-7ff9-b640-48294e8a87af'::uuid,'2025-02-19 07:27:55.595705','2025-02-19 07:27:55.595715',NULL,'permission-group-user/ui/PermissionGroupUserPage','권한별사용자관리','ENABLE','PROGRAM',6,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0194f3c7-cffe-7111-92b1-d42fbac7d8fc'::uuid);
INSERT INTO menu (id,create_at,update_at,menu_code,menu_mapping,menu_name,menu_status,menu_type,sort_seq,status,system_status,parent_menu_id,program_id) VALUES
('01951d1b-2ecc-7ffb-90b9-932022d26811'::uuid,'2025-02-19 07:27:55.597216','2025-02-19 07:27:55.597224',NULL,'user-management/ui/UserManagementPage','사용자관리','ENABLE','PROGRAM',7,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0194cf9a-86b4-7ff6-819a-02557f94ee3f'::uuid),
('01954ae5-1205-7603-b9fe-e0ae2b2871cc'::uuid,'2025-02-28 04:51:21.22693','2025-02-28 04:51:21.226953',NULL,'department-management/ui/DepartmentManagementPage','부서코드','ENABLE','PROGRAM',8,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'01954ae4-ed78-7702-ab75-3cb88757ae5a'::uuid),
('0194f42b-e435-7416-aabc-049e66e74adb'::uuid,'2025-02-11 08:41:44.759889','2025-02-11 08:41:44.759897',NULL,'common-code/ui/CommonCodePage','공통코드','ENABLE','PROGRAM',9,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0194f42b-8903-7215-b12a-945f97dd2e0c'::uuid),
('01959336-4e5c-7e0f-9328-dda718538a18'::uuid,'2025-03-14 05:52:44.63826','2025-03-14 05:52:44.638293',NULL,'schedule-management/ui/ScheduleManagementPage','스케줄 관리','ENABLE','PROGRAM',10,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0195932b-ca4b-7c05-b392-a43204db5cef'::uuid),
('0195932e-d471-720b-a111-80ed7f263b71'::uuid,'2025-03-14 05:44:34.67445','2025-03-14 05:44:34.674465',NULL,'schedule-list/ui/ScheduleListPage','스케줄 조회','ENABLE','PROGRAM',11,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'0195932c-438f-7b06-9e0d-5a5ea131a376'::uuid),
('01961877-3b1e-7ffb-8a8b-5df2f1d0a543'::uuid,'2025-04-09 02:53:09.280306','2025-04-09 02:53:09.280326',NULL,'api-ingress-policy/ui/ApiIngressPolicyPage','API 라우팅','ENABLE','PROGRAM',12,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'01961876-b21c-7ffa-abd5-929f9d51e2b1'::uuid),
('019db915-aea3-7ff9-adcf-e94196ba86ba'::uuid,'2026-04-23 06:44:59.171846','2026-04-23 06:44:59.171851',NULL,'organization-management/ui/OrganizationManagementPage','조직관리','ENABLE','PROGRAM',14,'ENABLE','ENABLE','0194cf50-32af-720b-99ad-84e57a5568b0'::uuid,'019db915-741c-7ff8-a2cc-8c37444a0a7e'::uuid),
('019536f3-97b6-7ffa-a9f7-0a97ac0efba0'::uuid,'2025-02-24 07:54:48.631','2025-03-07 11:47:59.337','test','','메인 (🙇 수정하지마세요)','DISABLE','MENU',17,'DISABLE','ENABLE',NULL,NULL),
('01956e77-b818-7ffe-a6d8-8e2d405cff55'::uuid,'2025-03-07 11:38:14.556059','2025-03-07 11:38:14.556059',NULL,'/','기본 (x-menu-id)가 없는 메뉴','DISABLE','PROGRAM',18,'ENABLE','ENABLE','019536f3-97b6-7ffa-a9f7-0a97ac0efba0'::uuid,'01956e74-f6c4-7ff6-ab48-a7a0fc0b4f3f'::uuid),
('0195655f-0499-7ff8-8dab-e2e9dca051c4'::uuid,'2025-03-05 17:14:40.798284','2025-03-05 17:14:40.798284',NULL,'/main2','메인','DISABLE','PROGRAM',19,'ENABLE','ENABLE','019536f3-97b6-7ffa-a9f7-0a97ac0efba0'::uuid,'01954138-9d15-7ffe-ac63-71517807bfda'::uuid);
INSERT INTO menu (id,create_at,update_at,menu_code,menu_mapping,menu_name,menu_status,menu_type,sort_seq,status,system_status,parent_menu_id,program_id) VALUES
('0194cf30-6822-7009-8a8c-ee7aadefb9bf'::uuid,'2025-02-04 04:20:43.685944','2026-04-24 05:25:18.242575',NULL,'national-management/ui/NationalManagementPage','국가관리','ENABLE','PROGRAM',16,'ENABLE','DISABLE','0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid,'0194cee7-3ddd-7b02-971c-8c2a5831c599'::uuid);

INSERT INTO menu_permission (id,create_at,update_at,custom1status,custom2status,custom3status,delete_status,down_status,insert_status,manage_status,print_status,select_status,update_status,authorization_group_id,menu_id) VALUES
('0195264a-5497-7432-9acf-1f3de967d636'::uuid,'2025-02-21 02:16:00.447','2025-02-26 16:26:47.835','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid),
('0195264a-5498-7533-b063-d72c0cc73755'::uuid,'2025-02-21 02:16:00.448','2025-02-26 16:26:47.836','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0194cf30-6822-7009-8a8c-ee7aadefb9bf'::uuid),
('0195264a-54b1-7934-9bfc-04feb2ac776e'::uuid,'2025-02-21 02:16:00.448','2025-02-26 16:26:47.837','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0194cf30-680c-7b08-96c1-54f72ad2fd85'::uuid),
('01956e7d-6ea0-7fff-979c-43fd83d00576'::uuid,'2025-03-07 11:44:28.965','2025-03-07 11:44:28.965','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01956e77-b818-7ffe-a6d8-8e2d405cff55'::uuid),
('019526bb-fc69-7a08-9d34-78651e620f28'::uuid,'2025-02-21 04:20:08.947','2025-04-09 10:51:59.43','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01951d1b-2ecc-7ffb-90b9-932022d26811'::uuid),
('01961878-fe01-7504-bd34-6da4ab09f70e'::uuid,'2025-04-09 02:55:04.783','2025-04-09 02:55:04.783','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01961877-3b1e-7ffb-8a8b-5df2f1d0a543'::uuid),
('019526bb-fc68-7f04-a1c6-4f73498710be'::uuid,'2025-02-21 04:20:08.941','2025-02-21 04:22:23.224','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0194cf50-32af-720b-99ad-84e57a5568b0'::uuid),
('019526bb-fc69-720a-a593-5b6cb4a3492d'::uuid,'2025-02-21 04:20:08.948','2025-02-21 04:22:23.225','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01951d1b-2eca-7ff9-b640-48294e8a87af'::uuid),
('019526bb-fc69-7905-be7d-3259c4eb8135'::uuid,'2025-02-21 04:20:08.946','2025-02-21 04:22:23.225','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0194f42b-e435-7416-aabc-049e66e74adb'::uuid),
('019526bb-fc69-7b09-94cc-6ee688e00d52'::uuid,'2025-02-21 04:20:08.947','2025-02-21 04:22:23.226','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid);
INSERT INTO menu_permission (id,create_at,update_at,custom1status,custom2status,custom3status,delete_status,down_status,insert_status,manage_status,print_status,select_status,update_status,authorization_group_id,menu_id) VALUES
('0195883f-983b-7ff9-b070-372de5ad1552'::uuid,'2025-03-12 11:47:04.015','2025-03-12 11:47:04.015','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01954ae5-1205-7603-b9fe-e0ae2b2871cc'::uuid),
('01954126-aa04-7ff6-b9ed-422171e5da9e'::uuid,'2025-02-26 16:26:47.829','2025-02-26 16:26:47.829','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'019535a2-f209-7d09-8ace-6b8889249192'::uuid),
('01956054-31ab-7ffa-be09-41350d26a09d'::uuid,'2025-03-04 17:44:45.366','2025-03-04 17:44:45.366','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01955fb4-9417-7a21-870d-599df6229b86'::uuid),
('0195656b-2430-7ff9-951d-eb70a8cdfb90'::uuid,'2025-03-05 17:27:55.318','2025-03-05 17:27:55.318','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0195655f-0499-7ff8-8dab-e2e9dca051c4'::uuid),
('01956572-cfdf-7ff7-b1ec-9db26460e625'::uuid,'2025-03-05 17:36:18.019','2025-03-05 17:36:18.019','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid),
('01959344-d33e-7ff6-8e04-5408ace093a0'::uuid,'2025-03-14 06:08:36.177','2025-03-14 06:08:36.177','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'01959336-4e5c-7e0f-9328-dda718538a18'::uuid),
('01959344-d344-7ff7-b242-592447c0224a'::uuid,'2025-03-14 06:08:36.26','2025-03-14 06:08:36.26','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'0195932e-d471-720b-a111-80ed7f263b71'::uuid),
('019db82d-a1b3-7ffe-bac0-e5b02e86415f'::uuid,'2026-04-23 02:31:31.508552','2026-04-23 02:31:31.508558','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'019db82d-617a-7ffd-9259-2e606c2fd7ca'::uuid),
('019db915-d542-7ffa-9203-6dc397e7ca2e'::uuid,'2026-04-23 06:45:09.060121','2026-04-23 06:45:09.060127','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid,'019db915-aea3-7ff9-adcf-e94196ba86ba'::uuid);
