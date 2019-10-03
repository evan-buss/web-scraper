package com.evanbuss.webscraper.crawler;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Crawler implements Runnable {

  private String url;
  private ParsedPagesModel model;
  private long delay;
  private int timeout;
  private int maxDepth;
  private boolean isRunning = true;
  private boolean finishAllJobs = true;

  private Timer timer;
  private ThreadPoolExecutor poolExecutor;
  private BlockingQueue<LinkModel> queue = new LinkedBlockingQueue<>();

  public static class Builder {
    // Required parameters
    private final String url;
    private final ParsedPagesModel model;
    private final Timer timer;

    // Optional parameters with defaults
    private int numThreads = 5;
    private int timeout = -1;
    private long delay = 500;
    private int maxDepth = -1;

    public Builder(String url, ParsedPagesModel model, Timer timer) {
      this.url = url;
      this.model = model;
      this.timer = timer;
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

  private Crawler(Builder builder) {
    this.url = builder.url;
    this.model = builder.model;
    this.delay = builder.delay;
    this.timeout = builder.timeout;
    this.maxDepth = builder.maxDepth;
    this.timer = builder.timer;

    poolExecutor =
        new ThreadPoolExecutor(
            builder.numThreads,
            builder.numThreads,
            100,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
  }

  // You can have multiple types of terminating conditions
  //  1) Maximum depth - Keep track of the link's depth along with the url in the queue, don't run
  // if limit reached
  //  2) Timeout - Stop parsing after a certain period has passed
  //  3) No Terminator - Keep going until user wishes to stop

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    queue.offer(new LinkModel(url, 0));

    while (isRunning) {
      // Timeout is set and current time exceeds the timeout
      if (timeout != -1) {
        if (((System.currentTimeMillis() - startTime) / 1000F) > timeout) {
          System.out.println("OUT OF TIME");
          break;
        }
      }

      // I have a blocking queue. I need to use it...
      if (!queue.isEmpty()) {
        LinkModel link = queue.poll();

        if (link.getDepth() <= maxDepth || maxDepth == -1) {
          poolExecutor.execute(new ScrapingThread(link, model, queue, delay));
        }
      }
    }

    System.out.println("Done Parsing");
    if (finishAllJobs) {
      poolExecutor.shutdown();
    } else {
      poolExecutor.shutdownNow();
    }

    while (poolExecutor.getTaskCount() != poolExecutor.getCompletedTaskCount()) {
    }
    timer.stop();
  }

  public long[] getStats() {
    return new long[]{poolExecutor.getCompletedTaskCount(), poolExecutor.getTaskCount()};
  }

  public void setFinishAllJobs(boolean bool) {
    finishAllJobs = bool;
  }

  public void shutdown() {
    isRunning = false;
  }
}
