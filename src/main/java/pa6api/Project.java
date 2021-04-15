package pa6api;

import java.util.List;

public interface Project {
    public List<Node> getNodeList() throws Exception;
    public List<TaskItem> getTasks() throws Exception;
    public Boolean isRunning(long waveId) throws Exception;
    public long execute(Node[] nodes) throws Exception;
    public long execute(Node[] nodes, Boolean wait) throws Exception;
    public Dataset getDataset(long objId) throws Exception;
}
