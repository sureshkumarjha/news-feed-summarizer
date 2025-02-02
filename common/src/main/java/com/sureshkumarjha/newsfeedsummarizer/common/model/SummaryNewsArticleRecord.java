package com.sureshkumarjha.newsfeedsummarizer.common.model;

import java.util.List;

public record SummaryNewsArticleRecord(String summary, List<String> tags) {
}
