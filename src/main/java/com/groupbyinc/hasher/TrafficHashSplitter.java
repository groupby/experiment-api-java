package com.groupbyinc.hasher;

import com.sangupta.murmur.Murmur3;
import org.apache.commons.lang3.StringUtils;

public class TrafficHashSplitter {

    private static final long MURMUR_SEED = 2321168210L;
    private static final long MAX_HASH_VALUE = (long) Math.floor(Math.pow(2, 32));

    public static int getBucketFromString(String targetString, BucketConfiguration configuration) throws ConfigurationException {

        int trafficAllocation = configuration.getTrafficAllocation();
        int[] bucketPercentages = configuration.getBucketPercentages();

        if (targetString == null) {
            throw new ConfigurationException("Target string cannot be null");
        } else if (StringUtils.isBlank(targetString)) {
            throw new ConfigurationException("Target string cannot be empty or blank");
        }

        double[] fractions = generateBucketFractions(bucketPercentages);

        long[] bucketMaxHashValues = mapFractionsToThresholds(trafficAllocation, fractions);

        long stringHash = Murmur3.hash_x86_32(targetString.getBytes(), targetString.length(), MURMUR_SEED);

        int bucket = 0;

        for (long bucketMaxHashValue : bucketMaxHashValues) {
            if (stringHash > bucketMaxHashValue) {
                bucket++;
            } else {
                return bucket;
            }
        }
        return -1;
    }

    protected static long[] mapFractionsToThresholds(int trafficAllocation, double[] fractions) {
        long[] bucketMaxHashValues = new long[fractions.length];

        for (int index = 0; index < fractions.length; index++) {
            long maxHashValue = (long) Math.floor(fractions[index] * trafficAllocation / 100 * MAX_HASH_VALUE);
            bucketMaxHashValues[index] = maxHashValue;
        }

        return bucketMaxHashValues;
    }

    protected static double[] generateBucketFractions(int[] bucketPercentages) {
        double[] fractions = new double[bucketPercentages.length];

        for (int index = 0; index < bucketPercentages.length; index++) {
            fractions[index] = index > 0 ? bucketPercentages[index] / 100.0 + fractions[index - 1]
                                         : bucketPercentages[index] / 100.0;
        }

        return fractions;
    }
}