package com.evanbuss.webscraper.crawler;

import com.evanbuss.webscraper.models.ParsedPagesModel;
import com.evanbuss.webscraper.models.QueryModel;
import com.evanbuss.webscraper.models.ResultModel;
import com.evanbuss.webscraper.utils.ParseUtils;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

class ScrapingThread implements Runnable {

    private final String url;
    private final long delay;
    private final QueryModel query;
    private final ThreadPoolExecutor threadPool;
    private int depth;

    ScrapingThread(
            String url,
            QueryModel query,
            ThreadPoolExecutor threadPool,
            long delay,
            int depth) {
        this.url = url;
        this.query = query;
        this.threadPool = threadPool;
        this.delay = delay;
        this.depth = depth;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);

            String[] result = ParseUtils.urlToHTML(url);

            ResultModel resultModel = ParseUtils.queryToResult(query, result[0], result[1]);


            if (notModelContains(url)) ParsedPagesModel.getInstance().addItem(url, resultModel, depth);

            // For each link found, we need to
            resultModel.links.forEach(
                    newURL -> {
                        if (newURL.contains("social-study")) {
                            System.out.println(newURL);
                        }
                        if (notModelContains(newURL)
                                && !threadPool.isShutdown()
                                && !threadPool.isTerminating()) {
                            System.out.println("adding " + newURL);
                            threadPool.execute(new ScrapingThread(newURL, query, threadPool, delay, depth + 1));
                        } else {
//                            System.out.println("Model contains: " + newURL);
                        }
                    });
        } catch (IOException | IllegalArgumentException | InterruptedException e) {
            System.out.println(url + " : " + e.getMessage());
            // This is expected, the service may be shutdown and cancel in-progress threads.
        }
    }

    private boolean notModelContains(String url) {
        return !ParsedPagesModel.getInstance().contains(url);
    }
}
