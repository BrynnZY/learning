install.packages("dplyr")
install.packages("RColorBrewer")
library(dplyr)
library(ggplot2)
library(leaflet)
library(RColorBrewer)

data = read.csv("PE2_R_Tree_Data.csv", header = T)
head(data)
names(data)

dataChoose <- select(data, Genus, Useful.Life.Expectancy.Value)
dataByGenus <- group_by(dataChoose, Genus)
head(dataByGenus)
transData <- summarise(dataByGenus, count = n(),
                       lifeExpenctancy = mean(Useful.Life.Expectancy.Value, na.rm = TRUE))
head(transData)
transData <- arrange(transData, desc(count))

Top5Genus <- transData$Genus[0:5]

transData.Top5Genus <- filter(data, Genus %in% Top5Genus)
head(transData.Top5Genus)

vs1 <- ggplot(transData.Top5Genus, aes(x = Genus))+
  geom_bar(stat = "count")+
  geom_text(stat = 'count', aes(label = ..count..), colour = "white", vjust = 1.6)+
  theme_minimal()
vs1
vs2 <- ggplot(transData.Top5Genus, aes(x = Useful.Life.Expectancy.Value))+
  geom_bar(stat = "count")+
  facet_wrap(~Genus)+
  theme_minimal()
vs2

display.brewer.pal(5,"Set2")
pal <- colorFactor(brewer.pal(5,"Set2"), domain = Top5Genus)
pal
m <- leaflet(data = transData.Top5Genus) %>% 
  addCircleMarkers(lng = ~Longitude, lat = ~Latitude,
                   radius = ~Diameter.Breast.Height/20,
                   color = ~pal(Genus), fill = TRUE,
                   stroke = F, fillOpacity = 0.5) %>%
  addTiles() %>%
  addLegend(pal = pal, values = Top5Genus)
m
