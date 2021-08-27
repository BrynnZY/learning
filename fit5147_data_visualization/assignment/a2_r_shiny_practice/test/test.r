
library(shiny)
library(ggmap) 
library(ggplot2) 
library(maps) 
library(mapproj)
library(dplyr)
#library(forcats)
library(RColorBrewer)
#library(ggridges)
library(grid)
#library(gridExtra)
library(leaflet)
library(RColorBrewer)
data <- read.csv('PE2_R_Tree_Data.csv')
sub_data <- select(data,"Genus")
treeNumber <- count(sub_data,Genus)
treeNumber <- arrange(treeNumber,desc(n))
df <- slice(treeNumber,1:5)
head(data)
ui <- fixedPage(
  fixedRow(
  titlePanel(
    h1("Common Trees around Fitzroy Gardens",align = "center")
  )),
  fixedRow(
    column(4,h3("The Top 5 Trees"),
           p("The figure in the right shows that how does 5 genus distribute in the map and what diameter do they have
             which is marked by their size"),
    ),
    column(2),
    column(6,selectInput("genus","Choose Genus:",
                         choices = unique(df$Genus),
                         multiple = T,
                         selected = NULL))
  ),
  fixedRow(
    column(4,plotOutput("topPlot")),
    column(8,leafletOutput("mapPlot"))

  ),
  fixedRow(
    column(4,h2("Life Expectency"),
           p("The plots in the right side shows that top 5 genus life expectency distribution."),br(),
           p("The bar plot and density ridges plot all show that how does different genus's life expectency distribute")),
    column(8,plotOutput(outputId = "lifePlot"))
        

  )
)

server <- function(input, output){
  output$topPlot <- renderPlot({
    sub_data <- select(data,"Genus")
    treeNumber <- count(sub_data,Genus)
    treeNumber <- arrange(treeNumber,desc(n))
    df <- slice(treeNumber,1:5)
    
    mutate(df,Genus = fct_reorder(Genus, n)) %>%
    ggplot( aes(Genus, n,fill=Genus)) +
      geom_bar(stat = "identity") +
      scale_fill_brewer(palette = "Set2") + 
      coord_flip()+
      geom_text(mapping = aes(label = df$n),size=5,vjust=1.4)+
      theme_minimal()
      
  })
  
  output$lifePlot<- renderPlot({
    sub_data <- select(data,"Genus")
    treeNumber <- count(sub_data,Genus)
    treeNumber <- arrange(treeNumber,desc(n))
    df <- slice(treeNumber,1:5)
    
    topDf <- dplyr::filter(data, Genus %in% df$Genus)
    topDf$Useful.Life.Expectancy.Value <- topDf$Useful.Life.Expectancy.Value %>% as.factor()
    p1 <-   ggplot(topDf,aes(Useful.Life.Expectancy.Value,fill=Genus)) + 
        geom_bar() +
        facet_wrap(~Genus,scales= "free" )
    topDf <- dplyr::filter(data, Genus %in% df$Genus)
    p2 <-   ggplot(topDf,aes(Useful.Life.Expectancy.Value,Genus,fill=Genus))+
        geom_density_ridges(alpha=0.5,scale = 1)+
        theme_ridges()
    grid.arrange(p1,p2,nrow = 2)
  })
  
  
  output$mapPlot <- renderLeaflet({
    
    sub_data <- select(data,"Genus")
    treeNumber <- count(sub_data,Genus)
    treeNumber <- arrange(treeNumber,desc(n))
    df <- slice(treeNumber,1:5)
    
    
    topDf <- dplyr::filter(data, Genus %in% df$Genus)
    if(!is.null(input$genus)){
      topDf <- dplyr::filter(topDf, Genus %in% input$genus)
      
    }
    col_set <- colorFactor(c("#E41A1B","#377E77","#4DBE4A","#984EA3","#FF7F00"),
                           domain =df$Genus
    )
    leaflet(topDf) %>%
      addTiles()%>%
      addCircleMarkers(
        ~Longitude,
        ~Latitude,
        radius = ~Diameter.Breast.Height/18,
        color = ~col_set(Genus),
        fillOpacity = 0.5
      )%>%addLegend(pal = col_set,values = ~Genus,opacity = 0.4)
  })

}

shinyApp(ui = ui, server = server)
