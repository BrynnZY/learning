library(dplyr)
new_candidate_data <- read.csv("combined_data.csv")
#new_candidate_data <- subset(new_candidate_data, select = -c(tweet_id, user_description, collected_at))
glimpse(new_candidate_data)
new_candidate_data <- new_candidate_data[,-1]
#put "" to NA
new_candidate_data[new_candidate_data == ""] <- NA

#change data types
new_candidate_data$likes <- as.numeric(new_candidate_data$likes)
new_candidate_data$user_followers_count <- as.numeric(new_candidate_data$user_followers_count)
new_candidate_data$lat <- as.numeric(new_candidate_data$lat)
new_candidate_data$long <- as.numeric(new_candidate_data$long)
write.csv(x = new_candidate_data, file = "d3_initial_data.csv", row.names = FALSE)
q1.general.time <- select(new_candidate_data, likes, retweet_count, user_followers_count, candidate, created_at)
q1.general.time <- na.omit(q1.general.time)
glimpse(q1.general.time)
q1.general.time$created_at <- as.Date(q1.general.time$created_at)
q1.time <- q1.general.time %>% 
  select(likes, retweet_count, created_at, candidate) %>% 
  group_by(created_at, candidate) %>% 
  summarize(num_likes = sum(likes), num_retweet = sum(retweet_count), num_tweets = n()) %>% 
  arrange(created_at)

q1.time
write.csv(x = q1.time, file = "d3_date_data.csv", row.names = FALSE)

new_candidate_data <- read.csv("q1_location_source.csv")
glimpse(new_candidate_data)
q1.country <- select(new_candidate_data, country, candidate)
q1.country <- na.omit(q1.country)
glimpse(q1.country)
q1.country.total <- q1.country %>% 
  select(country) %>% 
  group_by(country) %>% 
  summarize(country_total = n())
glimpse(q1.country.total)
q1.country.trump <- q1.country %>% 
  filter(candidate == 'Trump') %>%
  select(country) %>% 
  group_by(country) %>% 
  summarize(country_trump = n())
glimpse(q1.country.trump)
q1.country.biden <- q1.country %>% 
  filter(candidate == 'Biden') %>%
  select(country) %>% 
  group_by(country) %>% 
  summarize(country_biden = n())
glimpse(q1.country.biden)
q1.contry.combine <- q1.country.total %>% 
  inner_join(q1.country.trump, by = "country")%>% 
  inner_join(q1.country.biden, by = "country")%>% 
  arrange(desc(country_total))
q1.contry.combine <- q1.contry.combine[1:10,]
glimpse(q1.contry.combine)
dim(q1.contry.combine)
q1.contry.combine <- mutate(q1.contry.combine, rank = row_number())

q1.source <- select(new_candidate_data, source, candidate)
q1.source <- na.omit(q1.source)
glimpse(q1.source)
q1.source.total <- q1.source %>% 
  select(source) %>% 
  group_by(source) %>% 
  summarize(source_total = n())
glimpse(q1.source.total)
q1.source.trump <- q1.source %>% 
  filter(candidate == 'Trump') %>%
  select(source) %>% 
  group_by(source) %>% 
  summarize(source_trump = n())
glimpse(q1.source.trump)
q1.source.biden <- q1.source %>% 
  filter(candidate == 'Biden') %>%
  select(source) %>% 
  group_by(source) %>% 
  summarize(source_biden = n())
glimpse(q1.source.biden)
q1.source.combine <- q1.source.total %>% 
  inner_join(q1.source.trump, by = "source")%>% 
  inner_join(q1.source.biden, by = "source")%>% 
  arrange(desc(source_total))
q1.source.combine <- q1.source.combine[1:10,]
glimpse(q1.source.combine)
dim(q1.source.combine)
q1.source.combine <- mutate(q1.source.combine, rank = row_number())

q1.other <- q1.source.combine %>% 
  inner_join(q1.contry.combine, by = "rank")
