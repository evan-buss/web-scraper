package com.evanbuss.webscraper.ui;

import com.evanbuss.webscraper.crawler.Crawler;
import com.evanbuss.webscraper.models.ParsedPagesModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MainWindowController {

  @FXML
  private AnchorPane rootPane;
  @FXML
  private Button runButton;
  @FXML
  private TextField urlField;
  @FXML
  private TextField workersField;
  @FXML
  private TextField depthField;
  @FXML
  private TextField timeoutField;
  @FXML
  private TextField requestField;
  @FXML
  private CheckBox clearQueueCB;
  @FXML
  private CheckBox depthEnabledCB;
  @FXML
  private CheckBox timeoutEnabledCB;
  @FXML
  private Label elapsedLabel;
  @FXML
  private Label parsedLabel;
  @FXML
  private Label progressLabel;
  @FXML
  private Label fileLabel;

  private ParsedPagesModel model;
  private File selectedFile;
  private boolean isRunning = false;
  private Crawler crawler;
  private Timeline timeline;
  private long currentTime;

  public MainWindowController() {
    model = new ParsedPagesModel();
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                actionEvent -> {
                  currentTime++;
                  elapsedLabel.setText(currentTime + " seconds");
                  parsedLabel.setText(String.valueOf(model.getSize()));
                  long[] stats = crawler.getStats();
                  progressLabel.setText(stats[0] + " / " + stats[1]);
                }),
            new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  @FXML
  public void initialize() {
    workersField.setText(String.valueOf(Runtime.getRuntime().availableProcessors()));

    depthEnabledCB
        .selectedProperty()
        .addListener(
            observable ->
                depthEnabledCB.setText(depthEnabledCB.isSelected() ? "Enabled" : "Disabled"));

    timeoutEnabledCB
        .selectedProperty()
        .addListener(
            observable ->
                timeoutEnabledCB.setText(timeoutEnabledCB.isSelected() ? "Enabled" : "Disabled"));
  }

  /**
   * Save the parsed website to the selected file
   */
  @FXML
  public void dumpDataToFile() {
    model.saveToFile(selectedFile);
  }

  /** Show the file picker to select data file location */
  @FXML
  public void onSelectFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select an output file");
    selectedFile = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
    if (selectedFile != null) {
      fileLabel.setText(selectedFile.getAbsolutePath());
    }
  }

  /** Start/Stop button handler. Starts the crawler and updates the GUI */
  public void onRunClicked() {
    if (!isRunning) {
      runButton.setText("Stop");
      // Ensure valid URL
      try {
        new URL(urlField.getText());
      } catch (MalformedURLException ex) {
        System.out.println("bad url");
        return;
      }

      crawler = buildCrawler();
      crawler.setDoneListener(
          () -> {
            timeline.stop();
            Platform.runLater(() -> runButton.setText("Start"));
          });
      Thread thread = new Thread(crawler);
      thread.setDaemon(true);
      thread.start();
      timeline.playFromStart();
    } else {
      crawler.shutdown();
      runButton.setText("Start");
    }
    isRunning = !isRunning;
  }

  /**
   * Build a new crawler thread based on the values given in the GUI form.
   *
   * @return returns the new Crawler object
   */
  private Crawler buildCrawler() {
    // Build a crawler with the desired conditions
    Crawler.Builder builder =
        new Crawler.Builder(urlField.getText(), model)
            .numThreads(Integer.parseInt(workersField.getText()))
            .finishAllJobs(clearQueueCB.isSelected())
            .delay(Integer.parseInt(requestField.getText()));

    if (depthEnabledCB.isSelected()) {
      builder = builder.depth(Integer.parseInt(depthField.getText()));
    }

    if (timeoutEnabledCB.isSelected()) {
      builder = builder.timeout(Integer.parseInt(timeoutField.getText()));
    }

    return builder.build();
  }
}
