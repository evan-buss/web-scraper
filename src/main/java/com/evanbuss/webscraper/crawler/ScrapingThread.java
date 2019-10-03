package com.evanbuss.webscraper.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ScrapingThread implements Runnable {

  private String baseURL;
  private long sleepTime;
  private ParsedPagesModel model;
  private BlockingQueue<String> queue;

  ScrapingThread(
      String baseURL, ParsedPagesModel model, BlockingQueue<String> queue, long sleepTime) {
    this.baseURL = baseURL;
    this.model = model;
    this.queue = queue;
    this.sleepTime = sleepTime;
  }

  @Override
  public void run() {
    try {

      Thread.sleep(sleepTime);

      Document htmlDoc =
          Jsoup.connect(baseURL)
              .userAgent(
                  "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0")
              .get();
      Elements links = htmlDoc.select("a[href]");
      links.forEach(
          element -> {
            String link = element.attr("abs:href");
            queue.offer(link);
            model.addItem(new String[]{link, element.text()});
          });

    } catch (IOException e) {
      // System.out.println("Error opening connection" + e);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
