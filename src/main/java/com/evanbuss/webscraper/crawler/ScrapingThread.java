package com.evanbuss.webscraper.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ScrapingThread implements Runnable {

  private LinkModel linkModel;
  private long sleepTime;
  private ParsedPagesModel model;
  private BlockingQueue<LinkModel> queue;

  ScrapingThread(
      LinkModel linkModel, ParsedPagesModel model, BlockingQueue<LinkModel> queue, long sleepTime) {
    this.linkModel = linkModel;
    this.model = model;
    this.queue = queue;
    this.sleepTime = sleepTime;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(sleepTime);

      Connection.Response response =
          Jsoup.connect(linkModel.getUrl())
              .userAgent(
                  "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0")
              .execute();
      System.out.println("NEW THREAD: " + linkModel.getUrl() + " - " + response.statusCode());

      Document htmlDoc = response.parse();

      Elements links = htmlDoc.select("a[href]");
      links.forEach(
          element -> {
            String link = element.attr("abs:href");
            LinkModel found = new LinkModel(link, linkModel.getDepth() + 1);
            if (!queue.contains(found)) queue.offer(found);
            model.addItem(link, linkModel.getDepth() + 1);
          });

    } catch (IOException e) {
      // System.out.println("Error opening connection" + e);
    } catch (InterruptedException e) {
      // e.printStackTrace();
    }
  }
}
