package pa6api;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            api = new PA6API("https://localhost:5043", "administrator", "");
            api.login();
            Project prj = api.project("b48c8063-c51b-4a5c-bda2-666f4783442f");
            System.out.println(prj.getTasks());
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
