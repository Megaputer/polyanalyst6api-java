package pa6api;

import java.util.List;

public interface Dataset {
    public List<Integer> getColumnIndexes(String wrapperGuid, String cols[]) throws Exception;
    public String wrapperGuid() throws Exception;
    public DatasetInfo getInfo(String wrapperGuid) throws Exception;
    public List<List<Object>> getValues(String wrapperGuid, long offset, long rowCount) throws Exception;
    public List<List<Object>> getValues(String wrapperGuid, long offset, long rowCount, List<Integer> cols) throws Exception;
}
