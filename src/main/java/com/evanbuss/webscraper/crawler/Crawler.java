package com.evanbuss.webscraper.crawler;

import com.evanbuss.webscraper.models.QueryModel;
import com.evanbuss.webscraper.ui.CrawlingDoneListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Crawler implements Runnable {

    private final String url;
    private final long delay;
    private final int timeout;
    private final int maxDepth;
    private final boolean finishAllJobs;
    private final QueryModel query;

    private final ThreadPoolExecutor threadPool;
    private CrawlingDoneListener doneListener;
    private boolean isRunning = true;

    public static class Builder {
        // Required parameters
        private final String url;
        private final QueryModel query;

        // Optional parameters with defaults
        private int numThreads = 5;
        private int timeout = -1;
        private long delay = 500;
        private int maxDepth = -1;
        private boolean finishAllJobs = false;

        public Builder(String url, QueryModel query) {
            this.url = url;
            this.query = query;
        }

        public Builder finishAllJobs(boolean bool) {
            finishAllJobs = bool;
            return this;
        }

        public Builder numThreads(int numThreads) {
            this.numThreads = numThreads;
            return this;
        }

        public Builder timeout(int seconds) {
            timeout = seconds;
            return this;
        }

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder depth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        public Crawler build() {
            return new Crawler(this);
        }
    }

    private Crawler(Builder builder) {
        this.url = builder.url;
        this.delay = builder.delay;
        this.timeout = builder.timeout;
        this.maxDepth = builder.maxDepth;
        this.finishAllJobs = builder.finishAllJobs;
        this.query = builder.query;

        threadPool =
                new ThreadPoolExecutor(
                        builder.numThreads,
                        builder.numThreads,
                        4,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>());
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        threadPool.execute(new ScrapingThread(url, query, threadPool, delay, 1));

        int idleCounter = 0;

        while (isRunning) {
            // Timeout is set and current time exceeds the timeout
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (timeout != -1) {
                if (((System.currentTimeMillis() - startTime) / 1000F) > timeout) {
                    System.out.println("OUT OF TIME");
                    break;
                }
            }

            // Keep track of the number of times we do nothing
            if (threadPool.getCompletedTaskCount() == threadPool.getTaskCount()) {
                idleCounter++;
            }

            // We effectively have a 4 second "timeout" period of no work being done before exiting
            if (idleCounter > 4) {
                System.out.println("Idle timeout");
                isRunning = false;
            }
        }

        System.out.println("Done Parsing");
        if (finishAllJobs) {
            System.out.println("soft shutdown");
            threadPool.shutdown();
        } else {
            System.out.println("hard shutdown");
            threadPool.shutdownNow();
        }

        // FIXME: Figure out the best way to wait until the queue is completely shut down
        // while (threadPool.getTaskCount() != threadPool.getCompletedTaskCount()) {}
        // System.out.println("Completely shutdown?");
        while (threadPool.isTerminating()) {
        }
        doneListener.crawlingDone();
    }

    public long[] getStats() {
        return new long[]{threadPool.getCompletedTaskCount(), threadPool.getTaskCount()};
    }

    public void shutdown() {
        isRunning = false;
    }

    public void setDoneListener(CrawlingDoneListener listener) {
        doneListener = listener;
    }

    @Override
    public String toString() {
        return "Crawler{"
                + "url='"
                + url
                + '\''
                + ", delay="
                + delay
                + ", timeout="
                + timeout
                + ", maxDepth="
                + maxDepth
                + '}';
    }
}
