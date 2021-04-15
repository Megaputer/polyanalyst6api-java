package pa6api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetInfo {
    public long rowCount = 0;
    public List<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();
    public Map<String, Integer> colIdx = new HashMap<String, Integer>();

    static DatasetInfo fromMap(Map<String, Object> map) {
        DatasetInfo info = new DatasetInfo();
        info.rowCount = (long)(double)map.get("rowCount");

        List<Map<String, Object>> columnsInfo = List.class.cast(map.get("columnsInfo"));
        info.columnsInfo = new ArrayList<ColumnInfo>(columnsInfo.size());
        int colIdx = 0;
        for (Map<String, Object> col : columnsInfo) {
            ColumnInfo colInfo = ColumnInfo.fromMap(col);
            info.colIdx.put(colInfo.title, colIdx++);
            info.columnsInfo.add(colInfo);
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
