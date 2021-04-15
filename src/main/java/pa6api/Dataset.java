package pa6api;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;

public class Dataset extends Project {
    protected long objId;

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

        return json.get("wrapperGuid");
    }

    protected HttpResponse<String> getInfoRaw(String wrapperGuid) throws Exception {
        return sendSafe(requestAPI("/dataset/info?wrapperGuid=" + wrapperGuid).GET().build());
    }

    DatasetInfo getInfo(String wrapperGuid) throws Exception {
        HttpResponse<String> resp = getInfoRaw(wrapperGuid);
        Map<String, Object> json = gson.fromJson(resp.body(), Map.class);
        return DatasetInfo.fromMap(json);
    }

    protected HttpResponse<String> getValues(String wrapperGuid, long offset, long rowCount) throws Exception {
        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("wrapperGuid", wrapperGuid);
        postParams.put("offset", offset);
        postParams.put("rowCount", rowCount);
        return sendSafe(requestAPI("/dataset/values").POST(BodyPublishers.ofString(gson.toJson(postParams))).build());
    }
}
