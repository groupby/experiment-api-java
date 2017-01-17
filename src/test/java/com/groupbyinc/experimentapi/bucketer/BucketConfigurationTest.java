package com.groupbyinc.experimentapi.bucketer;

import com.groupbyinc.common.jackson.Mappers;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BucketConfigurationTest {
    private static final int TRAFFIC_ALLOCATION = 75;
    private static final int TRAFFIC_ALLOCATION_OFFSET = 0;

    private static final int[] BUCKET_PERCENTAGES = {25, 30, 30, 15};

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationWithEmptyBucketArray() throws Exception {
        new BucketConfiguration(new int[]{}, TRAFFIC_ALLOCATION,TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationWhenPercentagesNull() throws Exception {
        new BucketConfiguration(null, TRAFFIC_ALLOCATION, TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationThrowsOverHundred() throws Exception {
        new BucketConfiguration(new int[]{20, 30, 30, 40}, TRAFFIC_ALLOCATION, TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test
    public void testBucketConfigurationDoesNotThrowOnValidConfig() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION, TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test(expected = ConfigurationException.class)
    public void testMapFractionsToThresholdsTrafficAllocationGt100() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, 120, TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test(expected = ConfigurationException.class)
    public void testMapFractionsToThresholdsTrafficAllocationlt0() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, -10, TRAFFIC_ALLOCATION_OFFSET);
    }

    @Test(expected = ConfigurationException.class)
    public void testOffsetAndAllocationGt100() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, 50, 51);
    }

    @Test(expected = ConfigurationException.class)
    public void offsetLt0() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, 50, -1);
    }

    @Test(expected = ConfigurationException.class)
    public void testGenerateFromJsonWithBucketsOver100() throws Exception {
        Mappers.readValue("{\"bucketPercentages\": [30,20,30,40], \"trafficAllocation\" : 45}".getBytes(), BucketConfiguration.class, false).init();
    }

    @Test(expected = ConfigurationException.class)
    public void testGenerateFromJsonWithAllocationNull() throws Exception {
        Mappers.readValue("{\"bucketPercentages\": [10,20,30,40]}".getBytes(), BucketConfiguration.class, false).init();
    }

    @Test(expected = ConfigurationException.class)
    public void testGenerateFromJsonWithOffsetNull() throws Exception {
        Mappers.readValue("{\"bucketPercentages\": [10,20,30,40], \"trafficAllocation\" : 45}".getBytes(), BucketConfiguration.class, false).init();
    }

    @Test
    public void testGenerateConfigFromJson() throws Exception {
        BucketConfiguration configuration = Mappers.readValue("{\"trafficAllocationOffset\": 2, \"bucketPercentages\": [10,20,30,40], \"trafficAllocation\" : 45}".getBytes(), BucketConfiguration.class, false).init();
        assertArrayEquals(new int[]{10, 20, 30, 40}, configuration.getBucketPercentages());
        assertEquals(45, configuration.getTrafficAllocation());
        assertEquals(2, configuration.getTrafficAllocationOffset());
    }
}
