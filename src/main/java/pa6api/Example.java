package pa6api;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            api = PA6APIImpl.create("https://10.0.0.9:5043");
            api.login("administrator", "");
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
