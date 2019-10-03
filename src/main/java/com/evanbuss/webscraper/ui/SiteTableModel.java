package com.evanbuss.webscraper.ui;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class SiteTableModel extends AbstractTableModel {

  private Vector<String[]> data = new Vector<>();
  private final String[] columnNames = new String[]{"Link URL", "Link Title"};

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public Object getValueAt(int row, int col) {
    return data.get(row)[col];
  }

  public void addRow(String link, String title) {
    data.add(new String[]{link, title});
    this.fireTableDataChanged();
  }

  void saveToFile(File selectedFile) {
    try {
      PrintWriter writer = new PrintWriter(selectedFile);
      for (String[] d : data) {
        writer.println(d[0]);
        writer.println("\td" + d[1]);
      }
      writer.close();
    } catch (IOException e) {
      System.out.println("Could not open Print Writer");
    }
  }
}
