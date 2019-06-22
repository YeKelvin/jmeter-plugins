package pers.kelvin.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: KelvinYe
 * Date: 2019-06-22
 * Time: 10:41
 */
public class MapTypeAdapter extends TypeAdapter<Object> {

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                // 改写数字的处理逻辑，将数字值分为整型与浮点型。
                String numberStr = in.nextString();
                if (numberStr.contains(".") || numberStr.contains("e")
                        || numberStr.contains("E")) {
                    return Double.parseDouble(numberStr);
                }
                if (Long.parseLong(numberStr) <= Integer.MAX_VALUE) {
                    return Integer.parseInt(numberStr);
                }
                return Long.parseLong(numberStr);

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void write(JsonWriter out, Object value) {
        // 序列化无需实现
    }
}
