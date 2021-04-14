package pa6api;

import java.io.IOException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class PA6API {
    private String url;
    private String userName;
    private String password;
    private String baseUrl = "/polyanalyst";
    private String apiVersion = "1.0";
    private String api = "/api/v";
    private String sid = "";
    protected Gson gson = new Gson();
    protected HttpClient client;
    
    public PA6API(String url, String userName, String pwd) throws Exception {
        this(url, userName, pwd, "");
    }

    protected PA6API(String url, String userName, String pwd, String sid) throws Exception {
        this.url = url;
        this.userName = userName;
        this.password = pwd;
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

    private String getAPIUrl(String handler) {
        return this.url + this.baseUrl + this.api + this.apiVersion + handler;
    }

    private String getUrl(String handler) {
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

    protected HttpResponse<String> sendSafe(HttpRequest req, BodyHandler<String> handler) throws Exception {
        HttpResponse<String> resp = this.client.send(req, handler);
        if (resp.statusCode() == 200 || resp.statusCode() == 202)
            return resp;

        APIExeption apiError;
        try {
            Map<String, Object> json = gson.fromJson(resp.body(), Map.class);
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

    public HttpResponse<String> serverInfoRaw() throws Exception {
        return sendSafe(this.requestAPI("/server/info").GET().build());
    }

    public HttpResponse<String> login() throws Exception {
        final String post = String.join("&",
            "uname=" + URLEncoder.encode(this.userName, StandardCharsets.UTF_8.toString()),
            "pwd=" + URLEncoder.encode(this.password, StandardCharsets.UTF_8.toString())
        );
        HttpResponse<String> resp = sendSafe(
            this.request("/login")
            .POST(BodyPublishers.ofString(post))
            .setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .build()
        );

        this.sid = findSID(resp.headers());
        return resp;
    }

    public HttpResponse<String> logout() throws Exception {
        return sendSafe(this.request("/logout").GET().build());
    }

    public HttpResponse<String> versionsRaw() throws Exception {
        return sendSafe(this.request("/api/versions").GET().build());
    }

    public List<String> versions() throws Exception {
        HttpResponse<String> resp = this.versionsRaw();
        List<String> list = gson.fromJson(resp.body(), List.class);
        return list;
    }

    public Project project(String uuid) throws Exception {
        Project prj = new Project(uuid, this.url, this.userName, this.password, this.sid);
        prj.getNodeListRaw();
        return prj;
    }

    protected static final String SID_KEY = "sid=";
    protected static String findSID(HttpHeaders headers) {
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
