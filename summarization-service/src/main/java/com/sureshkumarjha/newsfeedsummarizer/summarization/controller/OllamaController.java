package com.sureshkumarjha.newsfeedsummarizer.summarization.controller;

import com.sureshkumarjha.newsfeedsummarizer.common.model.NewsArticle;
import com.sureshkumarjha.newsfeedsummarizer.common.model.SummaryNewsArticleRecord;
import com.sureshkumarjha.newsfeedsummarizer.summarization.service.OllamaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public SummaryNewsArticleRecord chat(@RequestBody NewsArticle newsArticle) {
        return ollamaService.getSummaryFromOllama(newsArticle);
    }
}