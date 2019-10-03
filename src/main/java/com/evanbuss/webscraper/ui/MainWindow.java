package com.evanbuss.webscraper.ui;

import com.evanbuss.webscraper.crawler.Crawler;
import com.evanbuss.webscraper.models.ParsedPagesModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindow extends JFrame {

  private File selectedFile;
  private ParsedPagesModel model = new ParsedPagesModel();
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private boolean isRunning = false;
  private JTextField urlField;
  private Timer timer;
  private JTextField depthTA;
  private JCheckBox depthCheckBox;
  private JTextField timeoutTF;
  private JCheckBox timeoutCheckBox;
  private JTextField workersTA;
  private Crawler crawler = null;
  private JCheckBox parseCB;
  private JTextField delayTF;

  public MainWindow() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 340);
    setTitle("Web Crawler");
    setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    setResizable(false);

    // ==================================
    // URL and Parse Button
    // ==================================
    JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    urlField = new JTextField(25);
    JButton runButton = new JButton("Start");
    controlsPanel.add(new JLabel("Starting URL:"));
    controlsPanel.add(urlField);
    controlsPanel.add(runButton);

    // ==================================
    // Thread Workers Controls
    // ==================================
    JPanel workersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    workersTA = new JTextField(30);
    workersTA.setText("5");
    workersPanel.add(new JLabel("Workers:"));
    workersPanel.add(workersTA);

    // ==================================
    // Maximum Depth Controls
    // ==================================
    depthTA = new JTextField(25);
    depthTA.setText("50");
    depthCheckBox = new JCheckBox();
    JPanel depthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    depthPanel.add(new JLabel("Maximum Depth:"));
    depthPanel.add(depthTA);
    depthPanel.add(depthCheckBox);

    // ==================================
    // Time Limit Controls
    // ==================================
    timeoutTF = new JTextField(25);
    timeoutTF.setText("120");
    timeoutCheckBox = new JCheckBox();
    JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    timeoutPanel.add(new JLabel("Timeout (seconds):"));
    timeoutPanel.add(timeoutTF);
    timeoutPanel.add(timeoutCheckBox);

    // ==================================
    // Delay Settings
    // ==================================
    JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    delayTF = new JTextField(10);
    delayTF.setText("1000");
    delayPanel.add(new JLabel("Delay Between Requests (milliseconds):"));
    delayPanel.add(delayTF);

    // ==================================
    // Elapsed Time
    // ==================================
    JPanel elapsedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel elapsedLabel = new JLabel("0 seconds");
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
    // Worker Stats
    // ==================================
    JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel statsLabel = new JLabel("0 / 0");
    statsPanel.add(new JLabel("Completed / Queued:"));
    statsPanel.add(statsLabel);

    // ==================================
    // Parse Settings
    // ==================================
    JPanel finishAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel parseLabel = new JLabel("Clear Work Queue Before Closing: ");
    parseCB = new JCheckBox();
    finishAllPanel.add(parseLabel);
    finishAllPanel.add(parseCB);

    // ==================================
    // File Controls
    // ==================================
    JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JTextField filePathLabel = new JTextField(19);
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
            filePathLabel.setText(selectedFile.getAbsolutePath());
          }
        });

    saveButton.addActionListener(e -> model.saveToFile(selectedFile));

    // Timer to show total elapsed time
    final int[] currentTime = {0};
    timer =
        new Timer(
            1000,
            actionEvent -> {
              currentTime[0]++;
              elapsedLabel.setText(currentTime[0] + " seconds");
              parsedLabel.setText(String.valueOf(model.getSize()));
              long[] stats = crawler.getStats();
              statsLabel.setText(stats[0] + " / " + stats[1]);
            });

    runButton.addActionListener(
        e -> {
          if (!isRunning) {
            runButton.setText("Stop");

            // Ensure valid URL
            try {
              URL url = new URL(urlField.getText());
            } catch (MalformedURLException ex) {
              System.out.println("bad url");
              return;
            }

            crawler = buildCrawler();
            executor.submit(crawler);

            timer.start();
          } else {
            crawler.shutdown();
            runButton.setText("Start");
          }
          isRunning = !isRunning;
        });

    add(controlsPanel);
    add(workersPanel);
    add(depthPanel);
    add(timeoutPanel);
    add(delayPanel);
    add(finishAllPanel);
    add(new JSeparator());
    add(elapsedPanel);
    add(parsedPanel);
    add(statsPanel);
    add(new JSeparator());
    add(filePanel);

    setVisible(true);
  }

  private Crawler buildCrawler() {
    // Build a crawler with the desired conditions
    Crawler.Builder builder =
        new Crawler.Builder(urlField.getText(), model, timer)
            .numThreads(Integer.parseInt(workersTA.getText()))
            .finishAllJobs(parseCB.isSelected())
            .delay(500);

    if (depthCheckBox.isSelected()) {
      builder = builder.depth(Integer.parseInt(depthTA.getText()));
    }

    if (timeoutCheckBox.isSelected()) {
      builder = builder.timeout(Integer.parseInt(timeoutTF.getText()));
    }

    return builder.build();
  }
}
