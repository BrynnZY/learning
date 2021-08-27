drop table topic;
drop table program;
drop table event;
drop table media_channel;
drop table person;
drop table address;
drop table person_interest;
drop table subscription;
drop table attendance;
drop table registration;
drop table event_marketing;
drop table volunteer;
drop table participant;
drop table follow_up;

create table topic as select * from monexplore.topic;
create table program as select * from monexplore.program;
create table event as select * from monexplore.event;
create table media_channel as select * from monexplore.media_channel;
create table person as select * from monexplore.person;
create table address as select * from monexplore.address;
create table person_interest as select * from monexplore.person_interest;
create table subscription as select * from monexplore.subscription;
create table attendance as select * from monexplore.attendance;
create table registration as select * from monexplore.registration;
create table event_marketing as select * from monexplore.event_marketing;
create table volunteer as select * from monexplore.volunteer;
create table participant as select * from monexplore.participant;
create table follow_up as select * from monexplore.follow_up;

--check duplicate reords
select topic_id, count(*)
from topic
group by topic_id
having count(*) > 1; --no dupilicate

select program_id, count(*)
from program
group by program_id
having count(*) > 1; --no dupilicate

select event_id, count(*)
from event
group by event_id
having count(*) > 1; --no dupilicate

select media_id, count(*)
from media_channel
group by media_id
having count(*) > 1; --no dupilicate

select person_id, count(*)
from person
group by person_id
having count(*) > 1; -- id pe057, pe078, pe021

--check for each record
select * from person
where person_id = 'PE057'; -- dupilicate

select * from person
where person_id = 'PE078'; -- dupilicate

select * from person
where person_id = 'PE021'; -- dupilicate

--clean
drop table person;
create table person as select distinct * from monexplore.person;

select address_id, count(*)
from address
group by address_id
having count(*) > 1; --no dupilicate

select person_id, topic_id, count(*)
from person_interest
group by person_id, topic_id
having count(*) > 1; --no dupilicate

select subscription_id, count(*)
from subscription
group by subscription_id
having count(*) > 1; --id su021, su243

--check for each record
select * from subscription
where subscription_id = 'SU021'; -- dupilicate

select * from subscription
where subscription_id = 'SU243'; -- dupilicate

--clean
drop table subscription;
create table subscription as select distinct * from monexplore.subscription;

select att_id, count(*)
from attendance
group by att_id
having count(*) > 1; --no dupilicate

select reg_id, count(*)
from registration
group by reg_id
having count(*) > 1;

--check pk if is null
select * from topic where topic_id is null;
select * from program where program_id is null;
select * from event where event_id is null;
select * from media_channel where media_id is null; --one null records
--clean
delete from media_channel where media_id is null;

select * from person where person_id is null;
select * from address where address_id is null;
select * from person_interest where topic_id is null or person_id is null;
select * from subscription where subscription_id is null;
select * from attendance where att_id is null;
select * from registration where reg_id is null;

--check for other possible errors: dupilicate columns, null in other columns that affects dw
select * from topic; -- one null in description
--clean
delete from topic where topic_description is null;
select * from program;
select * from event;
select * from media_channel;
select * from person;
select * from address;
select * from person_interest;
select * from subscription;
select * from attendance;
select * from registration;

--check invalid fk values
select * from person
where address_id not in (select address_id from address);

select * from person_interest
where person_id not in (select person_id from person)
or topic_id not in (select topic_id from topic);--T010
--clean
delete from person_interest
where person_id not in (select person_id from person)
or topic_id not in (select topic_id from topic);--T010

select * from subscription
where person_id not in (select person_id from person)
or program_id not in (select program_id from program);

select * from program
where topic_id not in (select topic_id from topic);

select * from event
where program_id not in (select program_id from program);--program id pr000, pr020
--one event must belongs to one program, so we can not just set fk to be null
--delete those invalid fk records
delete from event where program_id in ('PR000', 'PR020');

select * from attendance
where person_id not in (select person_id from person)
or event_id not in (select event_id from event);--event id 101,31
--one attendance must belongs to one event, so we can not just set fk to be null
--also, we do not know the event 31 and 101 information
--so we can not manually add event 31 and 101 to event table
--delete those invalid fk records
delete from attendance
where person_id not in (select person_id from person)
or event_id not in (select event_id from event);

select * from registration
where person_id not in (select person_id from person)
or event_id not in (select event_id from event)
or media_id not in (select media_id from media_channel);--event id 101,31
--one registration must belongs to one event, so we can not just set fk to be null
--also, we do not know the event 31 and 101 information
--so we can not manually add event 31 and 101 to event table
--delete those invalid fk records
delete from registration
where person_id not in (select person_id from person)
or event_id not in (select event_id from event)
or media_id not in (select media_id from media_channel);

--check inconsistent values
--check state name
select distinct address_state from address;--correct
--check not registed but attend 
select * from attendance
where person_id not in (select person_id from registration);--correct
select distinct program_length from program;

