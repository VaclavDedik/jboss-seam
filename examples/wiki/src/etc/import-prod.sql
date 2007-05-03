-- MySQL magic!
set session sql_mode='PIPES_AS_CONCAT'

-- Minimum Wiki data - can be customized a little (names and such)
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (1, 0, 'System', 'Administrator', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@email.tld', true, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, CREATED_ON) values (2, 0, 'Anonymous', 'Guest', 'guest', 'guest', 'guest', false, '1976-06-26 13:45:00')

insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (1, 0, 'Administrator', 'admin', 1000, '2006-06-27 13:45:00')
insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (2, 0, 'Guest', 'guest', 0, '2006-06-27 13:45:00')
insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (3, 0, 'Member', 'member', 1, '2006-06-27 13:45:00')

insert into USER_ROLE (USER_ID, ROLE_ID) values (1,1)
insert into USER_ROLE (USER_ID, ROLE_ID) values (2,2)

insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL) values (1, 'DIRECTORY', 1, 1, 1, 0, null, 0, 'ROOT', 'ROOT', false, '1976-06-26 11:11:11', 1000, 0)
insert into FEED (FEED_ID, OBJ_VERSION, TITLE, DESCRIPTION, AUTHOR, COPYRIGHT, PUBLISHED_ON, DIRECTORY_ID) values (1, 0, 'ROOT', null, 'Lacewiki', null, '1976-06-26 11:11:11', 1)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, NODE_POSITION, DEFAULT_DOCUMENT_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL) values (2, 'DIRECTORY', 1, 1, 2, 1, 0, null, 0, 'Member Area', 'MemberArea', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 1000, 0)

-- Link protocols, can be customized

insert into LINK_PROTOCOL (LINK_PROTOCOL_ID, OBJ_VERSION, PREFIX, LINK) values (1, 0, 'jbseam', 'http://jira.jboss.com/jira/browse/JBSEAM-[[link]]')
insert into LINK_PROTOCOL (LINK_PROTOCOL_ID, OBJ_VERSION, PREFIX, LINK) values (2, 0, 'hhh', 'http://opensource.atlassian.com/projects/hibernate/browse/HHH-[[link]]')

-- Required system default configuration

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (1, 0, 'wikiPreferences', 'baseUrl', 'http://www.lacewiki.org')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (2, 0, 'wikiPreferences', 'timeZone', 'CET')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (3, 0, 'wikiPreferences', 'themeName', 'default')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (4, 0, 'wikiPreferences', 'memberAreaId', '2')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (5, 0, 'wikiPreferences', 'defaultDocumentId', '4')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (6, 0, 'wikiPreferences', 'renderPermlinks', false)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (7, 0, 'wikiPreferences', 'permlinkSuffix', '.lace')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (8, 0, 'wikiPreferences', 'purgeFeedEntriesAfterDays', '30')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (9, 0, 'docEditorPreferences', 'minorRevisionEnabled', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (10, 0, 'docEditorPreferences', 'regularEditAreaRows', '25')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (11, 0, 'docEditorPreferences', 'regularEditAreaColumns', '80')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (14, 0, 'userManagementPreferences', 'activationCodeSalt', 'MySecretSalt123')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (15, 0, 'userManagementPreferences', 'passwordRegex', '^[0-9A-Za-z]{6,15}')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (16, 0, 'userManagementPreferences', 'newUserInRole', 'member')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (17, 0, 'userManagementPreferences', 'enableRegistration', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (18, 0, 'userManagementPreferences', 'createHomeAfterUserActivation', false)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (19, 0, 'lastModifiedDocumentsPreferences', 'numberOfItems', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (20, 0, 'lastModifiedDocumentsPreferences', 'showUsernames', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (21, 0, 'lastModifiedDocumentsPreferences', 'documentTitleLength', '20')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (22, 0, 'blogDirectoryPreferences', 'pageSize', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (23, 0, 'blogDirectoryPreferences', 'recentHeadlines', '10')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (24, 0, 'feedTeasersPreferences', 'teaserTitle', 'Site news feed:')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (25, 0, 'feedTeasersPreferences', 'feedIdentifier', '1')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (26, 0, 'feedTeasersPreferences', 'numberOfTeasers', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (27, 0, 'feedTeasersPreferences', 'truncateDescription', '200')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (28, 0, 'feedTeasersPreferences', 'showAuthor', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (29, 0, 'commentsPreferences', 'listAscending', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (30, 0, 'flashPreferences', 'flashURL', '')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (31, 0, 'flashPreferences', 'objectWidth', 350)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (32, 0, 'flashPreferences', 'objectHeight', 425)

-- Start document
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL) values (3, 'DIRECTORY', 1, 1, 3, 1, 1, 0, 'LaceWiki Home', 'LaceWikiHome', true, '1976-06-16 13:45:00', '1976-06-16 13:45:00', 0, 0)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, NODE_POSITION, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CONTENT, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NAME_AS_TITLE) values  (4, 'DOCUMENT', 1, 1, 3, 3, 0, 0, 'Welcome to LaceWiki!', 'WelcomeToLaceWiki', true, 'You need to login as /admin/ with password /admin/ to change settings.@@LF@@@@LF@@Do not forget to change the admin password!', '2005-06-27 13:45:00', '2005-06-27 13:45:00', 0, 0, true)
update NODE set DEFAULT_DOCUMENT_ID = 4 where NODE_ID = 3

-- More MySQL magic!
set session sql_mode=''
