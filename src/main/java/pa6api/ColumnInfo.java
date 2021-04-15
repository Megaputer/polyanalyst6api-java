package pa6api;

import java.util.Map;

public class ColumnInfo {
    public long id = 0;
    public String title = "";
    public String type = "";

    static ColumnInfo fromMap(Map<String, Object> map) {
        ColumnInfo info = new ColumnInfo();
        info.id = (long)(double)map.get("id");
        info.title = map.get("title").toString();
        info.type = map.get("type").toString();

        return info;
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
