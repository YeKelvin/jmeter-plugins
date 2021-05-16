package org.apache.jmeter.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author  Kelvin.Ye
 * @date    2019-06-22 10:52
 */
public class DoubleTypeAdapter implements JsonSerializer<Double> {
    @Override
    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == src.longValue()) {
            return new JsonPrimitive(src.longValue());
        }
        return new JsonPrimitive(src);
    }
}
