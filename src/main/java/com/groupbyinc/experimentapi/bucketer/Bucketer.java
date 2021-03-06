package com.groupbyinc.experimentapi.bucketer;

import com.groupbyinc.util.StringUtils;
import com.sangupta.murmur.Murmur3;

public class Bucketer {

  private BucketConfiguration bucketConfiguration;
  private static final long MURMUR_SEED = 2321168210L;
  private static final long MAX_HASH_VALUE = (long) Math.floor(Math.pow(2, 32));
  private static final int NO_BUCKET = -1;

  private long[] bucketThresholds;

  public Bucketer(BucketConfiguration configuration) throws ConfigurationException {
    bucketConfiguration = configuration;
    init();
  }

  public int getBucketId(String targetString) throws ConfigurationException {
    if (targetString == null) {
      throw new ConfigurationException("Target string cannot be null");
    } else if (StringUtils.isBlank(targetString)) {
      throw new ConfigurationException("Target string cannot be empty or blank");
    }

    long stringHash = Murmur3.hash_x86_32(targetString.getBytes(), targetString.length(), MURMUR_SEED);
    return placeInBucket(stringHash, bucketThresholds);
  }

  protected static long[] mapFractionsToThresholds(int trafficAllocation, int trafficAllocationOffset, double[] fractions) {
    long[] bucketMaxHashThresholds = new long[fractions.length + 1];

    bucketMaxHashThresholds[0] = trafficAllocationOffset * MAX_HASH_VALUE / 100;
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

  public final Bucketer init() throws ConfigurationException {
    if (bucketConfiguration == null) {
      throw new ConfigurationException("Bucket configuration can not be null");
    }

    int trafficAllocation = bucketConfiguration.getTrafficAllocation();
    int trafficAllocationOffset = bucketConfiguration.getTrafficAllocationOffset();
    int[] bucketPercentages = bucketConfiguration.getBucketPercentages();
    double[] fractions = generateBucketFractions(bucketPercentages);
    bucketThresholds = mapFractionsToThresholds(trafficAllocation, trafficAllocationOffset, fractions);

    return this;
  }
}
