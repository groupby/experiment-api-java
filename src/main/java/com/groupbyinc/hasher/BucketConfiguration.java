package com.groupbyinc.hasher;

import com.groupbyinc.common.jackson.Mappers;

import java.util.stream.IntStream;

public class BucketConfiguration {
    private int[] bucketPercentages;
    private int trafficAllocation;


    public BucketConfiguration(String jsonConfig){
        Mappers.readValue(jsonConfig.getBytes(), BucketConfiguration.class, false);
    }

    public BucketConfiguration(int[] percentages, int allocation) throws ConfigurationException {

        if (percentages == null){
            throw new ConfigurationException("Bucket percentages can not be null");
        }
        if (IntStream.of(percentages).sum() != 100) {
            throw new ConfigurationException("Bucket percentages should add to 100");
        }
        bucketPercentages = percentages;

        if (allocation <= 0 || allocation > 100) {
            throw new ConfigurationException("Traffic allocation must be an integer between 1 and 100");
        }
        trafficAllocation = allocation;
    }

    public int[] getBucketPercentages() {
        return bucketPercentages;
    }

    public int getTrafficAllocation() {
        return trafficAllocation;
    }

}


