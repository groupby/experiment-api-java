package com.hasher;

import com.sangupta.murmur.Murmur3;

import java.util.stream.IntStream;

public class TrafficHashSplitter {

    private static final long MURMUR_SEED = 2321168210L;
    private static final long MAX_HASH_VALUE = (long) Math.floor(Math.pow(2, 32));

    public static int getBucketFromSessionId(String sessionId, int trafficAllocation, int[] bucketPercentages) {

        double[] fractions = generateBucketFractions(bucketPercentages);

        long[] bucketMaxHashValues = mapFractionsToThresholds(trafficAllocation, fractions);

        long sessionHash = Murmur3.hash_x86_32(sessionId.getBytes(), sessionId.length(), MURMUR_SEED);

        int bucket = 0;

        for (long bucketMaxHashValue : bucketMaxHashValues) {
            if (sessionHash > bucketMaxHashValue) {
                bucket++;
            } else {
                return bucket;
            }
        }

        return -1;
    }

    public static long[] mapFractionsToThresholds(int trafficAllocation, double[] fractions) {

        long[] bucketMaxHashValues = new long[fractions.length];

        for (int index = 0; index < fractions.length; index++) {
            long maxHashValue = (long) Math.floor(fractions[index] * trafficAllocation / 100 * MAX_HASH_VALUE);
            bucketMaxHashValues[index] = maxHashValue;
        }

        return bucketMaxHashValues;
    }

    public static double[] generateBucketFractions(int[] bucketPercentages) {

        int sum = IntStream.of(bucketPercentages).sum();

        if (sum != 100) {
            throw new IllegalArgumentException("Bucket percentages should add to 100");
        }

        double[] fractions = new double[bucketPercentages.length];

        for (int index = 0; index < bucketPercentages.length; index++) {
            fractions[index] = index > 0 ? (double) bucketPercentages[index] / 100 + fractions[index - 1]
                                         : (double) bucketPercentages[index] / 100;
        }

        return fractions;
    }
}