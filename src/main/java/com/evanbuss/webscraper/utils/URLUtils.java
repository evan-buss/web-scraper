package com.evanbuss.webscraper.utils;

public class URLUtils {

  /**
   * Check if the given url has the proper https or http prefix. If not return the url with the
   * prefix prepended
   *
   * @param url - url to check if protocol is present
   * @return the url unchanged if correct protocol or the url with the correct protocol if it was
   * missing
   */
  public static String verifyURL(String url) {
    if (!url.contains("https://") || !url.contains("http://")) {
      return "https://" + url;
    }
    return url;
  }
}
