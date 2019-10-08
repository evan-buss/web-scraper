package com.evanbuss.webscraper.crawler;

import com.evanbuss.webscraper.models.LinkModel;
import com.evanbuss.webscraper.models.ParsedPagesModel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

public class ScrapingThread implements Runnable {

  private LinkModel linkModel;
  private long delay;
  private ParsedPagesModel model;
  private ThreadPoolExecutor threadPool;
  private int maxDepth;

  ScrapingThread(
      LinkModel linkModel,
      ParsedPagesModel model,
      ThreadPoolExecutor threadPool,
      long delay,
      int maxDepth) {
    this.linkModel = linkModel;
    this.model = model;
    this.threadPool = threadPool;
    this.delay = delay;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(delay);

      Connection.Response response =
          Jsoup.connect(linkModel.getUrl())
              .userAgent(
                  "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0")
              .execute();

      // End thread early if bad http response
      if (response.statusCode() != 200) {
        System.out.println(response.statusCode());
        return;
      }

      // System.out.println("NEW THREAD: " + linkModel.getUrl() + " - " + response.statusCode());
      Document htmlDoc = response.parse();
      Elements links = htmlDoc.select("a[href]");

      // Add all visited base links to the model
      model.addItem(linkModel.getUrl(), linkModel.getDepth());

      links.forEach(
          element -> {
            String link = element.attr("abs:href");
            LinkModel found = new LinkModel(link, linkModel.getDepth() + 1);
            // If the found link hasn't been visited before, add it to the parse queue.
            // if (!model.contains(found) && (found.getDepth() <= maxDepth || maxDepth == -1)) {
            if (!model.contains(found) && !threadPool.isShutdown() && !threadPool.isTerminating()) {
              threadPool.execute(new ScrapingThread(found, model, threadPool, delay, maxDepth));
            }
          });
    } catch (IOException | IllegalArgumentException | InterruptedException e) {
      // This is expected, the service may be shutdown and cancel in-progress threads.
      // System.out.println("Something went wrong in the ScrapingThread");
    }
  }
}
