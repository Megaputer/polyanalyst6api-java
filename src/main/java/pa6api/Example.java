package pa6api;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            api = new PA6API("https://10.0.0.9:5043", "administrator", "");
            System.out.println(api.versions());
            System.out.println(api.login().body());
            System.out.println(api.logout().body());
            //Project prj = api.project("b48c8063-c51b-4a5c-bda2-666f4783442f");
            //System.out.println(prj.getNodeList());
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
