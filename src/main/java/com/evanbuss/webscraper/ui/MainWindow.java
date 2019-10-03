package com.evanbuss.webscraper.ui;

import com.evanbuss.webscraper.crawler.Crawler;
import com.evanbuss.webscraper.crawler.ParsedPagesModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainWindow extends JFrame {

  private File selectedFile;
  private ParsedPagesModel model = new ParsedPagesModel();

  public MainWindow() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 250);
    setTitle("Web Crawler");
    setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

    // ==================================
    // URL and Parse Button
    // ==================================
    JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    JTextField urlField = new JTextField(25);
    JButton runButton = new JButton("Start");
    controlsPanel.add(new JLabel("Starting URL:"));
    controlsPanel.add(urlField);
    controlsPanel.add(runButton);

    // ==================================
    // Thread Workers Controls
    // ==================================
    JPanel workersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    JTextField workersTA = new JTextField(30);
    workersTA.setText("5");
    workersPanel.add(new JLabel("Workers:"));
    workersPanel.add(workersTA);

    // ==================================
    // Maximum Depth Controls
    // ==================================
    JTextField depthTA = new JTextField(25);
    depthTA.setText("50");
    JCheckBox depthCheckBox = new JCheckBox();
    JPanel depthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    depthPanel.add(new JLabel("Maximum Depth:"));
    depthPanel.add(depthTA);
    depthPanel.add(depthCheckBox);

    // ==================================
    // Time Limit Controls
    // ==================================
    JTextField timeoutTF = new JTextField(25);
    timeoutTF.setText("120");
    JCheckBox timeoutCheckBox = new JCheckBox();
    JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    timeoutPanel.add(new JLabel("Timeout (seconds):"));
    timeoutPanel.add(timeoutTF);
    timeoutPanel.add(timeoutCheckBox);

    // ==================================
    // Elapsed Time
    // ==================================
    JPanel elapsedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel elapsedLabel = new JLabel("0:00");
    elapsedPanel.add(new JLabel("Elapsed Time:"));
    elapsedPanel.add(elapsedLabel);

    // ==================================
    // Pages Parsed
    // ==================================
    JPanel parsedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel parsedLabel = new JLabel("0");
    parsedPanel.add(new JLabel("Pages Parsed:"));
    parsedPanel.add(parsedLabel);

    // ==================================
    // File Controls
    // ==================================
    JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel filePathLabel = new JLabel();
    JButton fileButton = new JButton("Select File");
    JButton saveButton = new JButton("Dump Data");

    JFileChooser filePicker = new JFileChooser();
    filePicker.setDialogTitle("Select file for scraping output.");
    filePicker.setDialogType(JFileChooser.SAVE_DIALOG);

    filePanel.add(new JLabel("File:"));
    filePanel.add(filePathLabel);
    filePanel.add(fileButton);
    filePanel.add(saveButton);

    fileButton.addActionListener(
        e -> {
          int option = filePicker.showOpenDialog(this.getContentPane());
          if (option == JFileChooser.APPROVE_OPTION) {
            selectedFile = filePicker.getSelectedFile();
            filePathLabel.setText(selectedFile.getName());
          }
        });

    saveButton.addActionListener(e -> model.saveToFile(selectedFile));

    final int[] currentTime = {0};
    Timer timer =
        new Timer(
            1000,
            actionEvent -> {
              currentTime[0]++;
              elapsedLabel.setText(String.valueOf(currentTime[0]));
            });

    runButton.addActionListener(
        e -> {
          ExecutorService executor = Executors.newSingleThreadExecutor();
          Future<?> done = executor.submit(new Crawler(urlField.getText(), model, 2, 10, 1000));
          timer.start();
        });

    add(controlsPanel);
    add(workersPanel);
    add(depthPanel);
    add(timeoutPanel);
    add(elapsedPanel);
    add(parsedPanel);
    add(filePanel);

    setVisible(true);
  }
}
