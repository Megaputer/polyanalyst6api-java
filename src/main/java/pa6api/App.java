package pa6api;


/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        PA6API api = new PA6API("https://10.0.0.9:5043", "administrator", "");
        try {
            api.loginRaw();
            Project prj = api.project("c48c8063-c51b-4a5c-bda2-666f4783442f");
            System.out.println(prj.getNodeList());
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