write.csv(x = q1.other, file = "d3_other_distribution_data.csv", row.names = FALSE)

tweet_words <- read.csv("q2_tweet_words.csv")
tweet_words$word[tweet_words$word == 'votes'] <- 'vote'
tweet_words$word[tweet_words$word == 'voted'] <- 'vote'
tweet_words$word[tweet_words$word == 'debates'] <- 'debate'
tweet_words$word[tweet_words$word == 'electionresults'] <- 'result'
tweet_words$word[tweet_words$word == 'results'] <- 'result'
tweet_words$word[tweet_words$word == 'winning'] <- 'win'
tweet_words$word[tweet_words$word == 'won'] <- 'win'
glimpse(tweet_words)
#define own stop word
own_stop_word = c('trump', 'biden', 'donaldtrump', 'joebiden', 'election', 
                  'elections', 'de', 'joe', 'donald', 'amp', 'la', 'en', 
                  'el', 'le', 'se', 'es', 'il', 'di', 'uselection', 'elecciones',
                  'der', 'usaelection', 'und', 'pr', 'si', 'che', 'des', 'las',
                  'don', 'trumpvsbiden', 'del', 'ist', 'lo', 'das', 'al', 'ha',
                  'qui', 'da', 'una', 'nicht', 'er', 'su', 'im', 'ne', 'du', 'kag', 'zu', 'zu','gt',
                  'je', 'va','sur','ya','als','ma','blm')
q2.trump_word <- tweet_words %>% filter(candidate == 'Trump' & !word %in% own_stop_word) %>% count(word, sort = TRUE)
#tweet_words<- tweet_words %>% count(word, sort = TRUE)
q2.trump_word
q2.biden_word <- tweet_words %>% filter(candidate == 'Biden' & !word %in% own_stop_word) %>% count(word, sort = TRUE)
q2.biden_word

q2.trump_word.combine <- q2.trump_word[1:100,]
q2.biden_word.combine <- q2.biden_word[1:100,]
write.csv(x = q2.trump_word.combine, file = "d3_trump_wordcloud_data.csv", row.names = FALSE)
write.csv(x = q2.biden_word.combine, file = "d3_biden_wordcloud_data.csv", row.names = FALSE)

q2_sentiment <- read.csv("q2_sentiment.csv")
q2_sentiment
q2.sentiment.count <- q2_sentiment %>%  select(candidate, sentiment) %>%
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
q2.sentiment.all <- rbind(q2.sentiment.count.biden,q2.sentiment.count.trump)
q2.sentiment.all
t_trump <- t(data.frame(q2.sentiment.count.trump, row.names = 1))
t_trump <- as.data.frame(t_trump)
t_biden <- t(data.frame(q2.sentiment.count.biden, row.names = 1))
t_biden <- as.data.frame(t_biden)
t1 <- rbind(t_trump,t_biden)
t1
q2.sentiment.all <- q2.sentiment.count.biden %>% 
  inner_join(q2.sentiment.count.trump, by = "sentiment")
write.csv(x = q2.sentiment.all, file = "d3_sentiment_data.csv", row.names = FALSE)
write.csv(x = q2.sentiment.count.trump, file = "d3_trump_sentiment_data.csv", row.names = FALSE)
write.csv(x = q2.sentiment.count.biden, file = "d3_biden_sentiment_data.csv", row.names = FALSE)

vote <- read.csv("q2_vote.csv")
tweet <- read.csv("q3_tweets.csv")
vote
vote <- vote %>% mutate( vote_trump_minus_biden = trump_minus_biden,
                         vote_biden_total = biden_total,
                         vote_trump_total = trump_total) %>%
  select(state, vote_trump_minus_biden, vote_biden_total, vote_trump_total)
q3.all <- tweet %>% 
  inner_join(vote, by = "state")
glimpse(q3.all)
write.csv(x = q3.all, file = "d3_vote_tweet_data.csv", row.names = FALSE)
