package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.controller;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.newsfetcher.service.NewsAggregatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/v1")
public class NewsFetcherController {

    private final NewsAggregatorService newsAggregatorService;

    public NewsFetcherController(NewsAggregatorService newsAggregatorService) {
        this.newsAggregatorService = newsAggregatorService;
    }

    @GetMapping("/fetch-news")
    public List<NewsArticle> getNewsFromSources(){
        return newsAggregatorService.aggregateNewsFromVariousSources();
    }
}
