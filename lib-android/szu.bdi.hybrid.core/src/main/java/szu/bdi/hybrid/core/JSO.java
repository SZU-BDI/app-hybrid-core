package szu.bdi.hybrid.core;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wanjochan on 7/11/16.
 */

public class JSO {
    public static final int JSO_NULL = 0;
    public static final int JSO_STRING = 1;
    public static final int JSO_OBJECT = 2;//{...}
    public static final int JSO_ARRAY = 3;//[]
    protected Object _value = null;
    protected int _value_type = 0;

    public void setValue(Object v) {
        _value = v;
    }

    public Object getValue() {
        return _value;
    }

    public void setValueType(int v) {
        _value_type = v;
    }

    public int getValueType() {
        return _value_type;
    }

    public String toString() {
        Object v = _value;
        int t = _value_type;
        if (t == JSO_OBJECT || t == JSO_ARRAY) {
            return v.toString();
        } else if (t == JSO_STRING) {
            return JSONObject.quote((String) v);
        } else {
            return null;
        }
    }

    static public String o2s(JSO o) {
        if (o == null) return null;
        Object v = o.getValue();
        int t = o.getValueType();
        if (t == JSO_OBJECT || t == JSO_ARRAY) {
            return v.toString();
        } else if (t == JSO_STRING) {
            return (String) v;
        } else {
            return null;
        }
    }

    static public JSO s2o(String s) {
        JSO jso = new JSO();
        Object value = null;
        int value_type = JSO_NULL;
        if (s == null || "".equals(s)) {
//            value=null;
        } else {
            try {
                value = new JSONObject(s);
                value_type = JSO_OBJECT;
            } catch (Exception ex) {
                try {
                    value = new JSONArray(s);
                    value_type = JSO_ARRAY;
                } catch (Exception ex2) {
                    value = s;
                    value_type = JSO_STRING;
                }
            }
        }
        jso.setValue(value);
        jso.setValueType(value_type);
        return jso;
    }
}
