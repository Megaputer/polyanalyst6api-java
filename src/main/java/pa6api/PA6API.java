package pa6api;

import java.util.List;

public interface PA6API {
    public void login(String userName, String pwd) throws Exception;
    public void logout() throws Exception;
    public List<String> versions() throws Exception;
    public Project project(String uuid) throws Exception;
}
