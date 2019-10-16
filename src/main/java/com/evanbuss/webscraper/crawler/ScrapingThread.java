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

    private Logger log = LoggerFactory.getLogger(ScrapingThread.class);

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
        try {
            Thread.sleep(delay);
            System.out.println(depth);

            String[] result = ParseUtils.urlToHTML(url);

            ResultModel resultModel = ParseUtils.queryToResult(query, result[0], result[1]);

            if (notModelContains(url)) ParsedPagesModel.getInstance().addItem(url, prevUrl, resultModel, depth);

            // For each link found, we need to
            resultModel.links.forEach(
                    newURL -> {
                        if (notModelContains(newURL)
                                && !threadPool.isShutdown()
                                && !threadPool.isTerminating()) {
                            Runnable scraper = new ScrapingThread(newURL, url, query, threadPool, delay, depth + 1);
                            if (!threadPool.getQueue().contains(scraper) ) {
                                threadPool.execute(scraper);
                            }
                        }
                    });
        } catch (IOException | IllegalArgumentException | InterruptedException e) {
            log.warn("{}: {}", url, e.getMessage());
        }
    }

    private boolean notModelContains(String url) {
        return !ParsedPagesModel.getInstance().contains(url);
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
