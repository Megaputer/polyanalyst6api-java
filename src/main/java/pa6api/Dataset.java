package pa6api;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dataset extends Project {
    protected long objId;
    static protected Map<String, DatasetInfo> infoByGuid = new HashMap<String, DatasetInfo>();

    protected Dataset(String prjUUID, long objId, String url, String sid) throws Exception {
        super(prjUUID, url, sid);
        this.objId = objId;
    }

    protected HttpResponse<String> wrapperGuidRaw() throws Exception {
        return sendSafe(requestAPI("/dataset/wrapper-guid?prjUUID=" + prjUUID + "&obj=" + objId).GET().build());
    }

    String wrapperGuid() throws Exception {
        HttpResponse<String> resp = wrapperGuidRaw();
        Map<String, String> json = gson.fromJson(resp.body(), Map.class);
        if (!json.containsKey("wrapperGuid") || json.get("wrapperGuid") instanceof String == false)
            throw new APIResultException("wrapperGuid key expected");

        String guid = json.get("wrapperGuid");
        getInfo(guid);
        return guid;
    }

    protected HttpResponse<String> getInfoRaw(String wrapperGuid) throws Exception {
        return sendSafe(requestAPI("/dataset/info?wrapperGuid=" + wrapperGuid).GET().build());
    }

    DatasetInfo getInfo(String wrapperGuid) throws Exception {
        if (infoByGuid.containsKey(wrapperGuid))
            return infoByGuid.get(wrapperGuid);

        HttpResponse<String> resp = getInfoRaw(wrapperGuid);
        Map<String, Object> json = gson.fromJson(resp.body(), Map.class);

        DatasetInfo info = DatasetInfo.fromMap(json);
        infoByGuid.put(wrapperGuid, info);
        return info;
    }

    protected List<Integer> getColumnIndexes(String wrapperGuid, String cols[]) throws Exception {
        DatasetInfo info = getInfo(wrapperGuid);
        List<Integer> idxs = new ArrayList<Integer>(cols.length);
        for (String col : cols) {
            if (!info.colIdx.containsKey(col))
                throw new RuntimeException("Dataset doesn't have column \"" + col + "\"");

            idxs.add( info.colIdx.get(col) );
        }
        return idxs;
    }

    protected HttpResponse<String> getValuesRaw(String wrapperGuid, long offset, long rowCount, List<Integer> cols) throws Exception {
        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("wrapperGuid", wrapperGuid);
        postParams.put("offset", offset);
        postParams.put("rowCount", rowCount);

        if (cols != null) {
            postParams.put("columnIndexes", cols);
        }

        return sendSafe(requestAPI("/dataset/values").POST(BodyPublishers.ofString(gson.toJson(postParams))).build());
    }

    List<List<Object>> getValues(String wrapperGuid, long offset, long rowCount, List<Integer> cols) throws Exception {
        DatasetInfo info = getInfo(wrapperGuid);
        offset = Math.max(0, Math.min(offset, info.rowCount));
        rowCount = Math.min(offset + rowCount, info.rowCount) - offset;
        if (rowCount == 0)
            return new ArrayList<List<Object>>();

        HttpResponse<String> resp = getValuesRaw(wrapperGuid, offset, rowCount, cols);
        Map<String, Object> json = gson.fromJson(resp.body(), Map.class);
        if (!json.containsKey("table") || json.get("table") instanceof List == false)
            throw new APIResultException("Expected \"table\" key");

        List<List<Object>> rows = List.class.cast(json.get("table"));
        int colsNum = cols == null ? info.columnsInfo.size() : cols.size();
        for (List<Object> row : rows) {
            for (int c = 0; c < colsNum; c++) {
                row.set(c, info.columnsInfo.get(cols == null ? c : cols.get(c)).castColumnValue(row.get(c)));
            }
        }

        return rows;
    }

    List<List<Object>> getValues(String wrapperGuid, long offset, long rowCount) throws Exception {
        return getValues(wrapperGuid, offset, rowCount, null);
    }
}
