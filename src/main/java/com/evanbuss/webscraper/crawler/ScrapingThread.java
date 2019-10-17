package com.evanbuss.webscraper.crawler;

import com.evanbuss.webscraper.models.ParsedPagesModel;
import com.evanbuss.webscraper.models.QueryModel;
import com.evanbuss.webscraper.models.ResultModel;
import com.evanbuss.webscraper.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

class ScrapingThread implements Runnable, Comparable<ScrapingThread> {

    private final String url;
    private final long delay;
    private final QueryModel query;
    private final ThreadPoolExecutor threadPool;
    private int depth;
    private String prevUrl;

    private Logger logger = LoggerFactory.getLogger(ScrapingThread.class);

    ScrapingThread(
            String url,
            String prevUrl,
            QueryModel query,
            ThreadPoolExecutor threadPool,
            long delay,
            int depth) {
        this.url = url;
        this.query = query;
        this.threadPool = threadPool;
        this.delay = delay;
        this.depth = depth;
        this.prevUrl = prevUrl;
    }

    @Override
    public void run() {
        // Ignore as the last threads will always be interrupted before shutdown
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }

        // Get HTML code for the given URL
        String[] result;
        try {
            result = ParseUtils.urlToHTML(url);
        } catch (IOException e) {
            logger.error("Could not get HTML: {}: {}", url, e.getMessage());
            return;
        }

        // Get the results from parsing the html
        ResultModel resultModel;
        try {
            resultModel = ParseUtils.queryToResult(query, result[0], result[1]);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }

        ParsedPagesModel.getInstance().addItem(url, prevUrl, resultModel, depth);

        // For each link found, we need to
        resultModel.links.forEach(
                newURL -> {
                    if (!ParsedPagesModel.getInstance().contains(newURL)
                            && !threadPool.isShutdown()
                            && !threadPool.isTerminating()) {
                        Runnable scraper = new ScrapingThread(newURL, url, query, threadPool, delay, depth + 1);
                        if (!threadPool.getQueue().contains(scraper)) {
                            threadPool.execute(scraper);
                        }
                    }
                });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScrapingThread that = (ScrapingThread) o;
        return delay == that.delay &&
                Objects.equals(url, that.url) &&
                Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, delay, query, depth);
    }

    @Override
    public int compareTo(ScrapingThread scrapingThread) {
        return this.depth - scrapingThread.depth;
    }
}
