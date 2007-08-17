-- MySQL magic!
set session sql_mode='PIPES_AS_CONCAT'

-- Minimum Wiki data - can be customized a little (names and such)
insert into USER_PROFILE (USER_PROFILE_ID, OBJ_VERSION, CREATED_ON) values (1, 0, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, USER_PROFILE_ID, CREATED_ON) values (1, 0, 'System', 'Administrator', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@email.tld', true, 1, '2006-06-27 13:45:00')
insert into USER_PROFILE (USER_PROFILE_ID, OBJ_VERSION, CREATED_ON) values (2, 0, '2006-06-27 13:45:00')
insert into USERS (USER_ID, OBJ_VERSION, FIRSTNAME, LASTNAME, USERNAME, PASSWORDHASH, EMAIL, ACTIVATED, USER_PROFILE_ID, CREATED_ON) values (2, 0, 'Anonymous', 'Guest', 'guest', 'guest', 'guest', false, 2, '1976-06-26 13:45:00')

insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (1, 0, 'Administrator', 'admin', 1000, '2006-06-27 13:45:00')
insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (2, 0, 'Guest', 'guest', 0, '2006-06-27 13:45:00')
insert into ROLES (ROLE_ID, OBJ_VERSION, DISPLAY_NAME, NAME, ACCESS_LEVEL, CREATED_ON) values (3, 0, 'Member', 'member', 1, '2006-06-27 13:45:00')

insert into USER_ROLE (USER_ID, ROLE_ID) values (1,1)
insert into USER_ROLE (USER_ID, ROLE_ID) values (2,2)

insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (1, 'DIRECTORY', 1, 1, 1, 0, 'ROOT', 'ROOT', false, '1976-06-26 11:11:11', 1000, 0, 1, 1, 8, 0)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (1, null)
insert into FEED (FEED_ID, OBJ_VERSION, TITLE, AUTHOR, PUBLISHED_ON, DIRECTORY_ID) values (1, 0, 'ROOT', 'Lacewiki', '1976-06-26 11:11:11', 1)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (2, 'DIRECTORY', 1, 1, 2, 1, 0, 'Member Area', 'MemberArea', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 1000, 0, 1, 2, 3, 0)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (2, null)

-- Link protocols, can be customized

insert into LINK_PROTOCOL (LINK_PROTOCOL_ID, OBJ_VERSION, PREFIX, LINK) values (1, 0, 'jbseam', 'http://jira.jboss.com/jira/browse/JBSEAM-[[link]]')
insert into LINK_PROTOCOL (LINK_PROTOCOL_ID, OBJ_VERSION, PREFIX, LINK) values (2, 0, 'hhh', 'http://opensource.atlassian.com/projects/hibernate/browse/HHH-[[link]]')

-- Required system default configuration

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (1, 0, 'wikiPreferences', 'baseUrl', 'http://lacewiki.org')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (2, 0, 'wikiPreferences', 'timeZone', 'CET')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (3, 0, 'wikiPreferences', 'themeName', 'default')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (4, 0, 'wikiPreferences', 'memberAreaId', '2')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (5, 0, 'wikiPreferences', 'defaultDocumentId', '4')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (6, 0, 'wikiPreferences', 'renderPermlinks', false)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (7, 0, 'wikiPreferences', 'permlinkSuffix', '.lace')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (8, 0, 'wikiPreferences', 'purgeFeedEntriesAfterDays', '30')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (9, 0, 'wikiPreferences', 'atSymbolReplacement', '(AT)')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (10, 0, 'wikiPreferences', 'mainMenuLevels', '3')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (11, 0, 'wikiPreferences', 'mainMenuDepth', '3')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (12, 0, 'wikiPreferences', 'mainMenuShowAdminOnly', false)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (20, 0, 'docEditorPreferences', 'minorRevisionEnabled', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (21, 0, 'docEditorPreferences', 'regularEditAreaRows', '15')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (22, 0, 'docEditorPreferences', 'regularEditAreaColumns', '80')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (30, 0, 'userManagementPreferences', 'activationCodeSalt', 'MySecretSalt123')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (31, 0, 'userManagementPreferences', 'passwordRegex', '^[0-9A-Za-z]{6,15}')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (32, 0, 'userManagementPreferences', 'newUserInRole', 'member')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (33, 0, 'userManagementPreferences', 'enableRegistration', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (34, 0, 'userManagementPreferences', 'createHomeAfterUserActivation', false)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (35, 0, 'userManagementPreferences', 'homepageDefaultContent', '<div style="float:right; width:33%;">@@LF@@ <div>[<=userProfile]</div>@@LF@@ <div style="margin-top:15px;">[<=dirMenu]</div>@@LF@@ <div style="margin-top:15px;">[<=recentEntries]</div>@@LF@@</div>@@LF@@<div style="width:65%;">[<=blogDirectory]</div>@@LF@@@@LF@@')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (40, 0, 'lastModifiedDocumentsPreferences', 'numberOfItems', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (41, 0, 'lastModifiedDocumentsPreferences', 'showUsernames', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (42, 0, 'lastModifiedDocumentsPreferences', 'documentTitleLength', '20')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (50, 0, 'blogDirectoryPreferences', 'pageSize', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (51, 0, 'blogDirectoryPreferences', 'recentHeadlines', '10')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (60, 0, 'feedTeasersPreferences', 'teaserTitle', 'Site news feed:')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (61, 0, 'feedTeasersPreferences', 'feedIdentifier', '1')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (62, 0, 'feedTeasersPreferences', 'numberOfTeasers', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (63, 0, 'feedTeasersPreferences', 'truncateDescription', '200')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (64, 0, 'feedTeasersPreferences', 'showAuthor', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (70, 0, 'commentsPreferences', 'listAscending', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (80, 0, 'flashPreferences', 'flashURL', '')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (81, 0, 'flashPreferences', 'objectWidth', 350)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (82, 0, 'flashPreferences', 'objectHeight', 425)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (110, 0, 'dirMenuPreferences', 'menuLevels', '3')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (111, 0, 'dirMenuPreferences', 'menuDepth', '3')

-- Start document
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values  (3, 'DIRECTORY', 1, 1, 3, 1, 0, 'LaceWiki Home', 'LaceWikiHome', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 0, 0, 1, 4, 7, 1)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (3, null)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION)  values (4, 'DOCUMENT', 1, 1, 3, 3, 0, 'Welcome!', 'Welcome', true,'2005-06-27 13:45:00', '2005-06-27 13:45:00', 0, 0, 1, 5, 6, 0)
insert into NODE_DOCUMENT (DOCUMENT_ID, CONTENT, NAME_AS_TITLE, ENABLE_COMMENTS, ENABLE_COMMENT_FORM) values (4, 'You need to login as /admin/ with password /admin/ to change settings.@@LF@@@@LF@@Do not forget to change the admin password!', true, false, false)
update NODE_DIRECTORY set DEFAULT_DOCUMENT_ID = '10' where DIRECTORY_ID='9'
update NODE_DIRECTORY set DEFAULT_DOCUMENT_ID = 4 where DIRECTORY_ID = 3

-- More MySQL magic!
set session sql_mode=''
