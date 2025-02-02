package com.sureshkumarjha.newsfeedsummarizer.common.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "summary-news-feed")
public class SummaryNewsArticle {
    @Id
    private String id;
    private String rawDocId;
    private String title;
    private String summary;
    private String sourceUrl;
    private LocalDateTime dateTime;
    private List<String> tags;
}
