package com.evanbuss.webscraper.models;

import java.util.Objects;

public class LinkModel {
  private String url;
  private int depth;

  public LinkModel(String url, int depth) {
    this.url = url;
    this.depth = depth;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
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

  @Override
  public String toString() {
    return "LinkModel{" + "url='" + url + '\'' + ", depth=" + depth + '}';
  }
}
