package pa6api;

import java.util.List;

public interface Project {
    /**
     * Function to get a list of nodes that are in the project
     * @return list of Node
     * @throws Exception
     */
    public List<Node> getNodeList() throws Exception;

    public List<TaskItem> getTasks() throws Exception;
    public Boolean isRunning(long waveId) throws Exception;
    public long execute(Node[] nodes) throws Exception;
    public long execute(Node[] nodes, Boolean wait) throws Exception;

    /**
     * Returns Dataset object type. If node is not dataset or project does not have object with a specific objId it will throw error 
     * @param objId dataset id that can be get by getNodeList function
     * @return Dataset object type
     * @throws Exception
     */
    public Dataset getDataset(long objId) throws Exception;
}
