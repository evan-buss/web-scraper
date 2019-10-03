package com.evanbuss.webscraper.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 1) Parse the users given url, ensure it is valid
 *
 * <p>2) Create URL download (check if text/html)
 *
 * <p>3) Scan text for link tags
 *
 * <p>- Need a robust way to homogenize link types
 *
 * <p>4) Each link should be followed
 */
public class Crawler implements Runnable {

  private String baseURL;
  private ParsedPagesModel model;
  private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
  private ThreadPoolExecutor poolExecutor;
  private long delay;
  private int timeout;

  public Crawler(String url, ParsedPagesModel model, int threads, int timeout, int delay) {
    this.model = model;
    this.baseURL = url;
    this.delay = delay;
    this.timeout = timeout;

    poolExecutor =
        new ThreadPoolExecutor(
            threads, threads, timeout, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
  }

  // TODO: Create builder to enable easier construction
  static class CrawlerBuilder {
  }

  @Override
  public void run() {
    try {
      URL url = new URL(baseURL);
    } catch (MalformedURLException ex) {
      System.out.println("bad url");
    }

    long startTime = System.currentTimeMillis();
    queue.offer(baseURL);
    do {
      if (!queue.isEmpty()) {
        System.out.printf(
            "Queue Size: %d Active Threads: %d\n", queue.size(), poolExecutor.getActiveCount());
        poolExecutor.execute(new ScrapingThread(queue.poll(), model, queue, delay));
      }
    } while (((System.currentTimeMillis() - startTime) / 1000F) < timeout);

    System.out.println("Done scraping");
  }
}
