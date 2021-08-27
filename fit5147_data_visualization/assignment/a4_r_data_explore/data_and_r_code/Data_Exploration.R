install.packages("corrplot")
install.packages("visdat")
install.packages("inspectdf")
install.packages("fmsb")
install.packages("tidytext")
install.packages("tm")
install.packages("wordcloud")
install.packages("textdata")
library(dplyr)
library(corrplot)
library(visdat)
library(inspectdf)
library(fmsb)
library(ggmap)
library(tidytext)
library(tm)
library(stringr)
library(wordcloud)
library(RColorBrewer)
library(textdata)
library(ggplot2)

data_trump <- read.csv("hashtag_donaldtrump.csv")
data_biden <- read.csv("hashtag_joebiden.csv")
glimpse(data_trump)
glimpse(data_biden)
summary(data_trump)
summary(data_biden)
sum(duplicated(data_trump))
sum(duplicated(data_biden))

#first combine these two dataset together
#add a new column called candidate
data_trump <- mutate(data_trump, candidate = "Trump")
data_biden <- mutate(data_biden, candidate = "Biden")
#bind two datasets together
candidate_data <- rbind(data_trump,data_biden)
glimpse(candidate_data)

#head(data_trump)
#data_trump$likes <- as.numeric(data_trump$likes)
#sum(duplicated(candidate_data))
dim(candidate_data)
new_candidate_data <- candidate_data[!duplicated(candidate_data),]
dim(new_candidate_data)

#write.csv(x = new_candidate_data, file = "combined_data.csv")
#glimpse(new_candidate_data)
new_candidate_data <- read.csv("combined_data.csv")
#new_candidate_data <- subset(new_candidate_data, select = -c(tweet_id, user_description, collected_at))
glimpse(new_candidate_data)
new_candidate_data <- new_candidate_data[,-1]
#put "" to NA
new_candidate_data[new_candidate_data == ""] <- NA
#view na counts for each column
sapply(new_candidate_data, function(x) sum(is.na(x)))
new_candidate_data %>% visdat::vis_dat(warn_large_data = FALSE)

#change data types
#new_candidate_data$created_at <- as.Date(new_candidate_data$created_at)
new_candidate_data$likes <- as.numeric(new_candidate_data$likes)
#new_candidate_data$user_join_date <- as.Date(new_candidate_data$user_join_date)
new_candidate_data$user_followers_count <- as.numeric(new_candidate_data$user_followers_count)
new_candidate_data$lat <- as.numeric(new_candidate_data$lat)
new_candidate_data$long <- as.numeric(new_candidate_data$long)

#transform subsets for each quesiton
#question 1
#analysis in general
q1.general <- select(new_candidate_data, likes, retweet_count, user_followers_count, candidate)
glimpse(q1.general)
dim(q1.general)
sapply(q1.general, function(x) sum(is.na(x)))
#drop na
q1.general <- na.omit(q1.general)
#write to csv and analyse in tableau
write.csv(x = q1.general, file = "q1_general.csv", row.names = FALSE)
#analysis in time
q1.time <- select(new_candidate_data, created_at, candidate)
dim(q1.time)
sapply(q1.time, function(x) sum(is.na(x)))#no na values
#write to csv and analyse in tableau
write.csv(x = q1.time, file = "q1_time.csv", row.names = FALSE)
#analysis in source and location
q1.location_source <- select(new_candidate_data, source, lat, long, country, state, candidate)
dim(q1.location_source)
sapply(q1.location_source, function(x) sum(is.na(x)))
q1.location_source <- q1.location_source[!(is.na(q1.location_source$source) | is.na(new_candidate_data$lat) | is.na(new_candidate_data$long)),]
#correct errors
#United States and United States of America
q1.location_source$country[q1.location_source$country == 'United States of America'] <- 'United States'
#wrong country name
q1.location_source$country[q1.location_source$country == 'California'] <- 'United States'
q1.location_source$country[q1.location_source$country == 'Florida'] <- 'United States'
q1.location_source$country[q1.location_source$country == 'England'] <- 'United Kingdom'
q1.location_source$country[q1.location_source$country == 'Palestinian Territory'] <- 'Israel'
write.csv(x = q1.location_source, file = "q1_location_source.csv", row.names = FALSE)
q1.location_source = read.csv("q1_location_source.csv")
#state analysis in r
q1.state <- filter(q1.location_source, country == 'United States', !is.na(state))
q1.state <- select(q1.state, lat, long, state, candidate)
glimpse(q1.state)
unique(q1.state$state)
q1.state.count <- q1.state %>%  select(candidate, state) %>%
  group_by(state, candidate) %>%
  summarise(sum = n())
