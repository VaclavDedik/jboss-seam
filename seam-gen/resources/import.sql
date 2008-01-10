insert into UserAccount (accountid, username, enabled, accounttype) values (1, 'admin', 1, 1);
insert into UserAccount (accountid, username, passwordhash, enabled, accounttype) values (2, 'demo', '04ac53975612d07ad97107ef524589e5', 1, 0);

insert into AccountMembership (accountid, memberof) values (2, 1);