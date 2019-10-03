package com.evanbuss.webscraper.crawler;

import java.util.Objects;

public class LinkModel {
  private String url;
  private int depth;

  LinkModel(String url, int depth) {
    this.url = url;
    this.depth = depth;
  }

  String getUrl() {
    return url;
  }

  void setUrl(String url) {
    this.url = url;
  }

  int getDepth() {
    return depth;
  }

  void setDepth(int depth) {
    this.depth = depth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LinkModel linkModel = (LinkModel) o;
    return Objects.equals(url, linkModel.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, depth);
  }
}
