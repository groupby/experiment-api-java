package com.hasher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TrafficHashSplitter
 *
 * @author groupby
 */
public class TrafficHashSplitter {

    public static int getBucketFromSessionId(String sessionId, int trafficAllocation, int[] bucketPercentages) {

        List<Double> fractions = new ArrayList<>();

        for (int index = 0; index < bucketPercentages.length; index++) {
            fractions.add(index > 0 ? (double) (bucketPercentages[index] + bucketPercentages[index - 1]) / 100
                                    : (double) bucketPercentages[index] / 100);
        }

        List<Double> bucketMaxHashValues = fractions.stream().map(
                (fraction) -> fraction * trafficAllocation * Integer.MAX_VALUE).collect(Collectors.toList());

        return 3;
    }
}