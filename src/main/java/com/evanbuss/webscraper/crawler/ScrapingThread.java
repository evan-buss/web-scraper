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

      String html = ParseUtils.urlToHTML(url);

      ResultModel resultModel = ParseUtils.queryToResult(query, html);

      // Add all visited base links to the model
      model.addItem(url, resultModel);

      // TODO: Do something with the data retrieved from the HTML
      resultModel.data.forEach(resultPair -> {
      });

      // For each link found, we need to
      resultModel.links.forEach(
          urlString -> {
            if (!threadPool.isShutdown() && !threadPool.isTerminating()) {
              threadPool.execute(new ScrapingThread(urlString, model, query, threadPool, delay));
            }
          });
    } catch (IOException | IllegalArgumentException | InterruptedException e) {
      System.out.println("Error parsing HTML or SOMETHING");
      // This is expected, the service may be shutdown and cancel in-progress threads.
      // System.out.println("Something went wrong in the ScrapingThread");
    }
  }
}
