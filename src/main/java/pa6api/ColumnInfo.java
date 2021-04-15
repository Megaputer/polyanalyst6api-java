package pa6api;

import java.util.Map;

public class ColumnInfo {
    public enum Type {
        None,
        Numeric,
        Integer,
        String,
        Text,
        DateTime
    };

    public long id = 0;
    public String title = "";
    public Type type = Type.None;

    static ColumnInfo fromMap(Map<String, Object> map) {
        ColumnInfo info = new ColumnInfo();
        info.id = (long)(double)map.get("id");
        info.title = map.get("title").toString();

        try {
            info.type = Type.valueOf(map.get("type").toString());
        } catch(Exception e) {
        }

        return info;
    }

    Object castColumnValue(Object value) {
        if (value instanceof Double) {
            if (type == Type.Integer)
                return (long)(double)value;
        }

        return value;
    }

    @Override
    public String toString() {
        String str = "{ ";
        str += "id: " + id;
        str += ", title: \"" + title + "\"";
        str += ", type: \"" + type + "\"";
        str += " }";
        return str;
    }
}
