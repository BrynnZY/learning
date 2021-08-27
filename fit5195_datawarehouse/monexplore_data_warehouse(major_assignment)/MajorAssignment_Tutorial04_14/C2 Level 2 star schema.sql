
---create the EventSizeDim
drop table eventsizedim;
create table eventsizeDim 
(event_size_ID varchar2(2) not null,
event_size varchar2(20) not null,
event_size_desc varchar2(50) not null
);

insert into EventSizeDim values('1', 'Short', 'less than 10 people');
insert into EventSizeDim values('2', 'Medium','Between 11 and 30 people');
insert into EventSizeDim values('3', 'Large','More than 30 people');

select * from eventsizedim;

--- create OccupationDim
drop table OccupationDim;
create table OccupationDim 
(occupationID varchar2(2) not null,
occupation_desc varchar2(20) not null
);

insert into occupationdim values('1', 'Student');
insert into occupationdim values('2', 'Staff');
insert into occupationdim values('3', 'Conmmunity');

---create locationDim
drop table locationDim;
create table locationdim as 
select distinct address_state as state from address;

--- create maritaldim
drop table maritaldim;
create table maritaldim
( marital_id varchar2(2) not null,
marital_desc varchar2(50) not null
);

insert into maritaldim values ('1', 'Not married');
insert into maritaldim values ('2', 'Divorced');
insert into maritaldim values ('3', 'Married');



--- create agegroupdim
drop table agegroupdim;
create table agegroupdim
( age_group_id varchar2(2) not null,
age_group_name varchar2(50) not null,
age_group_desc varchar2(50) not null
);
insert into agegroupdim values ('1', 'Child', '0-16 years old');
insert into agegroupdim values ('2', 'Young adults', '17-30 years old');
insert into agegroupdim values ('3', 'Middle-aged adults', '31-45 years old');
insert into agegroupdim values ('4', 'Old-aged adults', 'Over 45 years old');

select * from agegroupdim;


---- create mediadim
drop table mediadim;
create table mediadim as
select * from media_channel;

---- create topicdim
drop table topicdim;
create table topicdim as
select * from topic;

---- create timedim
drop table time_1;
drop table time_2;
drop table time_3;
drop table time_4;
drop table time_5;
drop table timedim;

create table time_1 as 
select to_char(subscription_date, 'MON-YYYY') as timeid,
to_char(subscription_date, 'MON') as month,
to_char(subscription_date, 'YYYY') as year
from subscription;

create table time_2 as 
select to_char(event_start_date,'MON-YYYY') as timeid,
to_char(event_start_date, 'MON') as month,
to_char(event_start_date, 'YYYY') as year from event;

create table time_3 as 
select to_char(event_end_date,'MON-YYYY') as timeid,
to_char(event_end_date, 'MON') as month,
to_char(event_end_date, 'YYYY') as year
from event;

create table time_4 as 
select to_char(att_date,'MON-YYYY') as timeid,
to_char(att_date, 'MON') as month,
to_char(att_date, 'YYYY') as year
from attendance;

create table time_5 as 
select to_char(reg_date, 'MON-YYYY') as timeid,
to_char(reg_date, 'MON') as month,
to_char(reg_date, 'YYYY') as year
from registration;


insert into time_1 select * from time_2;
insert into time_1 select * from time_3;
insert into time_1 select * from time_4;
insert into time_1 select * from time_5;

create table timedim as
select distinct * from time_1;


---  create programdim
drop table programdim;
create table programdim as
select program_id, program_name, program_details, program_fee,program_length, program_frequency
from program;

alter table programdim
add program_length_type varchar2(10)
add program_length_type_desc varchar2(50);

update programdim
set
program_length_type = 'Short', program_length_type_desc = 'Less than 3 sessions' 
where program_length in ('1 session','2 sessions');

update programdim
set
program_length_type = 'Medium', program_length_type_desc = 'Between 3 to 6 sessions' 
where program_length in ('3 sessions','4 sessions','5 sessions','6 sessions');

