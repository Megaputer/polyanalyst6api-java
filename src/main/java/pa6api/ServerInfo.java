package pa6api;

import java.util.Map;

public class ServerInfo {
    public String build = "";
    public String version = "";

    static ServerInfo fromMap(Map<String, Object> map) {
        ServerInfo info = new ServerInfo();
        info.build = map.get("build").toString();
        info.version = map.get("version").toString();
        return info;
    }

    @Override
    public String toString() {
        return "{ build: \"" + build + "\", " + "version: \"" + version + "\" }";
    }
}
