--student name: Yue Zhang
--student id: 30976316

--task 5 two more queries

--a)show total number of books sold by different book category in Sydney
--Reason for this query:
--the manager may want to know how different categories of books sell in a certatn location
--to help further decision like put more books that from a category that sells a lot
select p.CategoryID, c.Category, sum(Num_of_Books_Purchased) as Total_Num_of_Books_Sold
from PublicationFact p, CategoryDIM c, LocationCityDIM l
where p.CategoryID = c.CategoryID and p.LocationCityID = l.LocationCityID
    and l.LocationCityName = 'Sydney'
group by p.CategoryID, c.Category
order by p.CategoryID;

--b)show total number of Novels books sold in Melbourne from different quarter
--Reason for this query:
--the manager may want to compare how a certain category of book sold in a certain location in different period
--to help further decision like having discount for that category of books when the total sales are low
select q.QuarterID, q.QuarterPeriod, sum(Num_of_Books_Purchased) as Total_Num_of_Books_Sold
from PublicationFact p, CategoryDIM c, LocationCityDIM l, QuarterDIM q
where p.CategoryID = c.CategoryID 
    and p.LocationCityID = l.LocationCityID
    and p.QuarterID = q.QuarterID
    and c.Category = 'Novels' and l.LocationCityName = 'Melbourne'
group by q.QuarterID, q.QuarterPeriod
order by q.QuarterID;