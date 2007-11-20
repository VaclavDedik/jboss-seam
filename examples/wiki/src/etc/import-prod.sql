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

insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (1, 'DIRECTORY', 1, 1, 1, 0, 'ROOT', 'ROOT', false, '1976-06-26 11:11:11', 1000, 0, 1, 1, 14, 0)
insert into FEED (FEED_ID, OBJ_VERSION, TITLE, AUTHOR, PUBLISHED_ON, DIRECTORY_ID) values (1, 0, 'ROOT', 'Lacewiki', '1976-06-26 11:11:11', 1)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (2, 'DIRECTORY', 1, 1, 2, 1, 0, 'Member Area', 'MemberArea', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 1000, 0, 1, 2, 3, 0)

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
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (13, 0, 'wikiPreferences', 'showDocumentCreatorHistory', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (14, 0, 'wikiPreferences', 'showTags', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (15, 0, 'wikiPreferences', 'helpArea', 'Help')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (20, 0, 'docEditorPreferences', 'minorRevisionEnabled', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (21, 0, 'docEditorPreferences', 'regularEditAreaRows', '15')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (22, 0, 'docEditorPreferences', 'regularEditAreaColumns', '80')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (30, 0, 'userManagementPreferences', 'activationCodeSalt', 'MySecretSalt123')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (31, 0, 'userManagementPreferences', 'passwordRegex', '^[0-9A-Za-z]{6,15}')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (32, 0, 'userManagementPreferences', 'newUserInRole', 'member')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (33, 0, 'userManagementPreferences', 'enableRegistration', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (34, 0, 'userManagementPreferences', 'createHomeAfterUserActivation', false)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (35, 0, 'userManagementPreferences', 'homepageDefaultContent', 'This is your homepage, login to edit it.')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (40, 0, 'lastModifiedDocumentsPreferences', 'numberOfItems', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (41, 0, 'lastModifiedDocumentsPreferences', 'showUsernames', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (42, 0, 'lastModifiedDocumentsPreferences', 'documentTitleLength', '20')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (50, 0, 'blogDirectoryPreferences', 'pageSize', '5')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (60, 0, 'feedTeasersPreferences', 'teaserTitle', 'Site news feed:')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (61, 0, 'feedTeasersPreferences', 'feedIdentifier', '1')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (62, 0, 'feedTeasersPreferences', 'numberOfTeasers', '5')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (63, 0, 'feedTeasersPreferences', 'truncateDescription', '200')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (64, 0, 'feedTeasersPreferences', 'showAuthor', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (70, 0, 'commentsPreferences', 'listAscending', true)
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (71, 0, 'commentsPreferences', 'enableByDefault', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (80, 0, 'flashPreferences', 'flashURL', '')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (81, 0, 'flashPreferences', 'objectWidth', '425')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (82, 0, 'flashPreferences', 'objectHeight', '355')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, STRING_VALUE)   values (83, 0, 'flashPreferences', 'allowedDomains', 'video.google.com, youtube.com')

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (110, 0, 'dirMenuPreferences', 'menuLevels', '3')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (111, 0, 'dirMenuPreferences', 'menuDepth', '3')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (112, 0, 'dirMenuPreferences', 'showSubscribeIcon', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (120, 0, 'blogRecentEntriesPreferences', 'recentHeadlines', '10')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (121, 0, 'blogRecentEntriesPreferences', 'truncateItemText', '40')
insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (123, 0, 'blogRecentEntriesPreferences', 'showSubscribeIcon', true)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, BOOLEAN_VALUE)  values (130, 0, 'blogArchivePreferences', 'showSubscribeIcon', false)

insert into PREFERENCE(PREF_ID, OBJ_VERSION, COMPONENT_NAME, PROPERTY_NAME, LONG_VALUE)     values (140, 0, 'forumPreferences', 'topicsPerPage', '20')

-- Start document
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values  (3, 'DIRECTORY', 1, 1, 3, 1, 0, 'LaceWiki Home', 'LaceWikiHome', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 0, 0, 1, 4, 7, 1)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (3, null)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION)  values (4, 'DOCUMENT', 1, 1, 3, 3, 0, 'Welcome!', 'Welcome', true,'2005-06-27 13:45:00', '2005-06-27 13:45:00', 0, 0, 1, 5, 6, 0)
insert into NODE_DOCUMENT (DOCUMENT_ID, CONTENT, NAME_AS_TITLE, ENABLE_COMMENTS, ENABLE_COMMENT_FORM, ENABLE_COMMENTS_ON_FEEDS, MACROS) values (4, 'You need to login as /admin/ with password /admin/ to change settings.@@LF@@@@LF@@Do not forget to change the admin password!@@LF@@@@LF@@If you have not updated it already, change the base URL of your installation in the administration screen.', true, false, false, false, '')
update NODE_DIRECTORY set DEFAULT_DOCUMENT_ID = 4 where DIRECTORY_ID = 3

-- Help documents

insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (70, 'DIRECTORY', 1, 1, 70, 1, 0, 'Help', 'Help', false, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 1000, 0, 1, 8, 13, 2)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (70, null)
insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values (71, 'DIRECTORY', 1, 1, 70, 70, 0, 'Working with documents', 'WorkingWithDocuments', true, '2005-06-27 13:45:00', '2005-06-27 13:45:00', 1000, 0, 1, 9, 12, 0)
insert into NODE_DIRECTORY (DIRECTORY_ID, DEFAULT_DOCUMENT_ID) values (71, null)

insert into NODE (NODE_ID, NODE_TYPE, NODE_REVISION, CREATED_BY_USER_ID, AREA_NR, PARENT_NODE_ID, OBJ_VERSION, NAME, WIKINAME, MENU_ITEM, CREATED_ON, LAST_MODIFIED_ON, WRITE_ACCESS_LEVEL, READ_ACCESS_LEVEL, NS_THREAD, NS_LEFT, NS_RIGHT, DISPLAY_POSITION) values  (72, 'DOCUMENT', 1, 1, 70, 71, 0, 'Wiki Text Markup', 'WikiTextMarkup', true, '2007-04-01 13:02:03', '2007-04-01 13:02:03', 1000, 0, 1, 10, 11, 0)
insert into NODE_DOCUMENT (DOCUMENT_ID, CONTENT, NAME_AS_TITLE, ENABLE_COMMENTS, ENABLE_COMMENT_FORM, ENABLE_COMMENTS_ON_FEEDS, MACROS) values (72, 'Most content on this website (blogs, blog comments, wiki pages, user profiles) is rendered using the [Seam Text=>http://docs.jboss.com/seam/latest/reference/en/html/text.html] engine. If you are creating content, it helps to know a few simple tricks.@@LF@@@@LF@@++ Basic formatting@@LF@@@@LF@@You can emphasize words using /italics/, *bold*, _underline_, ~strikeout~ or ^superscript^.@@LF@@@@LF@@`You can emphasize words using @@LF@@/italics/, *bold*, _underline_, ~strikeout~ or ^superscript^.`@@LF@@@@LF@@But if you really want to type a special character such as \/, \* or \+, you need to escape it with a \\.@@LF@@@@LF@@`But if you really want to type a special character @@LF@@such as \/, \* or \+, you need to escape it with a \\.`@@LF@@@@LF@@Alternatively, you can use special characters freely inside |monospace text|.@@LF@@@@LF@@`Alternatively, you can use special characters freely @@LF@@inside |monospace text|.`@@LF@@@@LF@@++ Block formatting@@LF@@@@LF@@Of course, you can also use "inline quotes".@@LF@@@@LF@@"And block quotes."@@LF@@@@LF@@And split text across several paragraphs.@@LF@@@@LF@@`Of course, you can also use "inline quotes".@@LF@@@@LF@@"And block quotes."@@LF@@@@LF@@And split text across several paragraphs.`@@LF@@@@LF@@You can create@@LF@@@@LF@@= unorderedlists@@LF@@= of stuff@@LF@@= like this@@LF@@@@LF@@or@@LF@@@@LF@@# numbered lists@@LF@@# of other things@@LF@@@@LF@@`You can create@@LF@@@@LF@@= unordered lists@@LF@@= of stuff@@LF@@= like this@@LF@@@@LF@@or@@LF@@@@LF@@# numbered lists@@LF@@# of other things`@@LF@@@@LF@@A third option for embedding text that uses special characters is to use a code block, delimited by backticks. For example:@@LF@@@@LF@@`for (int i=0; i<100; i++) {@@LF@@   log.info("Hello world!");@@LF@@}`@@LF@@@@LF@@+ Here is a first-level heading@@LF@@@@LF@@Here is a normal paragraph.@@LF@@@@LF@@++ Here is a second-level heading@@LF@@@@LF@@And another paragraph.@@LF@@@@LF@@`+ Here is a first-level heading@@LF@@@@LF@@Here is a normal paragraph.@@LF@@@@LF@@++ Here is a second-level heading@@LF@@@@LF@@And another paragraph.`@@LF@@@@LF@@++ Links@@LF@@@@LF@@The wiki has powerful handling for links. @@LF@@@@LF@@You can create a simple link to [=>http://hibernate.org] or attach the link to [some text=>http://hibernate.org].@@LF@@@@LF@@`You can create a simple link to [=>http://hibernate.org] @@LF@@or attach the link to [some text=>http://hibernate.org].`@@LF@@@@LF@@You can easily link to [=>wiki://2359] which is actually [this page=>wiki://2359] or to [any other page=>wiki://1449] on the wiki.@@LF@@@@LF@@<pre>You can easily link to \[\=\>Seam Text\] which is actually @@LF@@\[this page\=\>Seam Text\] or to \[any other page\=\>Bloggers\|Gavin\] @@LF@@on the wiki.</pre>@@LF@@@@LF@@You can embed images like this:@@LF@@@@LF@@[=>wiki://2280]@@LF@@@@LF@@<pre>\[\=\>Bloggers\|Copper Ridge\]</pre>@@LF@@@@LF@@You can even link to a [Hibernate JIRA issue=>hhh://2702], or a [Seam JIRA issue=>jbseam://1920].@@LF@@@@LF@@`You can even link to a [Hibernate JIRA issue=>hhh://2702], @@LF@@or a [Seam JIRA issue=>jbseam://1920].`@@LF@@@@LF@@++ Embedded HTML@@LF@@@@LF@@You can even use <i>many</i> HTML tags directly, but <b>not</b> tags that would create a security vulnerability!@@LF@@@@LF@@`You can even use <i>many</i> HTML tags directly, @@LF@@but <b>not</b> tags that would create a @@LF@@security vulnerability!`', true, false, false, false, '')

-- More MySQL magic!
set session sql_mode=''
