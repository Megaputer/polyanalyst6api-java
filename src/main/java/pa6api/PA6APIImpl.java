package pa6api;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;

public class PA6APIImpl implements PA6API {
    protected String url;
    private String baseUrl = "/polyanalyst";
    private String apiVersion = "1.0";
    private String api = "/api/v";
    protected String sid = "";
    protected Gson gson = new Gson();
    protected HttpClient client;
    
    protected PA6APIImpl(String url) throws Exception {
        this(url, "");
    }

    public static PA6API create(String url) throws Exception {
        return new PA6APIImpl(url);
    }

    public static PA6API create(String url, String sid) throws Exception {
        return new PA6APIImpl(url, sid);
    }

    protected PA6APIImpl(String url, String sid) throws Exception {
        this.url = url;
        this.sid = sid;

        final Properties props = System.getProperties(); 
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        SSLParameters p = new SSLParameters();
        p.setEndpointIdentificationAlgorithm("");

        this.client = (
            HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .sslContext(sslContext)
            .sslParameters(p)
            .build()
        );
    }

    private static TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };

    protected String getAPIUrl(String handler) {
        return this.url + this.baseUrl + this.api + this.apiVersion + handler;
    }

    protected String getUrl(String handler) {
        return this.url + this.baseUrl + handler;
    }

    private HttpRequest.Builder request(String handler, Boolean api) {
        final String url = api ? this.getAPIUrl(handler) : this.getUrl(handler);
        HttpRequest.Builder req = (
            HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(2))
        );

        if (!this.sid.isEmpty())
            req.setHeader("cookie", SID_KEY + this.sid);

        return req;
    }

    protected HttpRequest.Builder requestAPI(String handler) {
        return this.request(handler, true);
    }

    protected HttpRequest.Builder request(String handler) {
        return this.request(handler, false);
    }

    protected <T> HttpResponse<T> sendSafe(HttpRequest req, BodyHandler<T> handler) throws Exception {
        HttpResponse<T> resp = this.client.send(req, handler);
        if (resp.statusCode() == 200 || resp.statusCode() == 202)
            return resp;

        APIExeption apiError;
        try {
            Map<String, Object> json = gson.fromJson(resp.body().toString(), Map.class);
            Map<String, Object> error = Map.class.cast(json.get("error"));
            String title = error.containsKey("title") ? error.get("title").toString() : "";
            String message = error.containsKey("message") ? error.get("message").toString() : "";
            apiError = new APIExeption(resp.statusCode(), title, message);
        } catch(Exception e) {
            throw new APIExeption(resp.statusCode(), "", "Internal server error: " + resp.body());
        }
        throw apiError;
    }

    protected HttpResponse<String> sendSafe(HttpRequest req) throws Exception {
        return sendSafe(req, BodyHandlers.ofString());
    }

    protected HttpResponse<InputStream> sendAndGetStream(HttpRequest req) throws Exception {
        return sendSafe(req, BodyHandlers.ofInputStream());
    }

    protected HttpResponse<String> serverInfoRaw() throws Exception {
        return sendSafe(this.requestAPI("/server/info").GET().build());
    }

    public ServerInfo serverInfo() throws Exception {
        String res = serverInfoRaw().body();
        Map<String, Object> json = gson.fromJson(res, Map.class);
        if (!json.containsKey("build") || !json.containsKey("version"))
            throw new APIResultException("Expected \"build\" and \"version\" keys");

        return ServerInfo.fromMap(json);
    }

    public String login(String userName, String pwd) throws Exception {
        final String post = String.join("&",
            "uname=" + URLEncoder.encode(userName, StandardCharsets.UTF_8),
            "pwd=" + URLEncoder.encode(pwd, StandardCharsets.UTF_8)
        );
        HttpResponse<String> resp = sendSafe(
            this.request("/login")
            .POST(BodyPublishers.ofString(post))
            .setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .build()
        );

        return this.sid = findSID(resp.headers());
    }

    public String ldapLogin(String userName, String pwd, String ldapServer) throws Exception {
        final String post = String.join("&",
            "uname=" + URLEncoder.encode(userName, StandardCharsets.UTF_8),
            "pwd=" + URLEncoder.encode(pwd, StandardCharsets.UTF_8),
            "useLDAP=1",
            "svr=" + URLEncoder.encode(ldapServer, StandardCharsets.UTF_8)
        );
        HttpResponse<String> resp = sendSafe(
            this.request("/login")
            .POST(BodyPublishers.ofString(post))
            .setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .build()
        );

        return this.sid = findSID(resp.headers());
    }

    public void logout() throws Exception {
        sendSafe(request("/logout").GET().build());
    }

    protected HttpResponse<String> versionsRaw() throws Exception {
        return sendSafe(this.request("/api/versions").GET().build());
    }

    public List<String> versions() throws Exception {
        HttpResponse<String> resp = this.versionsRaw();
        List<String> list = gson.fromJson(resp.body(), List.class);
        return list;
    }

    public Project project(String uuid) throws Exception {
        ProjectImpl prj = new ProjectImpl(uuid, this.url, this.sid);
        prj.getNodeListRaw();
        return prj;
    }

    public Drive drive() throws Exception {
        return new DriveImpl(this.url, this.sid);
    }

    public void runTask(long id) throws Exception {
        final Map<String, Object> json = new HashMap<>();
        json.put("taskId", id);
        final String post = gson.toJson(json);
        sendSafe(requestAPI("/scheduler/run-task").POST(BodyPublishers.ofString(post)).build());
    }

    public List<ParameterNode> getParameterNodes() throws Exception {
        String json = sendSafe(requestAPI("/parameters/nodes").GET().build()).body();
        List<Map<String, Object>> nodes = gson.fromJson(json, List.class);

        List<ParameterNode> res = new ArrayList<>();
        for(Map<String, Object> map : nodes) {
            res.add(ParameterNode.fromMap(map));
        }

        return res;
    }

    private static final String SID_KEY = "sid=";
    private static String findSID(HttpHeaders headers) {
        Map<String, List<String>> hdrs = headers.map();
        List<String> cookie = hdrs.get("set-cookie");
        if (cookie == null)
            return "";

        for (String key : cookie) {
            String[] name = key.split(";");
            if (name.length > 0 && name[0].startsWith(SID_KEY))
                return name[0].substring(SID_KEY.length());
        }
        return "";
    }
}
