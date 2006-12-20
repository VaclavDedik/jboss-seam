insert into Member (memberid, username, password, name) values (1, 'demo', 'demo', 'demo')
insert into MemberRole (roleid, name) values (1, 'user');
insert into MemberRole (roleid, name) values (2, 'admin');

insert into MemberRoles (member_id, role_id) values (1, 1);
insert into MemberRoles (member_id, role_id) values (1, 2);