q3.state.count <- q1.state.count
q1.state.count.trump <- q1.state.count %>% filter(candidate == 'Trump') %>%
  arrange(desc(sum))
q1.state.count.trump<-q1.state.count.trump[1:30,]
q1.state.count.trump$id <- seq(1, nrow(q1.state.count.trump))
ggplot(q1.state.count.trump, aes(x = as.factor(id), y = sum)) + 
  geom_bar(stat = "identity", fill = alpha("orange", 0.3)) + 
  ylim(-25000, 32000) +
  theme_minimal() +
  theme(
    legend.position = "none",
    axis.text = element_blank(),
    axis.title = element_blank(),
    panel.grid = element_blank(),
    plot.margin = unit(rep(-1,4), "cm"),
    plot.title = element_text(hjust = 0.5, vjust = -20)
  )+
  coord_polar(start = 0)+
  geom_text(data = q1.state.count.trump, aes(x = id, y = sum, label = state))+
  ggtitle("Number of Trump related tweets in Top 30 states")

q1.state.count.biden <- q1.state.count %>% filter(candidate == 'Biden') %>%
  arrange(desc(sum))
q1.state.count.biden<-q1.state.count.biden[1:30,]
q1.state.count.biden$id <- seq(1, nrow(q1.state.count.biden))
ggplot(q1.state.count.biden, aes(x = as.factor(id), y = sum)) + 
  geom_bar(stat = "identity", fill = alpha("blue", 0.3)) + 
  ylim(-25000, 32000) +
  theme_minimal() +
  theme(
    legend.position = "none",
    axis.text = element_blank(),
    axis.title = element_blank(),
    panel.grid = element_blank(),
    plot.margin = unit(rep(-1,4), "cm"),
    plot.title = element_text(hjust = 0.5, vjust = -20)
  )+
  coord_polar(start = 0)+
  geom_text(data = q1.state.count.biden, aes(x = id, y = sum, label = state))+
  ggtitle("Number of Biden related tweets in Top 30 states")


#question 2
q2 <- select(new_candidate_data, likes, retweet_count, tweet, candidate)
write.csv(x = q2, file = "q2.csv", row.names = FALSE)
q2 <- mutate(q2, id = row_number())
sapply(q2, function(x) sum(is.na(x)))
glimpse(q2)
q2 <- na.omit(q2)
glimpse(q2)
tidy_q2 <- q2 %>% unnest_tokens(word, tweet)
tidy_q2
#remove stopping words
#tidy_q2 <- tidy_q2 %>% anti_join(stop_words)
#tidy_q2 <- filter(tidy_q2, !grepl())
#unique(tidy_q2$word)
#tidy_q2 %>% count(word, sort = TRUE)

reg <- "([^A-Za-z']|'(?![A-Za-z]))"
tweet_words <- filter(q2, !str_detect(tweet, '^"'))
glimpse(tweet_words)
#%>%
tweet_words <- mutate(tweet_words, tweet =str_replace_all(tweet, "https://t.co/[A-Za-z\\d]+|&", "")) 
tweet_words <- tweet_words  %>% mutate(id = row_number()) %>% unnest_tokens(word, tweet, token ="regex", pattern = reg) %>%
  filter(!word %in%stop_words$word,str_detect(word, "[a-z]"))
tweet_words
write.csv(x = tweet_words, file = "q2_tweet_words.csv", row.names = FALSE)
data <- read.csv("q2_tweet_words.csv")
glimpse(data)
filter(data, candidate == "Biden")

