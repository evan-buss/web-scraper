package com.evanbuss.webscraper.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ParsedPagesModel {
  private ConcurrentMap<String, Integer> data = new ConcurrentHashMap<>();
  private long counter;

  public void addItem(String url, int depth) {
    Integer present = data.putIfAbsent(url, depth);
    if (present == null) {
      counter++;
    }
  }

  public boolean contains(LinkModel link) {
    return data.containsKey(link.getUrl());
  }

  public long getSize() {
    return counter;
  }

  public void saveToFile(File selectedFile) {
    Thread writerThread =
        new Thread(
            () -> {
              try {
                PrintWriter writer = new PrintWriter(selectedFile);
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                  writer.println(entry.getKey() + ", " + entry.getValue());
                }
                writer.close();
              } catch (IOException e) {
                System.out.println("Could not open Print Writer");
              }
            });

    writerThread.start();
  }
}
