package pa6api;

public class APIResultException extends Exception {
    public APIResultException() {
    }

    public APIResultException(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        String str = "Unexpected result";
        String msg = getMessage();
        if (!msg.isEmpty())
            str += ", " + msg;
        return str;
    }
}
