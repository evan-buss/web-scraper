package com.evanbuss.webscraper.models;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
    public Multimap<String, String> data = ArrayListMultimap.create();
    public final List<String> links = new ArrayList<>();
}
