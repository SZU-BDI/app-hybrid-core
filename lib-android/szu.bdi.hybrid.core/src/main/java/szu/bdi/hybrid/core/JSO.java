package szu.bdi.hybrid.core;


import android.util.Log;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JSO {
    private JsonValue _value = Json.NULL;

    //TODO Use JsonObject.merge()
    //shallow merge
    public static JSO basicMerge(JSO... jsos) {
        JSO rt = new JSO();
        for (JSO temp : jsos) {
            rt.merge(temp);
//            if (temp == null) continue;
//            Iterator<String> keys = temp.getChildKeys().listIterator();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                if (HybridTools.isEmpty(key)) {
//                    continue;
//                }
//                jsonObject.setChild(key, temp.getChild(key));
//            }
        }
        return rt;
    }

    public void merge(JSO jso) {
        JsonValue jv = _value;
        if (_value == null || _value.isNull()) {
            _value = Json.object();
        }
        if (_value instanceof JsonObject) {
            JsonValue jv2 = jso.getValue();
            if (jv2 instanceof JsonObject) {
                ((JsonObject) _value).merge(jv2.asObject());
            }
        }
    }

    private void setValue(Object v) {
        if (v instanceof JsonValue) _value = (JsonValue) v;
        else
            _value = (v == null) ? Json.NULL : Json.parse(v.toString());
    }

    public JsonValue getValue() {
        return _value;
    }

    public String toString() {
        return toString(false);
    }

    public String asString() {
        if (_value != null) return _value.asString();
        return null;
    }

    public String toString(boolean quote) {
        if (_value == null) return null;
        if (_value.isNull()) {
            if (quote) {
                return _value.toString();
            } else {
                return null;
            }
        }
        if (_value.isString()) {
            if (quote) {
                return _value.toString();
            } else {
                return _value.asString();
            }
        } else {
            return _value.toString();
        }
    }

    protected static JSONArray toJSONArray(Object array) throws JSONException {
        JSONArray result = new JSONArray();
        if (!array.getClass().isArray()) {
            throw new JSONException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            result.put(_wrap(Array.get(array, i)));
        }
        return result;
    }

    //TMP borrow from JSONObject
    //@ref http://stackoverflow.com/questions/21858528/convert-a-bundle-to-json
    //@ref https://android.googlesource.com/platform/libcore/+/master/json/src/main/java/org/json/JSONObject.java
    protected static Object _wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return toJSONArray(o);
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
            Log.v("JSO ??? ", o.toString());
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    static public String o2s(Object o) {
        return (o == null) ? null : _wrap(o).toString();
    }

    static public String o2s(JSO o) {
        return (o == null) ? null : o.toString();
    }

    final static public JSO s2o(String s) {
        JsonValue jv = null;
        try {
            if (s == null) jv = Json.NULL;
            else jv = Json.parse(s);
        } catch (ParseException ex) {
            //ex.printStackTrace();
            jv = Json.value(s);
        }
        JSO jso = new JSO();
        jso.setValue(jv);
        return jso;
    }

    public void setChild(String k, String childAsString) {
        this.setChild(k, JSO.s2o(childAsString));
    }

    public void setChild(String k, JSO chd) {
        if (_value == null || _value.isNull()) {
            _value = Json.object();
        }
        if (_value instanceof JsonObject) {
            ((JsonObject) _value).set(k, chd.getValue());
        }
    }

    public JSO getChild(String k) {
        if (_value == null) return null;
        JSO jso = new JSO();
        if (_value instanceof JsonObject) {
            JsonValue jv = _value.asObject().get(k);
            jso.setValue(jv);
        }
        return jso;
    }

    public boolean isNull() {
        if (_value == null) {
            return true;
        }
        return _value.isNull();
    }

    public List<String> getChildKeys() {
        if (_value instanceof JsonObject) {
            return ((JsonObject) _value).names();
        }
        return new ArrayList<String>();
    }

}
