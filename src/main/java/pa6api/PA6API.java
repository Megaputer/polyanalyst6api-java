package pa6api;

import java.util.List;

public interface PA6API {
    /**
     * Function to login by username and password
     * @param userName
     * @param pwd
     * @throws Exception
     */
    public String login(String userName, String pwd) throws Exception;

    /**
     * Function to login by username, password and ldapServer
     * @param userName
     * @param pwd
     * @param ldapServer
     * @throws Exception
     */
    public String ldapLogin(String userName, String pwd, String ldapServer) throws Exception;

    /**
     * Function to logout
     * @throws Exception
     */
    public void logout() throws Exception;

    /**
     * Function to get common server information: build number and version
     * @return
     * @throws Exception
     */
    public ServerInfo serverInfo() throws Exception;

    /**
     * Function to get list of available API versions
     * @return List of versions
     * @throws Exception
     */
    public List<String> versions() throws Exception;

    /**
     * Function to get project by uuid. If project with specific uuid not exists it will throw error
     * @param uuid of specific project that exists on the server
     * @return Project type object
     * @throws Exception
     */
    public Project project(String uuid) throws Exception;

    /**
     * Function to get drive api instance
     * @return drive api instance
     */
    public Drive drive() throws Exception;

    /**
     * Initiates scheduler task execution
     * @param id - task ID
     * @throws Exception
     */
    public void runTask(long id) throws Exception;

    /**
     * Returns list of nodes with parameters and strategies supported by Parameters node.
     * @return
     * @throws Exception
     */
    public List<ParameterNode> getParameterNodes() throws Exception;
}
