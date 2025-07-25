-- Permission
insert into permission (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_P_SYSTEM_ADMIN', 'ROLE_P_SYSTEM_ADMIN', now(), now());
insert into permission (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_P_READ_USER', 'ROLE_P_READ_USER', now(), now());
insert into permission (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_P_CREATE_USER', 'ROLE_P_CREATE_USER', now(), now());
insert into permission (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_P_UPDATE_USER', 'ROLE_P_UPDATE_USER', now(), now());
insert into permission (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_P_DELETE_USER', 'ROLE_P_DELETE_USER', now(), now());

-- Role
insert into role (id, code, name, created_at, updated_at) values (gen_random_uuid(), 'ROLE_SYSTEM_ADMIN', 'ROLE_SYSTEM_ADMIN', now(), now());

-- Role Permission
insert into role_permission (id, role_id, permission_id, created_at, updated_at) select gen_random_uuid(), (select id from role where code = 'ROLE_SYSTEM_ADMIN' limit 1), id, now(), now() from permission limit 100;
