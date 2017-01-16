package com.groupbyinc.example;

import com.groupbyinc.common.jackson.Mappers;
import com.groupbyinc.experimentapi.bucketer.BucketConfiguration;
import com.groupbyinc.experimentapi.bucketer.ConfigurationException;
import com.groupbyinc.experimentapi.bucketer.TrafficHashSplitter;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException, ConfigurationException {
        String bucketConfigurationContents = args[0];
        String sessionId = args[1];
        BucketConfiguration bucketConfiguration = Mappers.readValue(
                bucketConfigurationContents.getBytes(), BucketConfiguration.class, false);
        int bucket = TrafficHashSplitter.getBucketFromString(sessionId, bucketConfiguration);
        System.out.println("Bucket: " + bucket);
    }
}
