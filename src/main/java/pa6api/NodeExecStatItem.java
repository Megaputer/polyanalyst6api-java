package pa6api;

import java.util.Map;

public class NodeExecStatItem extends Node {
    public long startTime = 0;
    public long endTime = 0;
    public double duration = 0;
    public long datasetRows = 0;
    public long datasetCols = 0;
    public long freeMemoryInitial = 0;
    public long freeMemoryFinal = 0;
    public long freeDiskInitial = 0;
    public long freeDiskFinal = 0;

    @Override
    public String toString() {
        String str = super.toString();
        str += ", startTime: " + startTime;
        str += ", endTime: " + endTime;
        str += ", duration: " + duration;
        str += ", datasetRows: " + datasetRows;
        str += ", datasetCols: " + datasetCols;
        str += ", freeMemoryInitial: " + freeMemoryInitial;
        str += ", freeMemotyFinal: " + freeMemoryFinal;
        str += ", freeDiskInitial: " + freeDiskInitial;
        str += ", freeDiskFinal: " + freeDiskFinal;
        return str;
    }

    static NodeExecStatItem fromMap(Map<String, Object> map) {
        NodeExecStatItem item = new NodeExecStatItem();
        item.id = (long)(double)map.get("id");
        item.name = map.get("name").toString();
        item.type = map.get("type").toString();
        item.status = map.get("status").toString();
        item.startTime = Math.round((double)map.get("startTime"));
        item.endTime = Math.round((double)map.get("endTime"));
        item.duration = (double)map.get("duration");

        item.freeMemoryInitial = (long)(double)map.get("freeMemoryInitial");
        item.freeMemoryFinal = (long)(double)map.get("freeMemoryFinal");
        item.freeDiskInitial = (long)(double)map.get("freeDiskInitial");
        item.freeDiskFinal = (long)(double)map.get("freeDiskFinal");

        if (map.containsKey("datasetRows"))
            item.datasetRows = (long)(double)map.get("datasetRows");

        if (map.containsKey("datasetCols"))
            item.datasetCols = (long)(double)map.get("datasetCols");

        return item;
    }
}
