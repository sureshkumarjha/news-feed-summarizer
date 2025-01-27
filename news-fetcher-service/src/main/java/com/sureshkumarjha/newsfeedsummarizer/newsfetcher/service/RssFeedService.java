package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.newsfetcher.config.RssFeedConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RssFeedService implements NewsFetcherService{

    private final RssFeedConfig config;

    private final RestTemplate restTemplate;

    public RssFeedService(RssFeedConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<NewsArticle> getNewsFeedFromSources() {
        return getNewsFeedFromRSSSources();
    }

    public List<NewsArticle> getNewsFeedFromRSSSources() {
        return config.getSources()
                .parallelStream()
                .map(this::fetchFeed)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<NewsArticle> fetchFeed(RssFeedConfig.RssSource source) {
        ResponseEntity<String> response = restTemplate.getForEntity(source.url(), String.class);
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                Objects.requireNonNull(response.getBody()).getBytes(StandardCharsets.UTF_8)
        );) {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(byteArrayInputStream));
            return feed.getEntries()
                    .parallelStream()
                    .map(entry -> convertToArticle(entry, source))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch RSS feed: {}", source.url(), e);
        }
        return List.of();
    }

    private NewsArticle.ScrapedNews getScrapedNews(SyndEntry entry) {
        return Optional.ofNullable(entry)
                .map(SyndEntry::getLink)
                .map(url -> {
                    try {
                        return Jsoup.connect(url).get();
                    } catch (IOException ex) {
                        log.error("Error while scraping the news from {} with error message: {}", url, ex.getMessage());
                    }
                    return null;
                }).map(document -> NewsArticle.ScrapedNews.builder()
                        .title(document.title())
                        .context(document.text()).build())
                .orElse(null);
    }

    private NewsArticle convertToArticle(SyndEntry entry, RssFeedConfig.RssSource source) {
        NewsArticle article = new NewsArticle();
        article.setTitle(entry.getTitle());
        article.setContent(extractContent(entry));
        article.setSourceUrl(entry.getUri());
        article.setPublishedAt(parseDate(entry));
        article.setCategory(source.category());
        article.setScrapedNews(getScrapedNews(entry));
        return article;
    }


    private String extractContent(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().stream()
                    .map(SyndContent::getValue)
                    .collect(Collectors.joining("\n"));
        }
        return entry.getDescription() != null ?
                entry.getDescription().getValue() : "";
    }

    private LocalDateTime parseDate(SyndEntry entry) {
        return entry.getPublishedDate() != null ?
                LocalDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()) :
                LocalDateTime.now();
    }


}