package com.evanbuss.webscraper.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
public class Crawler {

  private String baseURL;
  private JTable table;

  public Crawler() {
  }

  public void startCrawl(String url, JTable table) throws MalformedURLException {
    this.table = table;
    this.baseURL = url;

    startScraping();
  }

  private void startScraping() throws MalformedURLException {
    try {
      URL url = new URL(baseURL);
    } catch (MalformedURLException ex) {
      throw new MalformedURLException();
    }

    try {

      Document htmlDoc = Jsoup.connect(baseURL).get();
      Elements links = htmlDoc.select("a[href]");
      links.forEach(
          element ->
              ((DefaultTableModel) table.getModel())
                  .addRow(new String[]{element.attr("abs:href"), element.text()}));

    } catch (IOException e) {
      System.out.println("Error opening connection");
    }
  }
}
