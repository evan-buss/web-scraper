package com.evanbuss.webscraper.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ResultModelDataAdapter implements JsonSerializer<ResultModel> {
  @Override
  public JsonElement serialize(ResultModel src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject obj = new JsonObject();
    obj.add("query", context.serialize(src.data.asMap()));
    return obj;
  }
}
