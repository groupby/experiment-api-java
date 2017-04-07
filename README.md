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
  <version>1.0.1</version>
  <classifier>uber</classifier>
</dependency>
```

### Examples

#### Bucketing by Session ID

```java
int[] bucketPercentages = new int[]{10, 30, 40, 20};
int trafficAllocation = 50;
int trafficAllocationOffset = 0;

BucketConfiguration configuration = new BucketConfiguration(bucketPercentages, trafficAllocation, trafficAllocationOffset);
Bucketer bucketer = new Bucketer(configuration);
int bucketId = bucketer.getBucketId("sessionId string goes here");   

switch (bucketId) {
  case -1:
    System.out.println("No bucket");
    break;
  case 0:
    System.out.println("First bucket. 10% bucket");
    break;
  case 1:
    System.out.println("Second bucket. 30% bucket");
    break;
  case 2:
    System.out.println("Third bucket. 40% bucket");
    break;
  case 3:
    System.out.println("Forth bucket. 20% bucket");
    break;
  default:
    System.out.println("Should not happen");
    break;
}
```
