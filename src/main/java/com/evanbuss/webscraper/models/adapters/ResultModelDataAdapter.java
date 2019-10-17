package com.evanbuss.webscraper.models.adapters;

import com.evanbuss.webscraper.models.ResultModel;
import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ResultModelDataAdapter implements JsonSerializer<ResultModel> {
    @Override
    public JsonElement serialize(ResultModel src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        src.data.asMap().forEach((s, strings) -> {
            obj.add(s, context.serialize(strings));
            if (strings.size() == 1) {
                obj.add(s, context.serialize(Iterables.get(strings, 0)));
            } else {
                obj.add(s, context.serialize(strings));
            }
        });
        return obj;
    }
}
