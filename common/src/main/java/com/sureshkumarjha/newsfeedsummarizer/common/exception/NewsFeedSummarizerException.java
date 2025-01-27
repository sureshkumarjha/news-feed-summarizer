package com.sureshkumarjha.newsfeedsummarizer.common.exception;

public class NewsFeedSummarizerException extends RuntimeException{

    public NewsFeedSummarizerException(String message) {
        super(message);
    }

    public NewsFeedSummarizerException(Exception ex) {
        super(ex);
    }
}
