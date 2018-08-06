package pl.kogx.netflixdb.service.util;

import java.util.Map;

public class JsonObject {

    private Map<String, Object> jsonMap;

    public JsonObject(Object value) {
        this.jsonMap = (Map<String, Object>) value;
    }

    public JsonObject get(String key) throws JsonUnmarshallException {
        return new JsonObject(getObject(key));
    }

    public String getString(String key) throws JsonUnmarshallException {
        return String.valueOf(getObject(key));
    }

    public Boolean getBool(String key) throws JsonUnmarshallException {
        return Boolean.valueOf(getString(key));
    }

    public Long getLong(String key) throws JsonUnmarshallException {
        return Long.valueOf(getString(key));
    }

    public Integer getInt(String key) throws JsonUnmarshallException {
        return Integer.valueOf(getString(key));
    }

    public Object getObject(String key) throws JsonUnmarshallException {
        if (jsonMap.containsKey(key)) {
            return jsonMap.get(key);
        } else {
            throw new JsonUnmarshallException("Key not found " + key);
        }
    }

    public boolean contains(String key) {
        return jsonMap.containsKey(key);
    }

    public static class JsonUnmarshallException extends Exception {

        public JsonUnmarshallException(String s) {
            super(s);
        }
    }
}
