package com.evanbuss.webscraper.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends javafx.application.Application {

  static final Logger logger = LoggerFactory.getLogger(Application.class);

  @Override
  public void start(Stage primaryStage) throws Exception {
    logger.info("Starting application");
    logger.debug("test");
    Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    primaryStage.setTitle("Web Scraper");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
