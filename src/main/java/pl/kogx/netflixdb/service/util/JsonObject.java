package pl.kogx.netflixdb.service.util;

import java.util.Map;

public class JsonObject {

    private Map<String, Object> jsonMap;

    public JsonObject(Object value) {
        this.jsonMap = (Map<String, Object>) value;
    }

    public JsonObject get(String key) {
        if (jsonMap.containsKey(key)) {
            return new JsonObject(jsonMap.get(key));
        } else {
            return null;
        }
    }

    public String getString(String key) {
        return String.valueOf(getObject(key));
    }

    public Long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    public Object getObject(String key) {
        if (jsonMap.containsKey(key)) {
            return jsonMap.get(key);
        } else {
            return null;
        }
    }
}
