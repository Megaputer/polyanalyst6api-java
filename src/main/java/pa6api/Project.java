package pa6api;

import java.util.List;
import java.util.Map;

public interface Project {
    /**
     * Initiates saving of all changes that have been made in the project
     * @throws Exception
     */
    public void save() throws Exception;

    /**
     * Delete the project from the server
     * @throws Exception
     */
    public void delete() throws Exception;

    /**
     * Delete the project from the server.
     * @param forceUnload Delete project regardless other users
        By default the project will be deleted only if it's not loaded to memory.
        To delete the project that loaded to memory (there are users working on
        this project right now) set forceUnload to true.
        This operation available only for project owner and administrators, and
        cannot be undone.
     * @throws Exception
     */
    public void delete(Boolean forceUnload) throws Exception;

    /**
     * Initiate the project repairing operation
     * @throws Exception
     */
    public void repair() throws Exception;

    /**
     * Aborts the execution of all nodes in the project
     * @return
     * @throws Exception
     */
    public void abort() throws Exception;

    /**
     * Unload the project from the memory and free system resources
     * @return
     * @throws Exception
     */
    public void unload() throws Exception;

    /**
     * Function to get a list of nodes that are in the project
     * @return list of Node
     * @throws Exception
     */
    public List<Node> getNodeList() throws Exception;

    /**
     * Function to get a list of tasks that are running right now 
     * @return list of TaskItem
     * @throws Exception
     */
    public List<TaskItem> getTasks() throws Exception;

    /**
     * Initiates execution of nodes and returns execution wave identifier
     * @param nodes with filled name and type
     * @return id of calculation wave (waveId)
     * @throws Exception
     */
    public long execute(Node[] nodes) throws Exception;
    
    /**
     * Initiates execution of nodes and returns execution wave identifier
     * @param nodes with filled name and type
     * @param wait for nodes execution to complete
     * @return id of calculation wave (waveId)
     * @throws Exception
     */
    public long execute(Node[] nodes, Boolean wait) throws Exception;
    
    /**
     * Checks that execution wave is still running in the project.
     * If `wave_id` is `-1` then the project is checked against any active
        execution, saving, publishing operations
     * @param waveId Execution wave identifier
     * @return true when wave is running or false otherwise
     * @throws Exception
     */
    public Boolean isRunning(long waveId) throws Exception;


    /**
     * Function to get Dataset object type by id. If node is not a dataset or project does not have object with a specific objId it will throw error 
     * @param objId dataset id that can be get by getNodeList function
     * @return Dataset object type
     * @throws Exception
     */
    public Dataset getDataset(long objId) throws Exception;

    /**
     * Function to get Dataset object type by name. If node is not a dataset or project does not have object with a specific objId it will throw error 
     * @param name dataset name
     * @return Dataset object type
     * @throws Exception
     */
    public Dataset getDataset(String name) throws Exception;

    /**
     * Sets `node_type` parameters and strategies for the Parameters node.
     * @param nodeId: node id
     * @param nodeType: node type which parameters needs to be set
     * @param params: node type parameters
     * @param strategies: node type strategies
     * @param unsync: reset status of the Parameters node
     * @param hardUpd: update every child node with new parameters if True, \
            otherwise reset their statuses. Works only if unsync is True
     * @return List of warnings or null when no warnings
     * @throws Exception
     */
    public List<String> parameterNodeSet(long nodeId, String nodeType, Map<String, String> params, List<Integer> strategies, Boolean unsync, Boolean hardUpd) throws Exception;
    public List<String> parameterNodeSet(String nodeName, String nodeType, Map<String, String> params, List<Integer> strategies, Boolean unsync, Boolean hardUpd) throws Exception;
}
