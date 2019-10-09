package com.evanbuss.webscraper.ui.controllers;

import com.evanbuss.webscraper.ui.models.QueryModel;
import com.evanbuss.webscraper.ui.models.ResultModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelectorsTabController {

  @FXML
  private TextArea selectorTA;
  @FXML
  private TextArea resultsTA;
  private MainController mainController;

  @FXML
  public void initialize() {
  }

  void inject(MainController mainController) {
    this.mainController = mainController;
  }

  /**
   * Test the user's query selectors by applying to HTML
   *
   * <p>First parse the user's input into variable names and query types. Then attempt to use those
   * queries on the Base URL's HTML Generate a JSON results object from the queried data and links
   * and set the result TF
   */
  @FXML
  public void parseSelectors() {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    QueryModel queryModel;

    try {
      queryModel = gson.fromJson(selectorTA.getText(), QueryModel.class);
      System.out.println(queryModel);
    } catch (Exception e) {
      System.out.println("error btw");
      return;
    }

    ResultModel result = new ResultModel();
    Document document = Jsoup.parse(mainController.getHTML());

    // Search for each query
    for (QueryModel.DataSelector queryPair : queryModel.data) {
      Elements elements = document.select(queryPair.query);
      for (Element elt : elements) {
        switch (queryPair.type) {
          case "HREF":
            String href = elt.attr("href");
            if (!href.equals("")) {
              result.data.add(new ResultModel.ResultPair(queryPair.name, href));
            }
            break;
          case "TEXT":
            result.data.add(new ResultModel.ResultPair(queryPair.name, elt.text()));
            break;
          default:
            System.out.println("WTF BRO");
        }
      }
    }

    // Search for each link
    for (String linkSelector : queryModel.links) {
      Elements elements = document.select(linkSelector);
      for (Element elt : elements) {
        result.links.add(elt.attr("href"));
      }
    }

    String json = gson.toJson(result);
    resultsTA.setText(json);
  }
}
