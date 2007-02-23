insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON) values (1, 'DIRECTORY', 1, 0, null, 0, 'ROOT', 'ROOT', false, '1976-06-26 11:11:11')

insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON) values (2, 'DIRECTORY', 2, 1, 0, null, 0, 'News', 'News', true, '2009-06-27 13:45:00', '2009-06-27 13:45:00' )
insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON) values (3, 'DIRECTORY', 3, 1, 1, null, 0, 'Documentation Area', 'DocumentationArea', true, '2009-06-27 13:45:00', '2009-06-27 13:45:00')
insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON) values (4, 'DIRECTORY', 4, 1, 2, null, 0, 'Download Overview', 'DownloadOverview', false, '2009-06-27 13:45:00', '2009-06-27 13:45:00')

insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON) values (5, 'DOCUMENT', 2, 2, 0, 0, 'Welcome!', 'Welcome', false,'+Welcome!@@LF@@@@LF@@Login with admin-admin or member-member.@@LF@@@@LF@@[A link=>wiki://6] to some page, a [broken link=>wiki://123], a normal link to [=>wiki://7], a link to [=>some page that does not exist] and a link to a [different area=>Documentation Area|FooBar].@@LF@@@@LF@@Seam Text is a human-friendly language for formatting text in blogs, forums and wikis. It''s easy to enter text in *bold*, /italic/, |monospace|, ~deleted~ or ^superscript^; you can easily enter links, lists, quotes and code blocks.@@LF@@@@LF@@[This is a link.=>http://hibernate.org]@@LF@@@@LF@@You can *not* enter HTML entities, even escaped: \&amp; \&lt; \&gt; \&quot; \&nbsp;@@LF@@@@LF@@And even emoticons: ;) :-) :-{ ;-)@@LF@@@@LF@@Here is an ordered list:@@LF@@@@LF@@#JBoss@@LF@@#Seam@@LF@@#Hibernate@@LF@@@@LF@@And this is an unordered list:@@LF@@@@LF@@=jBPM@@LF@@=Drools@@LF@@=Ajax4JSF@@LF@@=Facelets@@LF@@@@LF@@"Here is a quote from someone else"@@LF@@@@LF@@`<p>Here is some code</p>`@@LF@@@@LF@@And some plain HTML (restricted subset):@@LF@@@@LF@@<table style="border:1px solid blue;"><tr><td>Foo</td><td>Bar</td></tr></table>','2009-06-27 13:45:00', '2009-06-27 13:45:00')
insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON) values (6, 'DOCUMENT', 2, 2, 1, 0, 'Foo', 'Foo', true, 'Welcome *to the* /Wiki/!', '2009-06-27 13:45:00', '2009-06-27 13:45:00')
insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON) values (7, 'DOCUMENT', 2, 2, 2, 0, 'Bar', 'Bar', true, 'Welcome *to the* /Wiki/!', '2009-06-27 13:45:00', '2009-06-27 13:45:00')

insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON) values (8, 'DIRECTORY', 3, 3, 0, null, 0, 'FAQ', 'FAQ', true, '2009-06-27 13:45:00', '2009-06-27 13:45:00')

insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON)  values (9, 'DOCUMENT', 3, 3, 1, 0, 'FooBar', 'FooBar', true, 'Another one', '2009-06-27 13:45:00', '2009-06-27 13:45:00')
insert into NODE (NODE_ID, NODE_TYPE, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON)  values (10, 'DOCUMENT', 3, 8, 0, 0, 'An FAQ Page', 'AnFAQPage', true, 'FAQ!', '2009-06-27 13:45:00', '2009-06-27 13:45:00')

insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (1, 0, 'Admin', 'Admin', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (2, 0, 'Member', 'Member', 'member', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')

insert into ROLE (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, CREATED_ON) values (1, 0, 'Administrator', 'admin', '2006-06-27 13:45:00')
insert into ROLE (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, CREATED_ON) values (2, 0, 'Member', 'member', '2006-06-27 13:45:00')

insert into USER_ROLE (USER_ID, ROLE_ID) values (1,1)
insert into USER_ROLE (USER_ID, ROLE_ID) values (2,2)


insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (3, 0, 'Foo1', 'Foo1', 'foo1', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (4, 0, 'Foo2', 'Foo2', 'foo2', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (5, 0, 'Foo3', 'Foo3', 'foo3', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (6, 0, 'Foo4', 'Foo4', 'foo4', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (7, 0, 'Foo5', 'Foo5', 'foo5', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (8, 0, 'Foo6', 'Foo6', 'foo6', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (9, 0, 'Foo7', 'Foo7', 'foo7', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (10, 0, 'Foo8', 'Foo8', 'foo8', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (11, 0, 'Foo9', 'Foo9', 'foo9', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (12, 0, 'Foo10', 'Foo10', 'foo10', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (13, 0, 'Foo11', 'Foo11', 'foo11', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (14, 0, 'Bar1', 'Bar1', 'bar1', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (15, 0, 'Bar2', 'Bar2', 'bar2', 'aa08769cdcb26674c6706093503ff0a3', 'member@email.tld', true, '2006-06-27 13:45:00')

insert into USER_ROLE (USER_ID, ROLE_ID) values (3,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (4,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (5,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (6,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (7,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (8,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (9,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (10,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (11,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (12,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (13,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (14,2)
insert into USER_ROLE (USER_ID, ROLE_ID) values (15,2)

