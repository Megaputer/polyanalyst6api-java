package pa6api;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        PA6API api;
        try {
            api = new PA6API("https://localhost:5043");
            api.login("administrator", "");
            Project prj = api.project("b48c8063-c51b-4a5c-bda2-666f4783442f");
            // System.out.println(prj.getNodeList());
            Dataset ds = prj.getDataset(25);
            String guid = ds.wrapperGuid();
            DatasetInfo info = ds.getInfo(guid);
            System.out.println(ds.getValues(guid, 0, 5).body());
            // System.out.println( prj.getNodeList() );
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
