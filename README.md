# pa6api

Megaputer Polyanalyst 6 API

## Requirements

Building the API client library requires:
1. Java 11
2. Maven

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn clean deploy
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.megaputer.pa6api</groupId>
  <artifactId>pa6api</artifactId>
  <version>1.0</version>
  <scope>compile</scope>
</dependency>
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

* `target/pa6api-1.0.jar`
* `target/pa6api-1.0-sources.jar`

## Usage
```java
import java.util.List;
import pa6api.Dataset;
import pa6api.DatasetInfo;
import pa6api.Node;
import pa6api.PA6API;
import pa6api.PA6APIImpl;
import pa6api.Project;

public final class App {
    public static void main(String[] args) {
        String serverUrl = "https://localhost:5043";
        String userName = "administrator";
        String password = "";
        String prjUUID = "fcb2a7f7-c65e-40c5-b0b0-6393069cbc9f";
        String datasetName = "CarData.csv";
        try {
            // Create API instance
            PA6API api = PA6APIImpl.create(serverUrl);

            // Authentication
            api.login(userName, password);

            // Get project API instance
            Project prj = api.project(prjUUID);

            // Get node list
            List<Node> nodes = prj.getNodeList();

            // Get dataset instance by id
            Dataset ds = prj.getDataset(25);

            // Get dataset instance by name
            ds = prj.getDataset(datasetName);

            // Get dataset info
            DatasetInfo info = ds.getInfo();

            // Iterate over all rows
            long offset = 0;
            long rowsNum = 100;
            List<List<Object>> rows = ds.getValues(offset, rowsNum);
            while (!rows.isEmpty()) {
                offset += rows.size();
                rows = ds.getValues(offset, rowsNum);
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
```
