--student name: Yue Zhang
--student id: 30976316

--task 3 create fact and dimension tables.
drop table LocationCityDIM;
drop table CategoryDIM;
drop table QuarterDIM;
drop table PriceRangeDIM;
drop table PublicationFact;
drop table TempFact;
drop sequence category_seq_id;
drop sequence location_seq_id;

--table QuarterDIM
create table QuarterDIM(
    QuarterID number(1),
    QuarterPeriod varchar2(20)); 
--insert values manually
insert into QuarterDIM values (1, 'Jan-Mar');
insert into QuarterDIM values (2, 'Apr-Jun');
insert into QuarterDIM values (3, 'Jul-Sep');
insert into QuarterDIM values (4, 'Oct-Dec');

--table PriceRangeDIM
create table PriceRangeDIM(
    PriceRangeID varchar2(1),
    PriceRange varchar2(20)); 
--insert values manually
--L represents Low, M represents Medium, H represents High
insert into PriceRangeDIM values ('L', 'price < $20');
insert into PriceRangeDIM values ('M', '$20 <= price <= $50');
insert into PriceRangeDIM values ('H', 'price > $50');

--table CategoryDIM
create table CategoryDIM as
    select distinct BookCategory as Category from PUBLISH.BOOK1;
--add a surrogate key
alter table CategoryDIM add (CategoryID number(3));
--use sequence to add surrogate key
create sequence category_seq_id
    start with 1
    increment by 1
    maxvalue 99999999
    minvalue 1
    nocycle;
update CategoryDIM set CategoryID = category_seq_id.nextval;

--table LocationCityDIM
create table LocationCityDIM as 
    select distinct CustomerCity as LocationCityName, CustomerCountry as Country
    from PUBLISH.CUSTOMER1;
--add a surrogate key
alter table LocationCityDIM add (LocationCityID number(3));
--use sequence to add surrogate key
create sequence location_seq_id
    start with 1
    increment by 1
    maxvalue 99999999
    minvalue 1
    nocycle;
update LocationCityDIM set LocationCityID = location_seq_id.nextval;

--table PublicationFact
--use TempFact Table because we have manually created attribute
create table TempFact as
select t.transactiondate, b.price, b.bookcategory, c.customercity, t.quantity
from PUBLISH.book1 b, PUBLISH.transaction1 t, PUBLISH.customer1 c
where t.bookisbn = b.bookisbn and t.customerid = c.customerid;
--add QuarterID to TempFact table
alter table TempFact add(QuarterID number(1));
--update QuarterID value in TempFact table
update TempFact set QuarterID = 1
where to_char(transactiondate, 'MM') >= '01'
and to_char(transactiondate, 'MM') <= '03';

update TempFact set QuarterID = 2
where to_char(transactiondate, 'MM') >= '04'
and to_char(transactiondate, 'MM') <= '06';

update TempFact set QuarterID = 3
where to_char(transactiondate, 'MM') >= '07'
and to_char(transactiondate, 'MM') <= '09';

update TempFact set QuarterID = 4
where QuarterID is null;
--add PriceRangeID to TempFact table
alter table TempFact add (PriceRangeID varchar2(1));
--update PriceRangeID value in TempFact table
update TempFact set PriceRangeID = 'L'
where price < 20;

update TempFact set PriceRangeID = 'H'
where price > 50;

update TempFact set PriceRangeID = 'M'
where PriceRangeID is null;
--add CategoryID to TempFact table
alter table TempFact add(CategoryID number(3));
--update CategoryID value in TempFact table
update TempFact tf
set tf.CategoryID = (select c.categoryid
                        from CategoryDIM c
                        where c.category = tf.bookcategory);
--add LocationCityID to TempFact table
alter table TempFact add(LocationCityID number(3));
--update LocationCityID value in TempFact table
update TempFact tf
set tf.LocationCityID = (select l.locationcityid
                        from LocationCityDIM l
                        where l.locationcityname = tf.customercity);
--create actual PublicationFact table
create table PublicationFact as
select CategoryID, QuarterID, LocationCityID, PriceRangeID, sum(Quantity) as Num_of_Books_Purchased
from TempFact
group by CategoryID, QuarterID, LocationCityID, PriceRangeID;

COMMIT;
                        
--select statement to see table content
select * from QuarterDIM;
select * from PriceRangeDIM;
select * from CategoryDIM;
select * from LocationCityDIM;
select * from PublicationFact;










