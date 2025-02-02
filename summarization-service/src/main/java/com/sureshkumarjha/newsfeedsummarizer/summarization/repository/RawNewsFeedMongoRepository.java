package com.sureshkumarjha.newsfeedsummarizer.summarization.repository;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawNewsFeedMongoRepository extends MongoRepository<NewsArticle, String> {
}
