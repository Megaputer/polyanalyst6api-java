package pa6api;

public class Node {
    String name = "";
    String type = "";

    String id = "";
    String status = "";

    public Node() {
    }

    public Node(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return "name: \"" + name + "\", type: \"" + type + "\", id: \"" + id + "\", status: \"" + status + "\"";
    }
}
