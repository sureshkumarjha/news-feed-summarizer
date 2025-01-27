package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.service;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.newsfetcher.repository.NewsFeedSummarizerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NewsAggregatorService {
    private final List<NewsFetcherService> newsFetcherServiceList;
    private final NewsFeedSummarizerRepository newsFeedSummarizerRepository;
    private final KafkaPublisherService kafkaPublisherService;
    public NewsAggregatorService(List<NewsFetcherService> newsFetcherServiceList, NewsFeedSummarizerRepository newsFeedSummarizerRepository, KafkaPublisherService kafkaPublisherService) {
        this.newsFetcherServiceList = newsFetcherServiceList;
        this.newsFeedSummarizerRepository = newsFeedSummarizerRepository;
        this.kafkaPublisherService = kafkaPublisherService;
    }

    public List<NewsArticle> aggregateNewsFromVariousSources(){
        List<NewsArticle> newsArticleList = newsFetcherServiceList.parallelStream()
                .map(NewsFetcherService::getNewsFeedFromSources)
                .flatMap(List::stream)
                .toList();

        //TODO: Filter duplicate news from Mongo

        List<String> newsArticleIdList = newsFeedSummarizerRepository.saveAll(newsArticleList)
                .stream()
                .map(NewsArticle::getId)
                .toList();
        log.info("List of Id generated {}", newsArticleIdList);

        newsArticleIdList.forEach(kafkaPublisherService::publish);
        return newsArticleList;
    }
}
