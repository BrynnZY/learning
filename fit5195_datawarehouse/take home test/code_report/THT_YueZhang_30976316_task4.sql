--student name: Yue Zhang
--student id: 30976316

--task 4 queries

--a) Show total number of books sold by different transaction periods in Melbourne.
select p.QuarterID, q.QuarterPeriod, sum(Num_of_Books_Purchased) as Total_Num_of_Books_Sold
from PublicationFact p, QuarterDIM q, LocationCityDIM l
where p.QuarterID = q.QuarterID and p.LocationCityID = l.LocationCityID
    and l.LocationCityName = 'Melbourne'
group by p.QuarterID, q.QuarterPeriod
order by p.QuarterID;

--b) Show total number of books sold by each book category
select p.CategoryID, c.Category, sum(Num_of_Books_Purchased) as Total_Num_of_Books_Sold
from PublicationFact p, CategoryDIM c
where p.CategoryID = c.CategoryID
group by p.CategoryID, c.Category
order by p.CategoryID;

--c) Show total number of Fantasy books sold below $20
select sum(Num_of_Books_Purchased) as Total_Num_of_Books_Sold
from PublicationFact p, CategoryDIM c
where p.CategoryID = c.CategoryID
    and c.Category = 'Fantasy' and p.PriceRangeID = 'L';