update programdim
set
program_length_type = 'Long', program_length_type_desc = 'More than 6 sessions' 
where program_length not in ('1 session','2 sessions','3 sessions','4 sessions','5 sessions','6 sessions');

alter table programdim 
drop column program_length;

--- create subscriptionFact 
drop table subscriptionFact;
drop table subscriptionFacttemp;

create table subscriptionFacttemp as 
select s.subscription_id, pr.program_id, to_char(s.subscription_date,'MON-YYYY') as timeid, pe.person_job, ad.address_state as state,
pe.person_marital_status, pe.person_age
from program pr, subscription s, person pe, address ad
where pr.program_id = s.program_id and s.person_id = pe.person_id
and pe.address_id = ad.address_id; 


alter table subscriptionFacttemp
add occupationid varchar2(2)
add marital_id varchar2(2)
add age_group_id varchar2(2);

update subscriptionFacttemp
set
occupationid = '1' where person_job = 'Student';

update subscriptionFacttemp
set
occupationid = '2' where person_job = 'Staff';

update subscriptionFacttemp
set
occupationid = '3' where person_job not in('Student','Staff');

update subscriptionFacttemp
set
marital_id = '1' where person_marital_status = 'Not married';

update subscriptionFacttemp
set
marital_id = '2' where person_marital_status = 'Divorced';

update subscriptionFacttemp
set
marital_id = '3' where person_marital_status = 'Married';

update subscriptionFacttemp
set
age_group_id = '1' where person_age between '0' and '16';

update subscriptionFacttemp
set
age_group_id = '2' where person_age between '17' and '30';

update subscriptionFacttemp
set
age_group_id = '3' where person_age between '31' and '45';

update subscriptionFacttemp
set
age_group_id = '4' where person_age > 45;

select * from subscriptionFacttemp;

create table subscriptionFact as 
select program_id, timeid, state ,occupationid, marital_id, age_group_id, 
count(subscription_id) as Num_of_People_subscribed
from subscriptionFacttemp
group by program_id, timeid, state ,occupationid, marital_id, age_group_id;


---- create registrationFact
drop table registrationFact;
drop table registrationFacttemp;

create table registrationFacttemp as 
select re.reg_id, to_char(re.reg_date, 'MON-YYYY') as timeid, pe.person_job, ad.address_state as state,
pe.person_marital_status, pe.person_age, re.media_id
from registration re, person pe, address ad, media_channel me
where re.media_id = me.media_id and re.person_id = pe.person_id
and pe.address_id = ad.address_id; 

alter table registrationFacttemp
add occupationid varchar2(2)
add marital_id varchar2(2)
add age_group_id varchar2(2);

update registrationFacttemp
set
occupationid = '1' where person_job = 'Student';

update registrationFacttemp
set
occupationid = '2' where person_job = 'Staff';

update registrationFacttemp
set
occupationid = '3' where person_job not in('Student','Staff');

update registrationFacttemp
set
marital_id = '1' where person_marital_status = 'Not married';

update registrationFacttemp
set
marital_id = '2' where person_marital_status = 'Divorced';

update registrationFacttemp
set
marital_id = '3' where person_marital_status = 'Married';

update registrationFacttemp
set
age_group_id = '1' where person_age between '0' and '16';

update registrationFacttemp
set
age_group_id = '2' where person_age between '17' and '30';

update registrationFacttemp
set
age_group_id = '3' where person_age between '31' and '45';

update registrationFacttemp
set
age_group_id = '4' where person_age > 45;

create table registrationFACT as 
select timeid, state ,occupationid, marital_id, age_group_id, media_id,
count(reg_id) as Num_of_People_registered
from registrationFacttemp
group by timeid, state ,occupationid, marital_id, age_group_id, media_id;


---- create intersetFact
drop table interestFact;
drop table interestFacttemp;

