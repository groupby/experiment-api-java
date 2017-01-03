package com.groupbyinc.hasher;

import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static com.groupbyinc.hasher.TrafficHashSplitter.generateBucketFractions;
import static com.groupbyinc.hasher.TrafficHashSplitter.getBucketFromSessionId;
import static com.groupbyinc.hasher.TrafficHashSplitter.mapFractionsToThresholds;
import static org.junit.Assert.assertArrayEquals;

public class TrafficHashSplitterTest {
    private static final int TRAFFIC_ALLOCATION = 75;
    private static final int[] BUCKET_PERCENTAGES = {25, 30, 30, 15};

    @Test
    public void testHashReturnsExpectedBuckets() throws Exception {

        String[] testStrings = new Scanner(new File("src/test/resources/testStrings.csv")).nextLine().split(",");
        int[] hashResults = new int[testStrings.length];

        for (int ind = 0; ind < testStrings.length; ind++) {
            BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION);

            hashResults[ind] = getBucketFromSessionId(testStrings[ind], configuration);
        }

        String[] expBucketsStr = new Scanner(new File("src/test/resources/expectedBuckets.csv")).nextLine().split(",");
        int[] expectedBuckets = new int[expBucketsStr.length];

        for (int ind = 0; ind < testStrings.length; ind++) {
            expectedBuckets[ind] = Integer.parseInt(expBucketsStr[ind]);
        }
        assertArrayEquals(expectedBuckets, hashResults);
    }

    @Test
    public void testMapsFractionsToThresholds() throws Exception {
        long[] thresholds = mapFractionsToThresholds(75, new double[]{0.1, 0.3, 0.6, 1});
        assertArrayEquals(new long[]{322122547L, 966367641L, 1932735283L, 3221225472L}, thresholds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBucketWhenSessionIdNull() throws Exception {
        BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION);
        getBucketFromSessionId(null, configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBucketWhenSessionIdEmpty() throws Exception {
        BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION);
        getBucketFromSessionId("", configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBucketWhenSessionIdBlank() throws Exception {
        BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION);
        getBucketFromSessionId("   ", configuration);
    }

    @Test
    public void testGenerateBucketsFractionsReturnsAsExpected() throws Exception {
        int[] testBuckets = {10, 20, 30, 40};
        assertArrayEquals(new double[]{0.1, 0.3, 0.6, 1}, generateBucketFractions(testBuckets), 0.001);
    }
}