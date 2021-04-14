package pa6api;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

public class Project extends PA6API {
    private String uuid;
    private Gson gson = new Gson();
    private Map<String, Object> postParams = new HashMap<String, Object>();

    public Project(String uuid, String url, String userName, String pwd, String sid) {
        super(url, userName, pwd, sid);
        this.uuid = uuid;
        this.postParams.put("prjUUID", uuid);
    }

    public HttpResponse<String> getNodeListRaw() throws IOException, InterruptedException {
        return this.sendSafe(this.requestAPI("/project/nodes?prjUUID=" + this.uuid).GET().build());
    }

    public List<Node> getNodeList() throws IOException, InterruptedException {
        HttpResponse<String> resp = getNodeListRaw();

        Map<String,Object> json = gson.fromJson(resp.body(), HashMap.class);
        Object nodesObj = json.get("nodes");
        if (nodesObj == null || nodesObj instanceof List == false)
            throw new IOException("Unexpected format of result");

        List<Node> list = new ArrayList<Node>();
        List<Map<String, Object>> nodes = List.class.cast(nodesObj);
        for (Map<String, Object> _node : nodes) {
            Node node = new Node();
            node.name = _node.get("name").toString();
            node.type = _node.get("type").toString();
            node.status = _node.get("status").toString();
            node.id = String.valueOf(Math.round((Double)_node.get("id")));
            list.add(node);
        }

        return list;
    }

    public HttpResponse<String> getExecutionStatsRaw() throws IOException, InterruptedException {
        return this.sendSafe(
            this.requestAPI("/project/execution-statistics?prjUUID=" + this.uuid).GET().build()
        );
    }

    public HttpResponse<String> getTasksRaw() throws IOException, InterruptedException {
        return this.sendSafe(
            this.requestAPI("/project/tasks?prjUUID=" + this.uuid).GET().build()
        );
    }

    public HttpResponse<String> executeRaw(Node [] nodes) throws IOException, InterruptedException {
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

    public HttpResponse<String> isRunningRaw(String waveId) throws IOException, InterruptedException {
        return this.sendSafe(
            this.requestAPI("/project/is-running?prjUUID=" + this.uuid + "&executionWave=" + waveId).GET().build()
        );
    }

    public Boolean isRunning(String waveId) throws IOException, InterruptedException {
        HttpResponse<String> resp = isRunningRaw(waveId);
        Map<String, Object> json = gson.fromJson(resp.body(), HashMap.class);
        Object result = json.get("result");
        if (result == null)
            throw new IOException("Unexcepted format of result");

        return result.equals(1.0);
    }

    public String execute(Node[] nodes) throws IOException, InterruptedException {
        HttpResponse<String> resp = executeRaw(nodes);
        if (resp.statusCode() == 202)
            return parseExecutionWaveId(resp.headers());
        return "";
    }

    public String execute(Node[] nodes, Boolean wait) throws IOException, InterruptedException {
        String waveId = execute(nodes);
        if (waveId.isEmpty())
            throw new IOException("Unexpected format of result");

        while (wait && isRunning(waveId)) {
            TimeUnit.SECONDS.sleep(1);
        }

        return waveId;
    }

    public HttpResponse<String> save() throws IOException, InterruptedException {
        return this.sendSafe(
            this.requestAPI("/project/save").POST(BodyPublishers.ofString(postParams())).build()
        );
    }

    public HttpResponse<String> abort() throws IOException, InterruptedException {
        return this.sendSafe(
            this.requestAPI("/project/global-abort").POST(BodyPublishers.ofString(postParams())).build()
        );
    }

    public HttpResponse<String> unload() throws IOException, InterruptedException {
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