#define own stop word
own_stop_word = c('trump', 'biden', 'donaldtrump', 'joebiden', 'election', 
                  'elections', 'de', 'joe', 'donald', 'amp', 'la', 'en', 
                  'el', 'le', 'se', 'es', 'il', 'di', 'uselection', 'elecciones',
                  'der', 'usaelection', 'und', 'pr', 'si', 'che', 'des', 'las',
                  'don', 'trumpvsbiden', 'del', 'ist', 'lo', 'das', 'al', 'ha',
                  'qui', 'da', 'una', 'nicht', 'er', 'su', 'im')
#word cloud analysis
q2.trump_word <- tweet_words %>% filter(candidate == 'Trump' & !word %in% own_stop_word) %>% count(word, sort = TRUE)
#tweet_words<- tweet_words %>% count(word, sort = TRUE)
head(q2.trump_word)
wordcloud(
  words = q2.trump_word$word, freq = q2.trump_word$n, min.freq = 1,
  max.words=300, random.order=FALSE, rot.per=0.35, 
  colors=brewer.pal(8, "Dark2")
)

q2.biden_word <- tweet_words %>% filter(candidate == 'Biden' & !word %in% own_stop_word) %>% count(word, sort = TRUE)
head(q2.biden_word)
#wordcloud2(q2.trump_word[1:200,])
wordcloud(
  words = q2.biden_word$word, freq = q2.biden_word$n, min.freq = 1,
  max.words=300, random.order=FALSE, rot.per=0.35, 
  colors=brewer.pal(8, "Dark2")
)

write.csv(x = q2.trump_word, file = "q2_trump_words.csv", row.names = FALSE)
write.csv(x = q2.biden_word, file = "q2_biden_words.csv", row.names = FALSE)

#sentiments analysis
nrc <- get_sentiments("nrc")
unique(nrc$sentiment)
filter(nrc, sentiment == "negative")
filter(nrc, sentiment == "positive")
q2.sentiment <- tweet_words %>% filter(!word %in% own_stop_word) %>% inner_join(nrc, by = "word")
write.csv(x = q2.sentiment, file = "q2_sentiment.csv", row.names = FALSE)
#radar chart
q2.sentiment.count <- q2.sentiment %>%  select(candidate, sentiment) %>%
  group_by(sentiment, candidate) %>%
  summarise(sum = n())
q2.sentiment.count
q2.sentiment.count.trump <- q2.sentiment.count %>%
  filter(candidate == 'Trump') %>%
  select(sentiment, trump_sum = sum)
q2.sentiment.count.trump
q2.sentiment.count.biden <- q2.sentiment.count %>%
  filter(candidate == 'Biden') %>%
  select(sentiment, biden_sum = sum)
q2.sentiment.count.biden
t_trump <- t(data.frame(q2.sentiment.count.trump, row.names = 1))
t_trump <- as.data.frame(t_trump)
t_biden <- t(data.frame(q2.sentiment.count.biden, row.names = 1))
t_biden <- as.data.frame(t_biden)
t1 <- rbind(t_trump,t_biden)
data <- rbind(rep(800000,10) , rep(0,10) , t1)

colors_border=c( rgb(0.2,0.5,0.5,0.9), rgb(0.7,0.5,0.1,0.9) )
colors_in=c( rgb(0.2,0.5,0.5,0.4), rgb(0.7,0.5,0.1,0.4) )
radarchart( data  , axistype=1 , 
            pcol=colors_border , pfcol=colors_in , plwd=4 , plty=1,
            
            cglcol="grey", cglty=1, axislabcol="grey", caxislabels=seq(0,800000,200000), cglwd=0.8,
            
            vlcex=0.8 
)
legend("topright", legend = c('Trump', 'Biden'), bty = "n", pch=20 , col=colors_in , text.col = "black", cex=1.2, pt.cex=3)

