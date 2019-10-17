package com.evanbuss.webscraper.ui;

// CrawlingDoneListener is used to run code when the crawler has finished execution.
// Right now it is used to stop the timer once completed.
public interface CrawlingDoneListener {
    void done();
}
