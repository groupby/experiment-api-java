GroupBy Experiment API
========
 
Please follow the steps carefully to ensure a successful build.

### To install:

    mvn
    
This will by default run the goals `clean` and `install`.


### To test:

    mvn -Punit
    
This will install the project and run all unit tests. By default, tests are not run.


### Add this library as a dependency to your project:
The Uber JAR must be used to ensure shaded dependencies are included correctly.

#### Maven

```xml
<dependency>
  <groupId>com.groupbyinc</groupId>
  <artifactId>experiment-api-java</artifactId>
  <version>VERSION</version>
  <classifier>uber</classifier>
</dependency>
```

### Examples

#### Bucketing by Session ID

```java
    BucketConfiguration configuration = new BucketConfiguration(new int[]{10, 30, 40, 20}, 50, 0);
    int bucketId = TrafficHashSplitter.getBucketFromString("sessionId", configuration);   
```
