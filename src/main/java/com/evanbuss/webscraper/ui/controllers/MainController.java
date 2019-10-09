package com.evanbuss.webscraper.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

  @FXML
  private TabPane tabPane;
  @FXML
  private SettingsTabController settingsTabController;
  @FXML
  private HTMLTabController htmlTabController;
  @FXML
  private SelectorsTabController selectorsTabController;
  private String queryJSON;

  @FXML
  public void initialize() {
    settingsTabController.inject(this);
    selectorsTabController.inject(this);
  }

  void updateHTML(String htmlContent) {
    htmlTabController.setHTML(htmlContent);
    htmlTabController.setDisabled();
  }

  String getHTML() {
    return htmlTabController.getHTML();
  }

  void setQueryJSON(String text) {
    queryJSON = text;
  }

  String getQueryJSON() {
    return queryJSON;
  }

  void setQueryError() {
    tabPane.getSelectionModel().select(2);
    selectorsTabController.setResultText();
  }
}
