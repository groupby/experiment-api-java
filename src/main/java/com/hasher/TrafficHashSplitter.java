package com.hasher;

import com.sangupta.murmur.Murmur3;

import java.util.stream.IntStream;

public class TrafficHashSplitter {

    private static final long MURMUR_SEED = 2321168210L;
    private static final long MAX_HASH_VALUE = (long) Math.floor(Math.pow(2, 32));

    protected static int getBucketFromSessionId(String sessionId, int trafficAllocation, int[] bucketPercentages) {

        if (sessionId == null) {
            throw new IllegalArgumentException("Session id cannot be null");
        } else if (sessionId.equals("") || sessionId.matches("^[\\s]*$")){
            throw new IllegalArgumentException("Session id cannot be empty or blank");
        }

        if (bucketPercentages == null) {
            throw new IllegalArgumentException("Bucket Percentages cannot be null");
        }

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

    protected static long[] mapFractionsToThresholds(int trafficAllocation, double[] fractions) {
        if (trafficAllocation <= 0 || trafficAllocation >100){
            throw new IllegalArgumentException("Traffic allocation must be an integer between 1 and 100");
        }

        long[] bucketMaxHashValues = new long[fractions.length];

        for (int index = 0; index < fractions.length; index++) {
            long maxHashValue = (long) Math.floor(fractions[index] * trafficAllocation / 100 * MAX_HASH_VALUE);
            bucketMaxHashValues[index] = maxHashValue;
        }

        return bucketMaxHashValues;
    }

    protected static double[] generateBucketFractions(int[] bucketPercentages) {

        if (IntStream.of(bucketPercentages).sum() != 100) {
            throw new IllegalArgumentException("Bucket percentages should add to 100");
        }

        double[] fractions = new double[bucketPercentages.length];

        for (int index = 0; index < bucketPercentages.length; index++) {
            fractions[index] = index > 0 ?  bucketPercentages[index] / 100.0 + fractions[index - 1]
                                         :  bucketPercentages[index] / 100.0;
        }

        return fractions;
    }
}