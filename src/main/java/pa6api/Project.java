package pa6api;

import java.util.List;

public interface Project {
    /**
     * Function to get a list of nodes that are in the project
     * @return list of Node
     * @throws Exception
     */
    public List<Node> getNodeList() throws Exception;

    /**
     * Function to get a list of tasks that are running at the moment 
     * @return list of TaskItem
     * @throws Exception
     */
    public List<TaskItem> getTasks() throws Exception;

    /**
     * Function to start a calculation process for specific nodes
     * @param nodes with filled name and type
     * @return id of calculation wave (waveId)
     * @throws Exception
     */
    public long execute(Node[] nodes) throws Exception;
    
    /**
     * Function to start a calculation process for specific nodes
     * @param nodes with filled name and type
     * @param wait for nodes execution to complete
     * @return id of calculation wave (waveId)
     * @throws Exception
     */
    public long execute(Node[] nodes, Boolean wait) throws Exception;
    
    /**
     * Function to check running status of a specific calculation wave
     * @param waveId
     * @return true when wave is running or false otherwise
     * @throws Exception
     */
    public Boolean isRunning(long waveId) throws Exception;

    /**
     * Function to get Dataset object type. If node is not dataset or project does not have object with a specific objId it will throw error 
     * @param objId dataset id that can be get by getNodeList function
     * @return Dataset object type
     * @throws Exception
     */
    public Dataset getDataset(long objId) throws Exception;
}
