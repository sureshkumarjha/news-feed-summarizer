package com.sureshkumarjha.newsfeedsummarizer.common.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "raw-news-feed")
public class NewsArticle {
    @Id
    private String id;
    private String title;
    private String content;
    private String sourceUrl;
    private String category;
    private LocalDateTime publishedAt;
    private ScrapedNews scrapedNews;

    @Data
    @Builder
    public static class ScrapedNews{
        private String title;
        private String context;
    }
}
