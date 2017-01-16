package com.groupbyinc.experimentapi.bucketer;

import com.sangupta.murmur.Murmur3;
import org.apache.commons.lang3.StringUtils;

public class TrafficHashSplitter {

  private BucketConfiguration bucketConfiguration = null;
  private static final long MURMUR_SEED = 2321168210L;
  private static final long MAX_HASH_VALUE = (long) Math.floor(Math.pow(2, 32));
  private static final int NO_BUCKET = -1;


  public TrafficHashSplitter(BucketConfiguration configuration) throws ConfigurationException {
    bucketConfiguration = configuration;
    init();
  }

  public int getBucketId(String targetString) throws ConfigurationException {

    int trafficAllocation = bucketConfiguration.getTrafficAllocation();
    int trafficAllocationOffset = bucketConfiguration.getTrafficAllocationOffset();
    int[] bucketPercentages = bucketConfiguration.getBucketPercentages();

    if (targetString == null) {
      throw new ConfigurationException("Target string cannot be null");
    } else if (StringUtils.isBlank(targetString)) {
      throw new ConfigurationException("Target string cannot be empty or blank");
    }

    double[] fractions = generateBucketFractions(bucketPercentages);

    long[] bucketThresholds = mapFractionsToThresholds(trafficAllocation, trafficAllocationOffset, fractions);

    long stringHash = Murmur3.hash_x86_32(targetString.getBytes(), targetString.length(), MURMUR_SEED);

    return placeInBucket(stringHash, bucketThresholds);
  }

  protected static long[] mapFractionsToThresholds(int trafficAllocation, int trafficAllocationOffset, double[] fractions) {
    long[] bucketMaxHashThresholds = new long[fractions.length + 1];

    bucketMaxHashThresholds[0] = trafficAllocationOffset / 100;
    for (int index = 0; index < fractions.length; index++) {
      long threshold = (long) Math.floor((fractions[index] * trafficAllocation + trafficAllocationOffset) * MAX_HASH_VALUE / 100);
      bucketMaxHashThresholds[index + 1] = threshold;
    }

    return bucketMaxHashThresholds;
  }

  protected static double[] generateBucketFractions(int[] bucketPercentages) {
    double[] fractions = new double[bucketPercentages.length];

    for (int index = 0; index < bucketPercentages.length; index++) {
      fractions[index] = index > 0 ? bucketPercentages[index] / 100.0 + fractions[index - 1] : bucketPercentages[index] / 100.0;
    }

    return fractions;
  }

  protected static int placeInBucket(long hashValue, long[] bucketThresholds) {
    int bucketId = NO_BUCKET;
    for (int index = 0; index < bucketThresholds.length; index++) {
      if (hashValue > bucketThresholds[index]) {
        bucketId = index;
      } else {
        return bucketId;
      }
    }
    return NO_BUCKET;
  }

  public BucketConfiguration getBucketConfiguration() {
    return bucketConfiguration;
  }

  public final TrafficHashSplitter init() throws ConfigurationException {
    if (bucketConfiguration == null){
      throw new ConfigurationException("Bucket configuration can not be null");
    }
    return this;
  }
}
