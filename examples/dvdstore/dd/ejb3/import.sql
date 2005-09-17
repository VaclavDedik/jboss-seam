insert into categories (category, categoryname) values (1, 'Action');
insert into categories (category, categoryname) values (2, 'Adult');
insert into categories (category, categoryname) values (3, 'Children');
insert into categories (category, categoryname) values (4, 'Comedy');
insert into categories (category, categoryname) values (5, 'Documentary');
insert into categories (category, categoryname) values (6, 'Classics');
insert into categories (category, categoryname) values (7, 'Drama');

insert into products (prod_id, category, title, actor, price) values (1, 2, 'Indahouse', 'Ali G', 12.0);
insert into products (prod_id, category, title, actor, price) values (2, 3, 'The Lion King', '', 12.50);
insert into products (prod_id, category, title, actor, price, common_prod_id) values (3, 5, 'March of the Penguins', '', 19.99, 2);
insert into products (prod_id, category, title, actor, price) values (4, 1, 'Indiana Jones and the Temple of Doom', 'Harisson Ford', 19.99);
insert into products (prod_id, category, title, actor, price) values (5, 1, 'Clear and Present Danger', 'Harisson Ford', 19.99);
insert into products (prod_id, category, title, actor, price) values (6, 6, 'Roman Holiday', 'Audrey Hepburn', 12.99);
insert into products (prod_id, category, title, actor, price) values (7, 6, 'Breakfast at Tiffany''s', 'Audrey Hepburn', 12.99);
insert into products (prod_id, category, title, actor, price) values (8, 6, 'Sabrina', 'Audrey Hepburn', 12.99);
insert into products (prod_id, category, title, actor, price) values (9, 4, 'Sabrina', 'Harrison Ford', 19.99);
insert into products (prod_id, category, title, actor, price) values (10, 1, 'Kill Bill Vol. 1', 'Uma Thurman', 19.99);
insert into products (prod_id, category, title, actor, price) values (11, 1, 'Kill Bill Vol. 2', 'Uma Thurman', 19.99);
insert into products (prod_id, category, title, actor, price) values (12, 7, 'Lost in Translation', 'Bill Murray', 19.99);
insert into products (prod_id, category, title, actor, price) values (13, 7, 'Broken Flowers', 'Bill Murray', 19.99);



insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (1, 1, 10, 0);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (2, 2, 15, 5);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (3, 3, 77, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (4, 4, 67, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (5, 5, 57, 9);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (6, 6, 44, 7);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (7, 7, 88, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (8, 8, 99, 8);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (9, 9, 56, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (10, 10, 102, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (11, 11, 994, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (12, 12, 12, 2);
insert into inventory (inv_id, prod_id, quan_in_stock, sales) values (13, 13, 77, 7);



INSERT INTO CUSTOMERS (CUSTOMERID,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CREDITCARD,CREDITCARDEXPIRATION,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (1,'VKUUXF','ITHOMQJNYX','4608499546 Dell Way','','QSDPAGD','SD',24101,'US',1,'ITHOMQJNYX@dell.com','4608499546',1,'1979279217775911','2012/03','user1','X03MO1qnZdYdgyfeuILPmQ==',55,100000,'M')
INSERT INTO CUSTOMERS (CUSTOMERID,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CREDITCARD,CREDITCARDEXPIRATION,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (2,'HQNMZH','UNUKXHJVXB','5119315633 Dell Way','','YNCERXJ','AZ',11802,'US',1,'UNUKXHJVXB@dell.com','5119315633',1,'3144519586581737','2012/11','user2','X03MO1qnZdYdgyfeuILPmQ==',80,40000,'M')
INSERT INTO CUSTOMERS (CUSTOMERID,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CREDITCARD,CREDITCARDEXPIRATION,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (3,'JTNRNB','LYYSHTQJRE','6297761196 Dell Way','','LWVIFXJ','OH',96082,'US',1,'LYYSHTQJRE@dell.com','6297761196',4,'8728086929768325','2010/12','user3','X03MO1qnZdYdgyfeuILPmQ==',47,100000,'M')
INSERT INTO CUSTOMERS (CUSTOMERID,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CREDITCARD,CREDITCARDEXPIRATION,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (4,'XMFYXD','WQLQHUHLFE','9862764981 Dell Way','','HOKEXCD','MS',78442,'US',1,'WQLQHUHLFE@dell.com','9862764981',5,'7160005148965866','2009/09','user4','X03MO1qnZdYdgyfeuILPmQ==',44,40000,'F')
INSERT INTO CUSTOMERS (CUSTOMERID,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CREDITCARD,CREDITCARDEXPIRATION,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (5,'PGDTDU','ETBYBNEGUT','2841895775 Dell Way','','RZQTCDN','AZ',16291,'US',1,'ETBYBNEGUT@dell.com','2841895775',3,'8377095518168063','2010/10','user5','X03MO1qnZdYdgyfeuILPmQ==',21,20000,'M')


