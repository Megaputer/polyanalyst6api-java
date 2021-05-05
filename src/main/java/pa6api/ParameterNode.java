package pa6api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParameterNode {
    public static class Parameter {
        public String name = "";
        public String type = "";
        public Boolean optional = false;
    }

    public static class Strategy {
        public String description = "";
        public long id = -1;
    }

    public String name = "";
    public String type = "";
    public List<ParameterNode.Parameter> parameters = new ArrayList<>();
    public List<ParameterNode.Strategy> stretegies = new ArrayList<>();

    static ParameterNode fromMap(Map<String, Object> map) {
        ParameterNode p = new ParameterNode();
        p.name = map.get("name").toString();
        p.type = map.get("type").toString();
        if (map.containsKey("parameters")) {
            List<Map<String, String>> params = List.class.cast(map.get("parameters"));
            for (Map<String, String> m : params) {
                Parameter keyValue = new Parameter();
                keyValue.name = m.get("name").toString();
                keyValue.type = m.get("type").toString();
                keyValue.optional = Double.class.cast(m.get("optional")) == 1;
                p.parameters.add(keyValue);
            }
        }
        if (map.containsKey("strategies")) {
            List<Map<String, Object>> sList = List.class.cast(map.get("strategies"));
            for (Map<String, Object> m : sList) {
                Strategy keyValue = new Strategy();
                keyValue.description = m.get("description").toString();
                keyValue.id = (long)(double)m.get("id");
                p.stretegies.add(keyValue);
            }
        }
        return p;
    }
}
