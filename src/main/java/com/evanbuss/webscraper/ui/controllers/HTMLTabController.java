package com.evanbuss.webscraper.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HTMLTabController {
  @FXML
  private TextArea siteDataTextArea;

  String getHTML() {
    return siteDataTextArea.getText();
  }

  void setHTML(String html) {
    siteDataTextArea.setText(html);
  }

  void setDisabled() {
    siteDataTextArea.setDisable(false);
  }
}
