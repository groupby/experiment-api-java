//package com.groupbyinc.example;
//
//import com.google.common.base.Charsets;
//import com.google.common.io.Resources;
//import com.hasher.BucketConfiguration;
//import com.hasher.TrafficHashSplitter;
//
//import java.io.IOException;
//import java.net.URL;
//
//public class Driver {
//  public static void main(String[] args) throws IOException {
//    URL url = Resources.getResource(args[0]);
//    String bucketConfigurationContents = Resources.toString(url, Charsets.UTF_8);
//    BucketConfiguration bucketConfiguration = new BucketConfiguration();  // Use Jackson to do this.
//    String sessionId = args[1];
//    int bucket = TrafficHashSplitter.getBucketFromSessionId(sessionId, bucketConfiguration);
//    System.out.println("Bucket: " + bucket);
//  }
//
//}
