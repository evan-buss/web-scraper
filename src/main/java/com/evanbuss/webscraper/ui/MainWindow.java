package com.evanbuss.webscraper.ui;

import com.evanbuss.webscraper.crawler.Crawler;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;

public class MainWindow extends JFrame {

  public MainWindow() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 600);
    setTitle("Web Crawler");
    setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

    JPanel controlsPanel = new JPanel();
    controlsPanel.setLayout(new FlowLayout());

    JTextField urlField = new JTextField(30);
    JButton runButton = new JButton("Start");

    controlsPanel.add(urlField);
    controlsPanel.add(runButton);

    JLabel statusLabel = new JLabel("Info: ");
    statusLabel.setForeground(Color.red);

    // Object[] cols = new String[]{"Website URL", "Website Title"};
    JTable table = new JTable(0, 2);
    JScrollPane scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    table.setEnabled(false);

    Crawler crawler = new Crawler();

    runButton.addActionListener(
        e -> {
          try {
            crawler.startCrawl(urlField.getText(), table);
          } catch (MalformedURLException ex) {
            statusLabel.setText("Error: Invalid URL provided.");
          }
        });

    add(controlsPanel);
    add(statusLabel);
    add(scrollPane);

    setVisible(true);
  }
}
