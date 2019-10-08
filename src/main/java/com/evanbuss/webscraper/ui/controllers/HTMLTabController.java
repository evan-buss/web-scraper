package com.evanbuss.webscraper.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HTMLTabController {
  @FXML
  TextArea siteDataTextArea;
  private MainController mainController;

  void inject(MainController mainController) {
    this.mainController = mainController;
  }
}
