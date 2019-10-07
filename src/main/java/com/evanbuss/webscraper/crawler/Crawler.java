package com.evanbuss.webscraper.crawler;

import com.evanbuss.webscraper.models.LinkModel;
import com.evanbuss.webscraper.models.ParsedPagesModel;
import com.evanbuss.webscraper.ui.CrawlingDoneListener;

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
  private boolean finishAllJobs;

  private ThreadPoolExecutor threadPool;
  private BlockingQueue<LinkModel> queue = new LinkedBlockingQueue<>();

  private CrawlingDoneListener doneListener;

  public static class Builder {
    // Required parameters
    private final String url;
    private final ParsedPagesModel model;

    // Optional parameters with defaults
    private int numThreads = 5;
    private int timeout = -1;
    private long delay = 500;
    private int maxDepth = -1;
    private boolean finishAllJobs = false;

    public Builder(String url, ParsedPagesModel model) {
      this.url = url;
      this.model = model;
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
    this.model = builder.model;
    this.delay = builder.delay;
    this.timeout = builder.timeout;
    this.maxDepth = builder.maxDepth;
    this.finishAllJobs = builder.finishAllJobs;

    threadPool =
        new ThreadPoolExecutor(
            builder.numThreads,
            builder.numThreads,
            100,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    queue.offer(new LinkModel(url, 1));

    // @FUTURE: come up with better way to parse the data. Not sure if the current thread logic is
    // good or even working
    LinkModel link;
    while (isRunning) {
      // Timeout is set and current time exceeds the timeout
      if (timeout != -1) {
        if (((System.currentTimeMillis() - startTime) / 1000F) > timeout) {
          System.out.println("OUT OF TIME");
          break;
        }
      }

      try {
        // Wait 4 seconds
        link = queue.poll(4, TimeUnit.SECONDS);

        if (link == null) {
          System.out.println("4 Second Timeout");
          break;
        }

        if (link.getDepth() <= maxDepth || maxDepth == -1) {
          System.out.println(queue);
          threadPool.execute(new ScrapingThread(link, model, queue, delay));
        } else if (link.getDepth() > maxDepth) {
          System.out.println("Over max depth");
          break;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Done Parsing");
    if (finishAllJobs) {
      threadPool.shutdown();
    } else {
      threadPool.shutdownNow();
    }

    while (threadPool.getTaskCount() != threadPool.getCompletedTaskCount()) {
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
