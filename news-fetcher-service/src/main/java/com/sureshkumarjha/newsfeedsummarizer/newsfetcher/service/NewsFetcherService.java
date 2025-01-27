package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.service;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import java.util.List;

public interface NewsFetcherService {
    List<NewsArticle> getNewsFeedFromSources();
}
