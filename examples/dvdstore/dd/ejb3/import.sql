insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (1, 'Action');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (2, 'Foreign');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (3, 'Children');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (4, 'Comedy');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (5, 'Documentary');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (6, 'Classics');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (7, 'Drama');

insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (1, 2, 'Life is Beautiful', 'Roberto Benini', 12.0);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (2, 3, 'Finding Nemo', 'Albert Brooks', 22.49);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (3, 5, 'March of the Penguins', 'Morgan Freeman', 16.98);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (4, 1, 'Indiana Jones and the Temple of Doom', 'Harisson Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (5, 1, 'Clear and Present Danger', 'Harisson Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (6, 6, 'Roman Holiday', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (7, 6, 'Breakfast at Tiffany''s', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (8, 6, 'Sabrina', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (9, 4, 'Sabrina', 'Harrison Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (10, 1, 'Kill Bill Vol. 1', 'Uma Thurman', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (11, 1, 'Kill Bill Vol. 2', 'Uma Thurman', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (12, 7, 'Lost in Translation', 'Bill Murray', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (13, 7, 'Broken Flowers', 'Bill Murray', 19.99);

insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (14,4,'Better Off Dead','John Cusak',8.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (15,4,'Grosse Pointe Blank','John Cusak',11.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (16,4,'High Fidelity','John Cusak',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (17,7,'Somewhere in Time','Christopher Reeve',11.24);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (18,1,'Superman - The Movie','Christopher Reeve',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (19,1,'Superman II','Christopher Reeve',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (20,1,'Superman III','Christopher Reeve',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (21,1,'Superman IV - The Quest for Peace','Christopher Reeve',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (22,2,'Run Lola Run','Franke Potente',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (23,2,'Chungking Express','Faye Wong',9.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (24,2,'2046','Tony Leung',19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (25,2,'In the Mood for Love','Tony Leung',23.96);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (26,2,'Hero','Jet Li',18.75);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (27,7,'Titanic','Kate Winslet',19.49);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (28,7,'Sense and Sensibility','Kate Winslet',14.96);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (29,7,'Finding Neverland','Johnny Depp',22.49);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (30,3,'Charlie and the Chocolate Factory','Johnny Depp',15.98);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (31,7,'Edward Scissorhands','Johnny Depp',8.98);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (32,1,'Fight Club','Edward Norton',14.98);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (33,3,'Lemony Snicket''s A Series of Unfortunate Events','Jim Carrey',8.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (34,7,'Eternal Sunshine of the Spotless Mind','Jim Carrey',14.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (35,7,'The Truman Show','Jim Carrey',15.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (36,4,'Bruce Almighty','Jim Carrey',8.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (37,4,'Napoleon Dynamite','Jon Heder',12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (38,4,'The 40 Year Old Virgin','Steven Carell',19.98);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (39,2,'Amelie','Audrey Tautou',18.76);

insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (1, 1, 10, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (2, 2, 15, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (3, 3, 77, 2);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (4, 4, 67, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (5, 5, 57, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (6, 6, 44, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (7, 7, 88, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (8, 8, 99, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (9, 9, 56, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (10, 10, 102, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (11, 11, 49, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (12, 12, 12, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (13, 13, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (14, 14, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (15, 15, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (16, 16, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (17, 17, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (18, 18, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (19, 19, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (20, 20, 102, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (21, 21, 99, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (22, 22, 12, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (23, 23, 72, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (24, 24, 17, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (25, 25, 33, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (26, 26, 47, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (27, 27, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (28, 28, 79, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (29, 29, 47, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (30, 30, 102, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (31, 31, 94, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (32, 32, 12, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (33, 33, 77, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (34, 34, 87, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (35, 35, 27, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (36, 36, 91, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (37, 37, 58, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (38, 38, 78, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (39, 39, 69, 0);



INSERT INTO USERS (USERID,TYPE,USERNAME,PASSWORD,FIRSTNAME,LASTNAME) VALUES (1,'admin','manager','password','Albus', 'Dumblebore')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (2,'customer','Harry','Potter','4608499546 Dell Way','','QSDPAGD','SD',24101,'US',1,'ITHOMQJNYX@dell.com','4608499546',1,'1979279217775911',03,2012,'user1','password',55,100000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (3,'customer','Hermione','Granger','5119315633 Dell Way','','YNCERXJ','AZ',11802,'US',1,'UNUKXHJVXB@dell.com','5119315633',1,'3144519586581737',11,2012,'user2','password',80,40000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (4,'customer','Ron','Weasley','6297761196 Dell Way','','LWVIFXJ','OH',96082,'US',1,'LYYSHTQJRE@dell.com','6297761196',4,'8728086929768325',12,2010,'user3','password',47,100000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (5,'customer','Neville','Longbottom','9862764981 Dell Way','','HOKEXCD','MS',78442,'US',1,'WQLQHUHLFE@dell.com','9862764981',5,'7160005148965866',09,2009,'user4','password',44,40000,'F')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (6,'customer','Ginny','Weasley','2841895775 Dell Way','','RZQTCDN','AZ',16291,'US',1,'ETBYBNEGUT@dell.com','2841895775',3,'8377095518168063',10,2010,'user5','password',21,20000,'M')

insert into ORDERS (ORDERID,ORDERDATE,USERID,NETAMOUNT,TAX,TOTALAMOUNT,TRACKING,STATUS) VALUES (1,now(),2,14,3,17,'TR7901' ,3)
insert into ORDERS (ORDERID,ORDERDATE,USERID,NETAMOUNT,TAX,TOTALAMOUNT,TRACKING,STATUS) VALUES (2,now(),2,24,5,29,'TR8331' ,3)

insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (1,1,1,1,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (2,2,1,2,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (3,1,2,3,2)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (4,2,2,4,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (5,3,2,5,1)
