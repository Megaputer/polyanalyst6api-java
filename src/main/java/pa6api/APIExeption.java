package pa6api;

public class APIExeption extends Exception {
    public String title = "";
    public String message = "";
    public int status = 0;

    public APIExeption(int status, String title, String message) {
        super();

        this.title = title;
        this.message = message;
        this.status = status;
    }

    @Override
    public String toString() {
        String res = "status: " + status;
        if (!title.isEmpty())
            res += ", " + title;
        
        if (!message.isEmpty())
            res += ", " + message;
        return res;
    }
}
