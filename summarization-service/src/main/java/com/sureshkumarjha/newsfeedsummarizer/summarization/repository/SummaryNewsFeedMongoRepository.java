package com.sureshkumarjha.newsfeedsummarizer.summarization.repository;

import com.sureshkumarjha.newsfeedsummarizer.common.model.SummaryNewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryNewsFeedMongoRepository extends MongoRepository<SummaryNewsArticle, String> {
    boolean existsByRawDocId(String rawDocId);
}
