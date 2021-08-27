library(shiny)

shinyUI(fluidPage(
  
  # Application title
  titlePanel("Common Trees around Fitzroy Gardens"),
  
  # Sidebar  
  sidebarLayout(
    sidebarPanel(
      h2("The Top 5 Trees", align = "center"),
      p("any description that is releant to the vis1 and map"),
      plotOutput("vs1"),
      h2("Life Expectancy", align = "center"),
      p("any description that is releant to the vis2")
    ),
    mainPanel(
      selectInput("genusSelection", label = "",choices = list("A" = 1, "B" = 2), multiple = TRUE),
      plotOutput("vs2")
    )
  )
))
