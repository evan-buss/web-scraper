package com.evanbuss.webscraper.crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class ParsedPagesModel {
  private Vector<String[]> data = new Vector<>();

  void addItem(String[] item) {
    data.add(item);
  }

  public int getSize() {
    return data.size();
  }

  public void saveToFile(File selectedFile) {
    try {
      PrintWriter writer = new PrintWriter(selectedFile);
      for (String[] d : data) {
        writer.println(d[0]);
        writer.println("\t" + d[1]);
      }
      writer.close();
    } catch (IOException e) {
      System.out.println("Could not open Print Writer");
    }
  }
}
