package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "news.rss")
@Getter
@Setter
public class RssFeedConfig {
    private List<RssSource> sources;
    private String cronSchedule;
    public record RssSource(String name, String url, String category){};
}