--check for incorrect values
------ 1. check the Person table
select * from person;

--- 1.1 check the age form the Person table 
select * from person
where person_age <0 or person_age = 0; --- all correct 

--- 1.2 check gender from the Person table 
select distinct person_gender from person; --- all correct

--- 1.3 check marital status from the Person table
select distinct person_marital_status from person;  --- all correct

------- 2. check the Attendance table
select * from attendance;

--- 2.1 check the date from the Attendance 
select * from attendance
where to_date(att_date, 'DD-MON-RR') > current_date;  ---**** ERROR **** one at 18/oct/21

--- modify
delete from attendance
where to_date(att_date, 'DD-MON-RR') > current_date;

--- 2.2 check amount from Attendance table
select * from attendance  ---**** ERROR **** attid 639:-25, attid1001:-5
where att_donation_amount < 0;
--- modify ......
delete from attendance  
where att_donation_amount < 0;

--- 2.3 check number of people attended
select * from attendance  --- all correct 
where att_num_of_people_attended < 1;

------ 3. check the Subcription table
select * from subscription;

--- 3.1 check the date from the Subcription table
select * from subscription  ---- all correct
where to_date(subscription_date, 'DD-MON-RR') > current_date;

------ 4. check the Registration table
select * from registration;

--- 4.1 check the date from the Registration table
select * from registration  --- all correct 
where to_date(reg_date, 'DD-MON-RR') > current_date;

--- 4.2 check number of people registered
select * from registration  --- all correct 
where reg_num_of_people_registered < 1;

------- 5. check the Program tab
select * from program;

--- 5.1 check the fee from the Program table
select * from program --- all correct 
where program_fee < 0;

--- 6 check the Media table
select * from media_channel;  

--- check the cost from Media table
select * from media_channel --- all correct 
where media_cost < 0;

---- 7 check the Event table
select * from event;

--- 7.1 check the Event size form Event table 
select * from event   ---- **** ERROR **** event id 11:-10, event id 47: -75
where event_size < 0;
--clean
delete from event_marketing where
event_id in (select event_id from event
where event_size < 0);
delete from attendance where
event_id in (select event_id from event
where event_size < 0);
delete from registration where
event_id in (select event_id from event
where event_size < 0);
delete from  event   ---- **** ERROR **** event id 11:-10, event id 47: -75
where event_size < 0;

--- 7.2 check the cost from Event table
select * from event   --- all correct 
where event_cost < 0;

--- 7.3 check the date from Event table
select * from event   --- **** Error **** event id 162, 163
where TO_DATE(event_start_date, 'DD-MON-RR') > TO_DATE(event_end_date, 'DD-MON-RR');
--clean
delete from event_marketing where
event_id in (select event_id from event 
where TO_DATE(event_start_date, 'DD-MON-RR') > TO_DATE(event_end_date, 'DD-MON-RR'));
delete from attendance where
event_id in (select event_id from event 
where TO_DATE(event_start_date, 'DD-MON-RR') > TO_DATE(event_end_date, 'DD-MON-RR'));
delete from registration where
event_id in (select event_id from event 
where TO_DATE(event_start_date, 'DD-MON-RR') > TO_DATE(event_end_date, 'DD-MON-RR'));
delete from  event   ---- **** ERROR **** event id 11:-10, event id 47: -75
where TO_DATE(event_start_date, 'DD-MON-RR') > TO_DATE(event_end_date, 'DD-MON-RR');

---check the Volunteer table
select * from volunteer;
--- check the person_id from Volunteer table
select * from volunteer  --- *** ERROR ***
where person_id not in(select person_id from person);
--clean 
delete from volunteer
where person_id not in(select person_id from person);

---check the date from Volunteer table
select * from volunteer --- *** ERROR ***
where to_date(vol_start_date,'DD-MON-RR') > to_char(vol_end_date,'DD-MON-RR');
--clean
delete from follow_up
where person_id in (select person_id from volunteer
where to_date(vol_start_date,'DD-MON-RR') > to_char(vol_end_date,'DD-MON-RR'));
delete from volunteer
where to_date(vol_start_date,'DD-MON-RR') > to_char(vol_end_date,'DD-MON-RR');

---check the Participant table
---check the person_id from the Participant table
select * from participant  --- all correct  
where person_id not in(select person_id from person);

---check the Follow_Up table
---check the person_id from the Follow_Up table
select * from follow_up  --- all correct  
where person_id not in(select person_id from volunteer)
or person_id2 not in(select person_id from participant);

--- check the Event_Marketing table
--- check the media_id from the Event_Marketing table
select * from event_marketing  --- all correct  
where media_id not in(select media_id from media_channel);

--- check the event_id from the Event_Marketing table
select * from event_marketing  --- *** ERROR *** 
where event_id not in(select event_id from event);
select event_id from event;
--clean
delete from event_marketing
where event_id not in(select event_id from event);

commit;






























