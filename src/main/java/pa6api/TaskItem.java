package pa6api;

import java.util.Map;

public class TaskItem {
    public String name;
    public long objId;
    public double progress;
    public double subProgress;
    public String currentState;
    public long startTime;
    public int state;

    static TaskItem fromMap(Map<String, Object> map) {
        TaskItem task = new TaskItem();
        task.name = map.get("name").toString();
        task.objId = (long)(double)map.get("objId");
        task.progress = (double)map.get("progress");
        task.subProgress = (double)map.get("subProgress");
        task.currentState = map.get("currentState").toString();
        task.startTime = (long)(double)map.get("startTime");
        task.state = (int)(double)map.get("state");
        return task;
    }

    @Override
    public String toString() {
        String str = "name: \"" + name + "\"";
        str += ", objId: " + objId;
        str += ", progress: " + progress;
        str += ", subProgress: " + subProgress;
        str += ", currentState: \"" + currentState + "\"";
        str += ", startTime: " + startTime;
        str += ", state: " + state;

        return str;
    }
}
