package pa6api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatasetInfo {
    public long rowCount = 0;
    public List<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();

    static DatasetInfo fromMap(Map<String, Object> map) {
        DatasetInfo info = new DatasetInfo();
        info.rowCount = (long)(double)map.get("rowCount");

        List<Map<String, Object>> columnsInfo = List.class.cast(map.get("columnsInfo"));
        info.columnsInfo = new ArrayList<ColumnInfo>(columnsInfo.size());
        for (Map<String, Object> col : columnsInfo) {
            info.columnsInfo.add(ColumnInfo.fromMap(col));
        }
        return info;
    }

    @Override
    public String toString() {
        String str = "rowCount: " + rowCount;
        str += ", columns: " + columnsInfo.toString();
        return str;
    }
}
