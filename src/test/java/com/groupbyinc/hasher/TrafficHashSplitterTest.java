package com.groupbyinc.hasher;

import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static com.groupbyinc.hasher.TrafficHashSplitter.generateBucketFractions;
import static com.groupbyinc.hasher.TrafficHashSplitter.getBucketFromString;
import static com.groupbyinc.hasher.TrafficHashSplitter.mapFractionsToThresholds;
import static com.groupbyinc.hasher.TrafficHashSplitter.placeInBucket;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TrafficHashSplitterTest {

  private static final int TRAFFIC_ALLOCATION = 75;
  private static final int NO_OFFSET = 0;
  private static final int TRAFFIC_ALLOCATION_OFFSET = 15;
  private static final int[] BUCKET_PERCENTAGES = {25, 30, 30, 15};

  @Test
  public void testHashReturnsExpectedBucketsNoOffset() throws Exception {

    String[] testStrings = new Scanner(new File("src/test/resources/testStrings.csv")).nextLine()
        .split(",");
    int[] hashResults = new int[testStrings.length];

    for (int ind = 0; ind < testStrings.length; ind++) {
      BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, NO_OFFSET);

      hashResults[ind] = getBucketFromString(testStrings[ind], configuration);
    }

    String[] expBucketsStr = new Scanner(new File("src/test/resources/expectedBuckets.csv")).nextLine()
        .split(",");
    int[] expectedBuckets = new int[expBucketsStr.length];

    for (int ind = 0; ind < testStrings.length; ind++) {
      expectedBuckets[ind] = Integer.parseInt(expBucketsStr[ind]);
    }
    assertArrayEquals(expectedBuckets, hashResults);
  }

  @Test
  public void testHashReturnsExpectedBuckets15Offset() throws Exception {

    String[] testStrings = new Scanner(new File("src/test/resources/testStrings.csv")).nextLine()
        .split(",");
    int[] hashResults = new int[testStrings.length];

    for (int ind = 0; ind < testStrings.length; ind++) {
      BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, TRAFFIC_ALLOCATION_OFFSET);

      hashResults[ind] = getBucketFromString(testStrings[ind], configuration);
    }

    String[] expBucketsStr = new Scanner(new File("src/test/resources/expectedBuckets15Offset.csv")).nextLine()
        .split(",");
    int[] expectedBuckets = new int[expBucketsStr.length];

    for (int ind = 0; ind < testStrings.length; ind++) {
      expectedBuckets[ind] = Integer.parseInt(expBucketsStr[ind]);
    }
    assertArrayEquals(expectedBuckets, hashResults);
  }

  @Test
  public void testMapsFractionsToThresholds() throws Exception {
    long[] thresholds = mapFractionsToThresholds(75, 0, new double[]{0.1, 0.3, 0.6, 1});
    assertArrayEquals(new long[]{0, 322122547L, 966367641L, 1932735283L, 3221225472L}, thresholds);
  }

  @Test
  public void testPlaceInBucketFirstBucket() throws Exception {
    long hashValue = 3L;
    long[] thresholds = new long[]{1L, 5L, 8L, 9L};
    assertEquals(0, placeInBucket(hashValue, thresholds));
  }

  @Test
  public void testPlaceInBucketLastBucket() throws Exception {
    long hashValue = 9L;
    long[] thresholds = new long[]{1L, 5L, 8L, 10L};
    assertEquals(2, placeInBucket(hashValue, thresholds));
  }

  @Test
  public void testPlaceInBucketLowerBound() throws Exception {
    long hashValue = 1L;
    long[] thresholds = new long[]{2L, 5L, 8L, 10L};
    assertEquals(-1, placeInBucket(hashValue, thresholds));
  }

  @Test
  public void testPlaceInBucketUpperBound() throws Exception {
    long hashValue = 11L;
    long[] thresholds = new long[]{2L, 5L, 8L, 10L};
    assertEquals(-1, placeInBucket(hashValue, thresholds));
  }

  @Test(expected = ConfigurationException.class)
  public void testGetBucketWhenSessionIdNull() throws Exception {
    BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, NO_OFFSET);
    getBucketFromString(null, configuration);
  }

  @Test(expected = ConfigurationException.class)
  public void testGetBucketWhenSessionIdEmpty() throws Exception {
    BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, NO_OFFSET);
    getBucketFromString("", configuration);
  }

  @Test(expected = ConfigurationException.class)
  public void testGetBucketWhenSessionIdBlank() throws Exception {
    BucketConfiguration configuration = new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, NO_OFFSET);
    getBucketFromString("   ", configuration);
  }

  @Test
  public void testGenerateBucketsFractionsReturnsAsExpected() throws Exception {
    int[] testBuckets = {10, 20, 30, 40};
    assertArrayEquals(new double[]{0.1, 0.3, 0.6, 1}, generateBucketFractions(testBuckets), 0.001);
  }
}