package com.evanbuss.webscraper.ui.controllers;

import javafx.fxml.FXML;

public class MainController {

  @FXML
  private SettingsTabController settingsTabController;
  @FXML
  private HTMLTabController htmlTabController;
  @FXML
  private SelectorsTabController selectorsTabController;

  @FXML
  public void initialize() {
    settingsTabController.inject(this);
    htmlTabController.inject(this);
    selectorsTabController.inject(this);
  }

  void updateHTML(String htmlContent) {
    htmlTabController.siteDataTextArea.setText(htmlContent);
  }
}
