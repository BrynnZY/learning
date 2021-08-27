--using the cleanned table in data_clean process
drop table programdim;
drop table eventdim;
drop table attendancedim;
drop table subscriptiontimedim;
drop table addressdim;
drop table persondim;
drop table registrationdim;
drop table mediadim;
drop table topicdim;
drop table subscriptionfact;
drop table attendancefact;


--level 0 
--ProgramDIM
select * from program;
create table ProgramDIM as select
program_id, program_name, program_details, program_fee, program_length, program_frequency
from program;

--EventDIM
select * from event;
create table EventDIM as select
event_id, event_start_date as start_date, event_end_date as end_date,
event_size, event_location, event_cost as total_cost
from event;

--AttendanceDIM
select * from attendance;
create table AttendanceDIM as select
att_id, att_date from attendance;

--SubscriptionTimeDIM
select * from subscription;
create table SubscriptionTimeDIM as select
distinct subscription_date from subscription;

--AddressDIM
select * from address;
create table AddressDIM as select * from address;

--PersonDIM
select * from person;
create table PersonDIM as select person_id, person_name, person_age,
person_email, person_gender, person_job, person_marital_status
from person;

--RegistrationDIM
select * from registration;
create table RegistrationDIM as select reg_id, reg_date
from registration;

--MediaDIM
select * from media_channel;
create table MediaDIM as select * from media_channel;

--TopicDIM
select * from topic;
create table TopicDIM as select * from topic;

--SubscriptionFact
create table SubscriptionFact as select 
s.subscription_date, 
p.program_id, 
pe.person_id, 
a.address_id, 
count(s.subscription_id) as numer_of_people_subscriped
from subscription s, program p, person pe, address a
where s.program_id = p.program_id
and s.person_id = pe.person_id
and pe.address_id = a.address_id
group by s.subscription_date, p.program_id, pe.person_id, a.address_id;

--AttendanceFact
create table AttendanceFact as select 
at.att_id, p.program_id, pe.person_id, a.address_id, e.event_id,
sum(at.att_num_of_people_attended) as numer_of_people_attended,
sum(at.att_donation_amount) as total_donation
from attendance at, program p, person pe, address a, event e
where e.program_id = p.program_id
and at.person_id = pe.person_id
and pe.address_id = a.address_id
and at.event_id = e.event_id
group by at.att_id, p.program_id, pe.person_id, a.address_id, e.event_id;

--RegistrationFact
create table RegistrationFact as select 
re.reg_id, pe.person_id, a.address_id, m.media_id,
sum(re.reg_num_of_people_registered) as numer_of_people_registered
from registration re, media_channel m, person pe, address a
where re.person_id = pe.person_id
and pe.address_id = a.address_id
and re.media_id = m.media_id
group by re.reg_id, pe.person_id, a.address_id, m.media_id;

--InterestFact
create table InterestFact as select 
pe.person_id, a.address_id, t.topic_id,
nvl(count(*),0) as numer_of_people_registered
from person_interest pi, topic t, person pe, address a
where pi.person_id = pe.person_id
and pe.address_id = a.address_id
and pi.topic_id = t.topic_id
group by pi.topic_id, pe.person_id, a.address_id, t.topic_id;

select * from programdim;
select * from eventdim;
select * from attendancedim;
select * from subscriptiontimedim;
select * from addressdim;
select * from persondim;
select * from registrationdim;
select * from mediadim;
select * from topicdim;
select count(*) from subscription;
select count(*) from subscriptionfact;
select * from subscriptionfact;
select count(*) from attendance;
select count(*) from attendancefact;
select * from attendancefact order by att_id;
select count(*) from Registration;
select count(*) from Registrationfact;
select * from Registrationfact order by reg_id;
select * from interestfact;--no records in the cleaned person_interest table