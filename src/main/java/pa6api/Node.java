package pa6api;

import java.util.Map;

public class Node {
    String name = "";
    String type = "";

    long id;
    String status = "";

    static Node fromMap(Map<String, Object> map) {
        Node node = new Node();
        node.name = map.get("name").toString();
        node.type = map.get("type").toString();
        node.status = map.get("status").toString();
        node.id = (long)(double)map.get("id");

        return node;
    }

    @Override
    public String toString() {
        return "{ name: \"" + name + "\", type: \"" + type + "\", id: \"" + id + "\", status: \"" + status + "\" }";
    }
}
