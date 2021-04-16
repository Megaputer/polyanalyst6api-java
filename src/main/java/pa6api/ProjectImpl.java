package pa6api;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

class ProjectImpl extends PA6APIImpl implements Project {
    protected String prjUUID;
    protected Map<String, Object> postParams = new HashMap<String, Object>();

    protected ProjectImpl(String prjUUID, String url, String sid) throws Exception {
        super(url, sid);
        this.prjUUID = prjUUID;
        this.postParams.put("prjUUID", prjUUID);
    }

    protected HttpResponse<String> getNodeListRaw() throws Exception {
        return this.sendSafe(this.requestAPI("/project/nodes?prjUUID=" + this.prjUUID).GET().build());
    }

    public List<Node> getNodeList() throws Exception {
        HttpResponse<String> resp = getNodeListRaw();

        Map<String,Object> json = gson.fromJson(resp.body(), TreeMap.class);
        Object nodesObj = json.get("nodes");
        if (nodesObj == null || nodesObj instanceof List == false)
            throw new APIResultException("Expected \"nodes\" key");

        List<Node> list = new ArrayList<Node>();
        List<Map<String, Object>> nodes = List.class.cast(nodesObj);
        for (Map<String, Object> map : nodes) {
            list.add(Node.fromMap(map));
        }

        return list;
    }

    protected HttpResponse<String> getExecutionStatsRaw() throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/execution-statistics?prjUUID=" + this.prjUUID).GET().build()
        );
    }

    public List<NodeExecStatItem> getExecutionStats() throws Exception {
        Map<String, Object> json = gson.fromJson(getExecutionStatsRaw().body(), TreeMap.class);
        if (!json.containsKey("nodes") || !(json.get("nodes") instanceof List))
            throw new APIResultException("expected \"nodes\" key with list of nodes");

        List<Map<String, Object>> nodes = List.class.cast(json.get("nodes"));
        List<NodeExecStatItem> res = new ArrayList<NodeExecStatItem>(nodes.size());
        for (Map<String, Object> node : nodes) {
            res.add(NodeExecStatItem.fromMap(node));
        }
        return res;
    }

    protected HttpResponse<String> getTasksRaw() throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/tasks?prjUUID=" + this.prjUUID).GET().build()
        );
    }

    public List<TaskItem> getTasks() throws Exception {
        List<Map<String, Object>> json = gson.fromJson(getTasksRaw().body(), List.class);
        List<TaskItem> list = new ArrayList<TaskItem>(json.size());
        for (Map<String, Object> item: json) {
            list.add(TaskItem.fromMap(item));
        }
        return list;
    }

    protected HttpResponse<String> executeRaw(Node [] nodes) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(postParams);
        List<Object> nodesList = new ArrayList<Object>(nodes.length);
        for (Node node : nodes) {
            Map<String, String> nm = new HashMap<String, String>();
            nm.put("name", node.name);
            nm.put("type", node.type);
            nodesList.add(nm);
        }
        params.put("nodes", nodesList);

        BodyPublisher postData = BodyPublishers.ofString(gson.toJson(params));
        return this.sendSafe(this.requestAPI("/project/execute").POST(postData).build());
    }

    protected HttpResponse<String> isRunningRaw(long waveId) throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/is-running?prjUUID=" + this.prjUUID + "&executionWave=" + waveId).GET().build()
        );
    }

    public Boolean isRunning(long waveId) throws Exception {
        HttpResponse<String> resp = isRunningRaw(waveId);
        Map<String, Object> json = gson.fromJson(resp.body(), HashMap.class);
        Object result = json.get("result");
        if (result == null || result instanceof Double == false)
            throw new APIResultException();

        return (long)(double)result == 1;
    }

    public long execute(Node[] nodes) throws Exception {
        HttpResponse<String> resp = executeRaw(nodes);
        if (resp.statusCode() == 202)
            return Long.valueOf(parseExecutionWaveId(resp.headers()));

        return -1;
    }

    public long execute(Node[] nodes, Boolean wait) throws Exception {
        long waveId = execute(nodes);
        if (wait && waveId == -1)
            throw new APIResultException();

        while (wait && isRunning(waveId)) {
            TimeUnit.SECONDS.sleep(1);
        }

        return waveId;
    }

    public Dataset getDataset(long objId) throws Exception {
        return new DatasetImpl(prjUUID, objId, url, sid);
    }

    public HttpResponse<String> save() throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/save").POST(BodyPublishers.ofString(postParams())).build()
        );
    }

    public HttpResponse<String> abort() throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/global-abort").POST(BodyPublishers.ofString(postParams())).build()
        );
    }

    public HttpResponse<String> unload() throws Exception {
        return this.sendSafe(
            this.requestAPI("/project/unload").POST(BodyPublishers.ofString(postParams())).build()
        );
    }

    private static String EXECUTION_WAVE = "executionWave="; 
    private static String parseExecutionWaveId(HttpHeaders headers) {
        List<String> locArr = headers.map().get("location");
        if (locArr == null || locArr.size() == 0)
            return "";

        String loc = locArr.get(0);
        if (loc == null)
            return "";

        int start = loc.indexOf(EXECUTION_WAVE);
        if (start == -1)
            return "";

        int end = loc.indexOf("&", start + EXECUTION_WAVE.length());
        if (end == -1)
            return loc.substring(start + EXECUTION_WAVE.length());

        return loc.substring(start + EXECUTION_WAVE.length(), end);
    }

    private String postParams() {
        return gson.toJson(postParams);
    }
}