package com.groupbyinc.experimentapi.bucketer;

import java.util.stream.IntStream;

public class BucketConfiguration {

  private int[] bucketPercentages;
  private int trafficAllocation = -1;
  private int trafficAllocationOffset = -1;

  public BucketConfiguration() {
    // empty constructor for jackson
  }

  public BucketConfiguration(int[] percentages, int allocation, int allocationOffset) throws ConfigurationException {
    bucketPercentages = percentages;
    trafficAllocation = allocation;
    trafficAllocationOffset = allocationOffset;
    init();
  }

  public final BucketConfiguration init() throws ConfigurationException {
    if (bucketPercentages == null) {
      throw new ConfigurationException("Bucket percentages can not be null");
    } else if (IntStream.of(bucketPercentages).sum() != 100) {
      throw new ConfigurationException("Bucket percentages should add to 100");
    } else if (trafficAllocation <= 0 || trafficAllocation > 100) {
      throw new ConfigurationException("Traffic allocation must be an integer between 1 and 100");
    } else if (trafficAllocationOffset < 0 || trafficAllocationOffset > 100) {
      throw new ConfigurationException("Traffic allocation offset must be an integer between 0 and 100");
    } else if (trafficAllocation + trafficAllocationOffset > 100) {
      throw new ConfigurationException("Sum of trafficAllocation and trafficAllocationOffset must be less than 100");
    }
    return this;
  }

  public int[] getBucketPercentages() {
    return bucketPercentages;
  }

  public int getTrafficAllocation() {
    return trafficAllocation;
  }

  public int getTrafficAllocationOffset() {
    return trafficAllocationOffset;
  }
}