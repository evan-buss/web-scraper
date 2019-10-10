package com.evanbuss.webscraper.models;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * QueryModel represents a user's request for data from a specific page using CSS selectors
 *
 * <p>The QueryModel also contains 1 or more CSS link selectors. Each of these link selectors is
 * used to find a new page link that will be added to the queue
 */
@ToString
@EqualsAndHashCode
public class QueryModel {
  // public DataSelector[] data;
  public Map<String, String> data;
  public String[] links;

  /**
   * DataSelector represents a single piece of data to be extracted from a page
   *
   * <p>The user sets a name that will be used for the data, the CSS selector to find it in the
   * page, and a type. The type defines what method of extraction will be used when the data is
   * found. TEXT extracts the text between the HTML element and HREF looks for an "href=" attribute
   */
  @ToString
  public static class DataSelector {
    public String name;
    public String query;
    public String type; // OPTIONS: HREF, TEXT
  }
}
