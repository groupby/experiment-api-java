package com.hasher;

import java.util.stream.IntStream;

/**
 * TrafficHashSplitter
 *
 * @author groupby
 */
public class TrafficHashSplitter {

    public static int getBucketFromSessionId(String sessionId, int trafficAllocation, int[] bucketPercentages) {

        double[] fractions = generateBucketFractions(bucketPercentages);

//        List<Double> bucketMaxHashValues = fractions.stream().map(
//                (fraction) -> fraction * trafficAllocation * Integer.MAX_VALUE).collect(Collectors.toList());

        return 3;
    }

    public static double[] generateBucketFractions(int[] bucketPercentages) {
        int sum = IntStream.of(bucketPercentages).sum();

        if (sum != 100) {
            throw new IllegalArgumentException("Bucket percentages should add to 100");
        }

        double[] fractions = new double[bucketPercentages.length];
        for (int index = 0; index < bucketPercentages.length; index++) {
            fractions[index] = index > 0 ? (double)bucketPercentages[index] / 100 + fractions[index - 1]
                                         : (double) bucketPercentages[index] / 100;
        }
        return fractions;
    }
}