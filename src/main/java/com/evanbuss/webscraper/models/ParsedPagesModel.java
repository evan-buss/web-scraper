package com.evanbuss.webscraper.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ParsedPagesModel {
  private final ConcurrentMap<String, ResultModel> data = new ConcurrentHashMap<>();
  private long counter;

  public void addItem(String url, ResultModel model) {
    ResultModel present = data.putIfAbsent(url, model);
    if (present == null) {
      counter++;
    }
  }

  public long getSize() {
    return counter;
  }

  public void clear() {
    data.clear();
    counter = 0;
  }

  public void saveToFile(File selectedFile) {
    Thread writerThread =
        new Thread(
            () -> {
              try (PrintWriter writer = new PrintWriter(selectedFile)) {
                Gson gson =
                    new GsonBuilder()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .registerTypeAdapter(ResultModel.class, new ResultModelDataAdapter())
                        .create();
                writer.write(gson.toJson(new ArrayList<>(data.values())));
              } catch (IOException e) {
                System.out.println("Could not open Print Writer");
              }
            });

    writerThread.start();
  }

  public boolean contains(String url) {
    return data.containsKey(url);
  }
}