create table interestFacttemp as 
select person_interest.person_id, pe.person_job, ad.address_state as state,
pe.person_marital_status, pe.person_age, person_interest.topic_id
from person_interest, person pe, address ad, topic
where person_interest.person_id = pe.person_id and person_interest.topic_id = topic.topic_id
and pe.address_id = ad.address_id;

alter table interestFacttemp
add occupationid varchar2(2)
add marital_id varchar2(2)
add age_group_id varchar2(2);

update interestFacttemp
set
occupationid = '1' where person_job = 'Student';

update interestFacttemp
set
occupationid = '2' where person_job = 'Staff';

update interestFacttemp
set
occupationid = '3' where person_job not in('Student','Staff');

update interestFacttemp
set
marital_id = '1' where person_marital_status = 'Not married';

update interestFacttemp
set
marital_id = '2' where person_marital_status = 'Divorced';

update interestFacttemp
set
marital_id = '3' where person_marital_status = 'Married';

update interestFacttemp
set
age_group_id = '1' where person_age between '0' and '16';

update interestFacttemp
set
age_group_id = '2' where person_age between '17' and '30';

update interestFacttemp
set
age_group_id = '3' where person_age between '31' and '45';

update interestFacttemp
set
age_group_id = '4' where person_age > 45;

create table interestFACT as 
select state ,occupationid, marital_id, age_group_id, topic_id,
count(person_id) as Num_of_People_interested
from interestFacttemp
group by state ,occupationid, marital_id, age_group_id, topic_id;



------ create attendencefact 
drop table eventtemp;
create table eventtemp as 
select * from event;

alter table eventtemp
add event_size_id varchar2(2) ;

update eventtemp
set event_size_id = '1' where event_size between '0' and '10';

update eventtemp
set event_size_id = '2' where event_size between '10' and '30';

update eventtemp
set event_size_id = '3' where event_size > 30;

drop table attendanceFact;
drop table attendanceFacttemp;
select * from attendanceFacttemp;

create table attendanceFacttemp as 
select att.att_id, to_char(att.att_date, 'MON-YYYY') as timeid, pe.person_job, ad.address_state as state,
pe.person_marital_status, pe.person_age, pr.program_id, ev.event_size_id, att.att_donation_amount
from program pr, person pe, address ad, eventtemp ev, attendance att
where att.event_id = ev.event_id and ev.program_id = pr.program_id
and att.person_id = pe.person_id
and pe.address_id = ad.address_id; 


alter table attendanceFacttemp
add occupationid varchar2(2)
add marital_id varchar2(2)
add age_group_id varchar2(2);

update attendanceFacttemp
set
occupationid = '1' where person_job = 'Student';

update attendanceFacttemp
set
occupationid = '2' where person_job = 'Staff';

update attendanceFacttemp
set
occupationid = '3' where person_job not in('Student','Staff');

update attendanceFacttemp
set
marital_id = '1' where person_marital_status = 'Not married';

update attendanceFacttemp
set
marital_id = '2' where person_marital_status = 'Divorced';

update attendanceFacttemp
set
marital_id = '3' where person_marital_status = 'Married';

update attendanceFacttemp
set
age_group_id = '1' where person_age between '0' and '16';

update attendanceFacttemp
set
age_group_id = '2' where person_age between '17' and '30';

update attendanceFacttemp
set
age_group_id = '3' where person_age between '31' and '45';

update attendanceFacttemp
set
age_group_id = '4' where person_age > 45;

drop table attendanceFact;
create table attendanceFact as 
select program_id, timeid, event_size_id, state ,occupationid, marital_id, age_group_id, 
count(att_id) as Num_of_People_attended,
sum(att_donation_amount) as Total_donation
from attendanceFacttemp
group by program_id, timeid, event_size_id, state ,occupationid, marital_id, age_group_id;

select * from interestfact;
select * from registrationfact;
select * from attendancefact;
select * from subscriptionfact;

select * from programdim;
select * from eventsizedim;
select * from timedim;
select * from occupationdim;
select * from locationdim;
select * from maritaldim;
select * from agegroupdim;
select * from mediadim;
select * from topicdim;

