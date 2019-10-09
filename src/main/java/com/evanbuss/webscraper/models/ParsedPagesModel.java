package com.evanbuss.webscraper.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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

  // FIXME: Need some way to output a JSON file, instead of text
  // In the future I may also have a database connection for SQLite
  public void saveToFile(File selectedFile) {
    Thread writerThread =
        new Thread(
            () -> {
              try {
                PrintWriter writer = new PrintWriter(selectedFile);
                // for (Map.Entry<String, ResultModel> entry : data.entrySet()) {
                //  writer.write(ParseUtils.resultToJSON(entry.getValue()) + "\n");
                //  // writer.println(entry.getKey() + ", " + entry.getValue());
                // }
                Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                writer.write(
                    gson.toJson(
                        data.values().stream()
                            .filter(resultModel -> resultModel.data.size() > 0)
                            .collect(Collectors.toList())));
                writer.close();
              } catch (IOException e) {
                System.out.println("Could not open Print Writer");
              }
            });

    writerThread.start();
  }
}
