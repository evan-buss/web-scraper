package com.evanbuss.webscraper.ui.controllers;

import com.evanbuss.webscraper.crawler.Crawler;
import com.evanbuss.webscraper.models.ParsedPagesModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsTabController {
  @FXML
  private ProgressIndicator loadingSpinner;
  @FXML
  private Button runButton;
  @FXML
  private TextField urlField;
  @FXML
  private Spinner<Integer> workersField;
  @FXML
  private Spinner<Integer> depthField;
  @FXML
  private CheckBox depthEnabledCB;
  @FXML
  private Spinner<Integer> timeoutField;
  @FXML
  private CheckBox timeoutEnabledCB;
  @FXML
  private Spinner<Integer> requestSpinner;
  @FXML
  private CheckBox clearQueueCB;
  @FXML
  private Label elapsedLabel;
  @FXML
  private Label parsedLabel;
  @FXML
  private Label progressLabel;
  @FXML
  private Label fileLabel;
  @FXML
  private AnchorPane rootPane;

  private ParsedPagesModel model;
  private File selectedFile;
  private boolean isRunning = false;
  private Crawler crawler;
  private Timeline timeline;
  private long currentTime;
  private MainController mainController;

  public SettingsTabController() {
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
    requestSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000, 1000));

    workersField.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1,
            2 * Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors()));

    depthEnabledCB
        .selectedProperty()
        .addListener((obs, oldVal, newVal) -> depthField.setDisable(!newVal));
    depthField.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 5));

    timeoutEnabledCB
        .selectedProperty()
        .addListener((obs, oldVal, newVal) -> timeoutField.setDisable(!newVal));
    timeoutField.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 10));

    enableProgress(false);

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

    urlField
        .focusedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (!newVal && urlField.getText().trim().length() > 0) loadSiteData();
            });
  }

  /**
   * Start/Stop button handler. Starts the crawler and updates the GUI
   */
  @FXML
  public void onRunClicked() {
    if (!isRunning) {
      model.clear();
      runButton.setText("Stop");
      // Ensure valid URL
      try {
        new URL(verifyURL(urlField.getText()));
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

  /** Save the parsed website to the selected file */
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

  /**
   * Build a new crawler thread based on the values given in the GUI form.
   *
   * @return returns the new Crawler object
   */
  private Crawler buildCrawler() {
    // Build a crawler with the desired conditions
    Crawler.Builder builder =
        new Crawler.Builder(verifyURL(urlField.getText()), model)
            .numThreads(workersField.getValue())
            .finishAllJobs(clearQueueCB.isSelected())
            .delay(requestSpinner.getValue());

    if (depthEnabledCB.isSelected()) {
      builder = builder.depth(depthField.getValue());
    }

    if (timeoutEnabledCB.isSelected()) {
      builder = builder.timeout(timeoutField.getValue());
    }

    return builder.build();
  }

  /** Load and parse the site's HTML into the HTML viewer tab's textfield. */
  private void loadSiteData() {
    enableProgress(true);
    Thread thread =
        new Thread(
            () -> {
              try {
                Document document = Jsoup.connect(verifyURL(urlField.getText())).get();
                document.getElementsByTag("script").remove();

                mainController.updateHTML(document.toString());

              } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Invalid URL!!");
              } finally {
                enableProgress(false);
              }
            });
    thread.start();
  }

  private void enableProgress(boolean enable) {
    if (enable) {
      loadingSpinner.setVisible(true);
      loadingSpinner.setManaged(true);
    } else {
      loadingSpinner.setVisible(false);
      loadingSpinner.setManaged(false);
    }
  }

  private String verifyURL(String url) {
    if (!url.contains("https://") || !url.contains("http://")) {
      return "https://" + url;
    }
    return url;
  }

  void inject(MainController controller) {
    mainController = controller;
  }
}
