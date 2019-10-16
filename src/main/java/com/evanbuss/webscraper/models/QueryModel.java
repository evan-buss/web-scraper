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
    public Map<String, String> data;
    public String[] links;
}
