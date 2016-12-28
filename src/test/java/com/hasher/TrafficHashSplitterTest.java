package com.hasher;

import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author groupby
 */
public class TrafficHashSplitterTest {
    private static final int TRAFFIC_ALLOCATION = 75;
    private static final int[] BUCKET_PERCENTAGES = {25, 30, 30, 15};

    @Test
    public void testHashReturnsExpectedBuckets() throws Exception {

        String[] testStrings = new Scanner(new File("src/test/resources/testStrings.csv")).nextLine().split(",");
        int[] hashResults = new int[testStrings.length];

        for (int ind = 0; ind < testStrings.length; ind++) {
            hashResults[ind] = TrafficHashSplitter.getBucketFromSessionId(
                    testStrings[ind], TRAFFIC_ALLOCATION, BUCKET_PERCENTAGES);
        }

        String[] expBucketsStr = new Scanner(new File("src/test/resources/expectedBuckets.csv")).nextLine().split(",");
        int[] expectedBuckets = new int [expBucketsStr.length];

        for (int ind = 0; ind < testStrings.length; ind++) {
            expectedBuckets[ind] = Integer.parseInt(expBucketsStr[ind]);
        }
        assertArrayEquals(expectedBuckets, hashResults);
    }
}