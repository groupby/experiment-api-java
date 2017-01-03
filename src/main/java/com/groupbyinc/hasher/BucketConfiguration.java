package com.groupbyinc.hasher;

import java.util.stream.IntStream;

public class BucketConfiguration {
    private int[] bucketPercentages;
    private int trafficAllocation;

    public BucketConfiguration() throws ConfigurationException{
    }

    public BucketConfiguration(int[] percentages, int allocation) throws ConfigurationException{
        bucketPercentages = percentages;
        trafficAllocation = allocation;
        init();
    }

    public final BucketConfiguration init() throws ConfigurationException {
        if (bucketPercentages == null){
            throw new ConfigurationException("Bucket percentages can not be null");
        }
        if (IntStream.of(bucketPercentages).sum() != 100) {
            throw new ConfigurationException("Bucket percentages should add to 100");
        }
        if (trafficAllocation <= 0 || trafficAllocation > 100) {
            throw new ConfigurationException("Traffic allocation must be an integer between 1 and 100");
        }
        return this;
    }


    public int[] getBucketPercentages() {
        return bucketPercentages;
    }

    public int getTrafficAllocation() {
        return trafficAllocation;
    }
}


