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
  private final ParsedPagesModel model;
  private final QueryModel query;
  private final ThreadPoolExecutor threadPool;

  ScrapingThread(
      String url,
      ParsedPagesModel model,
      QueryModel query,
      ThreadPoolExecutor threadPool,
      long delay) {
    this.url = url;
    this.model = model;
    this.query = query;
    this.threadPool = threadPool;
    this.delay = delay;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(delay);

      String[] result = ParseUtils.urlToHTML(url);

      ResultModel resultModel = ParseUtils.queryToResult(query, result[0], result[1]);

      // Add all visited base links to the model
      System.out.println("Adding model: " + url);
      model.addItem(url, resultModel);

      // For each link found, we need to
      resultModel.links.forEach(
          newURL -> {
            if (!model.contains(newURL)
                && !threadPool.isShutdown()
                && !threadPool.isTerminating()) {
              threadPool.execute(new ScrapingThread(newURL, model, query, threadPool, delay));
            } else {
              System.out.println("Model contains: " + url);
            }
          });
    } catch (IOException | IllegalArgumentException | InterruptedException e) {
      System.out.println(url + " : " + e.getMessage());
      // This is expected, the service may be shutdown and cancel in-progress threads.
    }
  }
}
