package com.evanbuss.webscraper.ui.controllers;

import com.evanbuss.webscraper.models.QueryModel;
import com.evanbuss.webscraper.models.ResultModel;
import com.evanbuss.webscraper.utils.ParseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.Map;

public class SelectorsTabController {

    @FXML
    private TextArea selectorTA;
    @FXML
    private TextArea resultsTA;
    private MainController mainController;

    private Map<String, String> replaceChars = Map.of(
            "{", "}",
            "[", "]",
            "\"", "\"",
            "(", ")"
    );

    @FXML
    public void initialize() {
        selectorTA.setOnKeyTyped(
                event -> {
                    // Replace default 8 char tabs with 2 chars
                    if (event.getCharacter().equals("\t")) {
                        String text = selectorTA.getText();
                        int replaceIndex = selectorTA.getCaretPosition() - 1;
                        selectorTA.setText(
                                text.substring(0, replaceIndex) + "  " + text.substring(replaceIndex + 1));
                        selectorTA.positionCaret(replaceIndex + 2);
                    }
                    // Automatically append a ] when [ is typed
                    else if (replaceChars.containsKey(event.getCharacter())) {
                        String pairChar = replaceChars.get(event.getCharacter());
                        String text = selectorTA.getText();
                        if (selectorTA.getCaretPosition() == selectorTA.getText().length()) {
                            text += " ";
                        }
                        int index = selectorTA.getCaretPosition();
                        selectorTA.setText(text.substring(0, index) + pairChar + text.substring(index));
                        selectorTA.positionCaret(index);
                    }
                });
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
        QueryModel queryModel;
        ResultModel result;

        try {
            queryModel = ParseUtils.jsonToQuery(selectorTA.getText());
        } catch (Exception e) {
            resultsTA.setText("Error in JSON selector.\n\nPlease check syntax and try again.");
            return;
        }

        // Make sure the user has entered a website before trying to extract data
        if (mainController.getHTML().length() == 0) {
            resultsTA.setText(
                    "Set a \"Base URL\" in the Settings tab to validate your queries against.");
            return;
        }

        try {
            result = ParseUtils.queryToResult(
                    queryModel, mainController.getHTML(), mainController.getBaseURI());
        } catch (Exception e) {
            resultsTA.setText("Syntax error.\n\n" + e.getMessage());
            return;
        }

        String json = ParseUtils.resultToJSON(result);
        resultsTA.setText(json);
    }

    void setResultText() {
        resultsTA.setText("Invalid JSON.\n\nPlease fix selector before starting crawl.");
    }

    String getJSON() {
        return selectorTA.getText();
    }
}
