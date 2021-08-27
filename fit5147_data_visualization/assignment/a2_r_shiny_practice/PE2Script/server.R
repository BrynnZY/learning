library(shiny)
library(ggplot2)
library(dplyr)

data <- read.csv("PE2_R_Tree_Data.csv", header = T)
dataChoose <- select(data, Genus)
dataByGenus <- group_by(dataChoose, Genus)

transData <- summarise(dataByGenus, count = n())
transData <- arrange(transData, desc(count))

Top5Genus <- transData$Genus[0:5]
transData.Top5Genus <- filter(data, Genus %in% Top5Genus)

# Define server logic required to draw a histogram
shinyServer(function(input, output) {
  updateSelectInput(session, "genusSelection", label = "", choices = Top5Genus)
   
  output$vs1 <- renderPlot({
    ggplot(transData.Top5Genus, aes(x = Genus))+
      geom_bar(stat = "count")+
      geom_text(stat = 'count', aes(label = ..count..), colour = "white", vjust = 1.6)+
      theme_minimal()})
  output$vs2 <- renderPlot({
    ggplot(transData.Top5Genus, aes(x = Useful.Life.Expectancy.Value))+
      geom_bar(stat = "count")+
      facet_wrap(~Genus)+
      theme_minimal()})
  })
