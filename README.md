# pa6api

Megaputer Polyanalyst 6 API

## Requirements

Building the API client library requires:
1. Java 1.8
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


