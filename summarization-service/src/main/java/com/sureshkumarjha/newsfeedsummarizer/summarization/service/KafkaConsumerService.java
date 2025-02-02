package com.sureshkumarjha.newsfeedsummarizer.summarization.service;

import com.sureshkumarjha.newsfeedsummarizer.common.exception.NewsFeedSummarizerException;
import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.common.model.SummaryNewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.common.model.SummaryNewsArticleRecord;
import com.sureshkumarjha.newsfeedsummarizer.summarization.repository.RawNewsFeedMongoRepository;
import com.sureshkumarjha.newsfeedsummarizer.summarization.repository.SummaryNewsFeedMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class KafkaConsumerService {

    private final RawNewsFeedMongoRepository rawNewsFeedMongoRepository;
    private final SummaryNewsFeedMongoRepository summaryNewsFeedMongoRepository;
    private final OllamaService ollamaService;

    public KafkaConsumerService(RawNewsFeedMongoRepository rawNewsFeedMongoRepository, SummaryNewsFeedMongoRepository summaryNewsFeedMongoRepository, OllamaService ollamaService) {
        this.rawNewsFeedMongoRepository = rawNewsFeedMongoRepository;
        this.summaryNewsFeedMongoRepository = summaryNewsFeedMongoRepository;
        this.ollamaService = ollamaService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}")
    public void consumeRawNewsFeedTopic(String documentId) {
        log.info("Processing raw news feed document ID: {} started", documentId);
        NewsArticle newsArticle = rawNewsFeedMongoRepository.findById(documentId)
                .orElseThrow(() -> new NewsFeedSummarizerException(String.format("Document if %s not found in raw-news-feed db", documentId)));
        SummaryNewsArticleRecord summaryNewsArticleRecord = ollamaService.getSummaryFromOllama(newsArticle);
        Optional.ofNullable(summaryNewsArticleRecord)
                .map(SummaryNewsArticleRecord::summary)
                .orElseThrow(()-> new NewsFeedSummarizerException(String.format("Null response from Ollama service, document Id %s", documentId)));
        if (!summaryNewsFeedMongoRepository.existsByRawDocId(newsArticle.getId()))
            summaryNewsFeedMongoRepository.save(SummaryNewsArticle
                    .builder()
                    .rawDocId(newsArticle.getId())
                    .title(newsArticle.getTitle())
                    .tags(summaryNewsArticleRecord.tags())
                    .summary(summaryNewsArticleRecord.summary())
                    .sourceUrl(newsArticle.getSourceUrl())
                    .dateTime(LocalDateTime.now())
                    .build());
        log.info("Processing raw news feed document ID: {} completed", documentId);
    }
}
