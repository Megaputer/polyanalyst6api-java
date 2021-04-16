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
            Project prj = api.project("b48c8063-c51b-4a5c-bda2-666f4783442f");
            Dataset ds = prj.getDataset(11);
            System.out.println( ds.getValues(0, 1) );
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
