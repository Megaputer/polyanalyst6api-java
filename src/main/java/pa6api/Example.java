package pa6api;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            int crimedataId = 236;
            int numSeqId = 12;
            int cardataId = 25;
            api = PA6APIImpl.create("https://10.0.0.9:5043");
            api.login("administrator", "");
            //Project prj = api.project("b48c8063-c51b-4a5c-bda2-666f4783442f");
            Project prj = api.project("fcb2a7f7-c65e-40c5-b0b0-6393069cbc9f");
            prj.delete();
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
