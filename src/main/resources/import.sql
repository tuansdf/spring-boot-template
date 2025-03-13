-- Permission
insert into permission (id, code, name, status) values (gen_random_uuid(), 'ROLE_P_SYSTEM_ADMIN', 'ROLE_P_SYSTEM_ADMIN', 1);
insert into permission (id, code, name, status) values (gen_random_uuid(), 'ROLE_P_READ_USER', 'ROLE_P_READ_USER', 1);
insert into permission (id, code, name, status) values (gen_random_uuid(), 'ROLE_P_CREATE_USER', 'ROLE_P_CREATE_USER', 1);
insert into permission (id, code, name, status) values (gen_random_uuid(), 'ROLE_P_UPDATE_USER', 'ROLE_P_UPDATE_USER', 1);
insert into permission (id, code, name, status) values (gen_random_uuid(), 'ROLE_P_DELETE_USER', 'ROLE_P_DELETE_USER', 1);

-- Role
insert into role (id, code, name, status) values (gen_random_uuid(), 'ROLE_SYSTEM_ADMIN', 'ROLE_SYSTEM_ADMIN', 1);

-- Role Permission
insert into role_permission (id, role_id, permission_id) select gen_random_uuid(), (select id from role where code = 'ROLE_SYSTEM_ADMIN'), id from permission limit 100;
