package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.repository;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsFeedSummarizerRepository extends MongoRepository<NewsArticle, String> {
}
