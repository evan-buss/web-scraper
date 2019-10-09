package com.evanbuss.webscraper.ui.models;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * ResultModel represents the results of a single scraped page.
 *
 * <p>The model contains the data the user requested as well as the links found using the user's
 * link selector
 */
@ToString
@EqualsAndHashCode
public class ResultModel {
  public List<ResultPair> data = new ArrayList<>();
  public List<String> links = new ArrayList<>();

  /**
   * ResultPair represents a single data result. It contains the user's desired variable name and the data itself
   */
  @ToString
  public static class ResultPair {
    public String name;
    public String value;

    public ResultPair(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }
}
