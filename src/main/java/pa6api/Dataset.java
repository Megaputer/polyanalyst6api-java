package pa6api;

import java.util.List;

public interface Dataset {
    public List<Integer> getColumnIndexes(String cols[]) throws Exception;
    
    /**
     * Returns an information about dataset: count of rows, list of columns and so on
     * @return information about dataset
     * @see DatasetInfo
     * @throws Exception
     */
    public DatasetInfo getInfo() throws Exception;

    /**
     * Returns dataset rows in a specific range. If passed range is out of dataset range it will return empty list.
     * @param offset start row position
     * @param rowCount count of rows
     * @return List of rows where a row is a list of columns values e.g. 
     * [[col1 value, col2 value, col3 value, ...], [col1 value, col2 value, col3 value, ...], ...]
     * Type of value can be as long, double or string that depends on column type
     * @throws Exception
     */
    public List<List<Object>> getValues(long offset, long rowCount) throws Exception;

    /**
     * Returns dataset rows in a specific range. If passed range is out of dataset range it will return empty list. 
     * If dataset does not contain specific column passed through parameter cols it will throw exception.
     * To get indexes by name of columns used getColumnIndexes
     * 
     * @param offset start row position
     * @param rowCount count of rows
     * @param cols indexes of columns that needed to get
     * @return List of rows where a row is a list of columns values e.g. 
     * 
     * [[col1 value, col2 value, col3 value, ...], [col1 value, col2 value, col3 value, ...], ...]
     * @throws Exception
     */
    public List<List<Object>> getValues(long offset, long rowCount, List<Integer> cols) throws Exception;
}
