package pa6api;

public interface Drive {
    /**
     * Upload the file to the PolyAnalyst's user directory.
     * @param source
     * @param dest
     * @throws Exception
     */
    public void uploadFile(String source, String dest) throws Exception;
    public void uploadFile(String source, String dest, ProgressHandler progress) throws Exception;
    public void uploadFolder(String source, String dest, ProgressHandler progress) throws Exception;
    
    /**
     * Create a new folder inside the PolyAnalyst's user directory.
       @param name: the folder name
       @param path: a relative path of the folder's parent directory
     * @throws Exception
     */
    public void createFolder(String name, String path) throws Exception;

    /**
     * Delete the folder in the PolyAnalyst's user directory.
     * @param name: the folder name
     * @param path: a relative path of the folder's parent directory
     * @throws Exception
     */
    public void deleteFolder(String name, String path) throws Exception;

    /**
     * Delete the file in the PolyAnalyst's user directory.
     * @param name: the filename
     * @param path: a relative path of the file's parent directory
     */
    public void deleteFile(String name, String path) throws Exception;

    /**
     * Download the file from the PolyAnalyst's user directory
     * @param remoteFileName a remote file name
     * @param remotePath a remote path of the file
     * @param localPath local path where downloaded file will be placed 
     * @throws Exception
     */
    public void downloadFile(String remoteFileName, String remotePath, String localPath) throws Exception;
}
