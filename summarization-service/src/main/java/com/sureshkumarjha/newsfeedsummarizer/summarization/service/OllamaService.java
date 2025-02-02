package com.sureshkumarjha.newsfeedsummarizer.summarization.service;

import com.sureshkumarjha.newsfeedsummarizer.common.exception.NewsFeedSummarizerException;
import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.common.model.SummaryNewsArticleRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OllamaService {

    private final OllamaChatModel ollamaChatModel;

    @Autowired
    public OllamaService(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    public SummaryNewsArticleRecord getSummaryFromOllama(NewsArticle newsArticle) {
        return ChatClient.create(ollamaChatModel).prompt()
                .user(u -> u.text("Generate the summary of max {maxWord} words along with tags for the following news text {newsText}.")
                        .param("maxWord", "500")
                        .param("newsText", Optional.of(newsArticle)
                                .map(NewsArticle::getScrapedNews)
                                .map(NewsArticle.ScrapedNews::getContext)
                                .orElseThrow(()-> new NewsFeedSummarizerException("Scraped news is null for the given news Article")))
                )
                .call()
                .entity(new BeanOutputConverter<>(SummaryNewsArticleRecord.class) {
                    @Override
                    public SummaryNewsArticleRecord convert(@NonNull String text) {
                        String cleanedJson = text
                                .replaceAll("<.*?>", "") // Remove XML tags
                                .replaceAll("(?s)^.*?(\\{.*}).*$", "$1"); // Extract JSON
                        return super.convert(cleanedJson);
                    }
                });
    }

}