afinn <- get_sentiments("afinn")
q2.sentiment2 <- tweet_words %>% filter(!word %in% own_stop_word) %>% inner_join(afinn, by = "word")
q2.sentiment2
write.csv(x = q2.sentiment2, file = "q2_sentiment2.csv", row.names = FALSE)
q2.sentiment2 = read.csv("q2_sentiment2.csv")
q2.sentiment2 <- q2.sentiment2 %>% group_by(id, likes, retweet_count, candidate) %>%
  summarise(sentiment_score = sum(value))
write.csv(x = q2.sentiment2, file = "q2_sentiment_score.csv", row.names = FALSE)
#a <- q2.sentiment2$sentiment_score
#a
#hist(a)
q2.sentiment2.trump <- q2.sentiment2 %>% filter(candidate == 'Trump' & sentiment_score > -50 & sentiment_score < 50)
q2.sentiment2.trump
q2.sentiment2.biden <- q2.sentiment2 %>% filter(candidate == 'Biden' & sentiment_score > -50 & sentiment_score < 50)
q2.sentiment2.trump
hist(q2.sentiment2.trump$sentiment_score, col = 'orange',
     main = "Trump related tweets sentiment score distribution")
hist(q2.sentiment2.biden$sentiment_score, col = 'blue',
     main = "Biden related tweets sentiment score distribution")

#q3 data wrangle
q3.vote <- read.csv("president_county_candidate.csv")
summary(q3.vote)
glimpse(q3.vote)
head(q3.state.count)
sapply(q3.vote, function(x) sum(is.na(x)))
q3.vote.biden <- q3.vote %>% filter(candidate == 'Joe Biden')%>% select(state, total_votes) %>% group_by(state)  %>% summarise(biden_total = sum(total_votes))
q3.vote.biden

q3.vote.trump <- q3.vote %>% filter(candidate == 'Donald Trump')%>% select(state, total_votes) %>% group_by(state)  %>% summarise(trump_total = sum(total_votes))
q3.vote.trump
q3.vote.clean <- q3.vote.biden %>% inner_join(q3.vote.trump, by = "state") %>% mutate(trump_minus_biden = trump_total - biden_total)
q3.vote.clean
write.csv(x = q3.vote.clean, file = "q2_vote.csv", row.names = FALSE)
q3.state.count
q3.state.biden <- q3.state.count %>% filter(candidate == 'Biden')%>% select(state, sum_biden = sum) 
q3.state.biden
q3.state.trump <- q3.state.count %>% filter(candidate == 'Trump')%>% select(state, sum_trump = sum)
q3.state.trump
q3.state.clean <- q3.state.biden %>% inner_join(q3.state.trump, by = "state") %>% mutate(trump_minus_biden = sum_trump - sum_biden)
q3.state.clean
write.csv(x = q3.state.clean, file = "q3_tweets.csv", row.names = FALSE)

q3.trump <- q3.state.trump %>% inner_join(q3.vote.trump, by = 'state')
q3.trump
q3.trump.standard <- q3.trump %>% mutate(vote = sum_trump/sum(q3.trump$sum_trump)) %>% mutate(tweet = trump_total/sum(q3.trump$trump_total))
q3.trump.standard
q3.biden <- q3.state.biden %>% inner_join(q3.vote.biden, by = 'state')
q3.biden.standard <- q3.biden %>% mutate(vote = sum_biden/sum(q3.biden$sum_biden)) %>% mutate(tweet = biden_total/sum(q3.biden$biden_total))
q3.biden.standard
q3.biden
write.csv(x = q3.trump, file = "q3_combine_trump.csv", row.names = FALSE)
write.csv(x = q3.biden, file = "q3_combine_biden.csv", row.names = FALSE)
#explore is there any correlation between votes and tweets
ggplot(q3.trump.standard, aes(x = tweet, y = vote)) + 
    geom_point(shape =1)+
    geom_smooth(method = "gam")+
  ggtitle("Correlation between Trump related tweets and real votes by each state")+
  theme_minimal()
ggplot(q3.biden.standard, aes(x = tweet, y = vote)) + 
    geom_point(shape =1)+ 
    geom_smooth(method = "gam")+
  ggtitle("Correlation between Biden related tweets and real votes by each state")+
  theme_minimal()
