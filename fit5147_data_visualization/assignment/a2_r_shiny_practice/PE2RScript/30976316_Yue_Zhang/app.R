library(shiny)
library(ggplot2)
library(leaflet)
library(dplyr)
library(RColorBrewer)

#read data from csv file
data <- read.csv("PE2_R_Tree_Data.csv", header = T)

#data transform(group by genus and sort)
dataByGenus <- group_by(data, Genus)
transData <- summarise(dataByGenus, count = n())
transData <- arrange(transData, desc(count))
#find the top 5 genus and corresponding data
Top5Genus <- transData$Genus[0:5]
transData.Top5Genus <- filter(data, Genus %in% Top5Genus)
#colorFactor for map
pal <- colorFactor(brewer.pal(5,"Set1"), domain = Top5Genus)

#ui
ui <- fixedPage(
  
  # title
  column(12, h1("Common Trees around Fitzroy Gardens")),
  
  column(5,
      #description of vs1 and map
      h2("The Top 5 Trees", align = "center"),
      p("The map is the distribution of Top 5 trees, the radius of each circle shows the diameter, while color represents genus. Below chart is the count of Top 5 Tree Genus."),
      p("Ulmus is the most common trees around Fitzroy Gardens. Ficus trees always have a very large diameter."),
      #vs1
      plotOutput("vs1"),
      #description of vs2
      h2("Life Expectancy", align = "center"),
      p("The groups of bar charts on the right show the distribution of life expectancy for each of the top 5 genus."),
      p("Most Quercus and Corymbia have relatively long life expectancy, while for the reset of three tree genus, the life expectancies are slightly short.")
    ),
    column(7,
           column(2,""),
           #select genus
           column(10,
                selectInput("genusSelection", 
                            label = "", 
                            choices = Top5Genus,
                            multiple = TRUE,
                            selected = NULL)),
      leafletOutput("map"),
      plotOutput("vs2")
    )
)

# server 
server <- function(input, output) {
  #vs1
  output$vs1 <- renderPlot({
    ggplot(transData.Top5Genus, aes(x = Genus, fill = Genus))+
      geom_bar(stat = "count")+
      geom_text(stat = 'count', aes(label = ..count..), colour = "white", vjust = 1.6)+
      xlab("Genus")+
      ylab("Total number of trees")+
      scale_fill_brewer(palette = "Set1")+
      theme_minimal()})
  #vs2
  output$vs2 <- renderPlot({
    ggplot(transData.Top5Genus, aes(x = Useful.Life.Expectancy.Value, fill = Genus))+
      geom_bar(stat = "count", width = 3)+
      facet_wrap(~Genus)+
      scale_fill_brewer(palette = "Set1")+
      xlab("Life Expectancy")+
      ylab("Total number of trees")+
      theme_minimal()})
  #map using leaflet
  output$map <- renderLeaflet({
    if(!is.null(input$genusSelection)){
      selectData <- filter(transData.Top5Genus, Genus %in% input$genusSelection)
    }
    else{
      selectData <- transData.Top5Genus
    }
    leaflet(data = selectData) %>% 
      addCircleMarkers(lng = ~Longitude, lat = ~Latitude,
                       radius = ~Diameter.Breast.Height/20,
                       color = ~pal(Genus), fill = TRUE,
                       stroke = F, fillOpacity = 0.5) %>%
      addTiles() %>%
      addLegend(pal = pal, values = ~Genus)})
}

# Run the application 
shinyApp(ui = ui, server = server)

