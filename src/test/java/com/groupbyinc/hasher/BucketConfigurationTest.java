package com.groupbyinc.hasher;

import com.groupbyinc.common.jackson.Mappers;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BucketConfigurationTest {
    private static final int TRAFFIC_ALLOCATION = 75;
    private static final int[] BUCKET_PERCENTAGES = {25, 30, 30, 15};

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationWithEmptyBucketArray() throws Exception {
        new BucketConfiguration(new int[]{}, TRAFFIC_ALLOCATION);
    }

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationWhenPercentagesNull() throws Exception {
        new BucketConfiguration(null, TRAFFIC_ALLOCATION);
    }

    @Test(expected = ConfigurationException.class)
    public void testBucketConfigurationThrowsOverHundred() throws Exception {
        new BucketConfiguration(new int[]{20, 30, 30, 40}, TRAFFIC_ALLOCATION);
    }

    @Test
    public void testBucketConfigurationDoesNotThrowOnValidConfig() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, TRAFFIC_ALLOCATION);
    }

    @Test(expected = ConfigurationException.class)
    public void testMapFractionsToThresholdsTrafficAllocationGt100() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, 120);
    }

    @Test(expected = ConfigurationException.class)
    public void testMapFractionsToThresholdsTrafficAllocationlt0() throws Exception {
        new BucketConfiguration(BUCKET_PERCENTAGES, -10);
    }

    @Test
    public void testGenerateConfigFromJson() throws Exception {
        BucketConfiguration configuration = Mappers.readValue("{\"bucketPercentages\": [10,20,30,40], \"trafficAllocation\" : 45}".getBytes(), BucketConfiguration.class, false).init();
        assertArrayEquals(new int[]{10, 20, 30, 40}, configuration.getBucketPercentages());
        assertEquals(45, configuration.getTrafficAllocation());
    }

    @Test(expected = ConfigurationException.class)
    public void testGenerateFromJsonWithBadConfig() throws Exception {
        Mappers.readValue("{\"bucketPercentages\": [30,20,30,40], \"trafficAllocation\" : 45}".getBytes(), BucketConfiguration.class, false).init();
    }

}