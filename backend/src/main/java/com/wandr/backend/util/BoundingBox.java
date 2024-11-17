package com.wandr.backend.util;

/**
 * Utility class to represent a geographic bounding box defined by
 * minimum and maximum latitudes and longitudes.
 */
public class BoundingBox {

    private final double minLat;
    private final double maxLat;
    private final double minLng;
    private final double maxLng;

    public BoundingBox(double minLat, double maxLat, double minLng, double maxLng) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLng = minLng;
        this.maxLng = maxLng;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLng() {
        return minLng;
    }

    public double getMaxLng() {
        return maxLng;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "minLat=" + minLat +
                ", maxLat=" + maxLat +
                ", minLng=" + minLng +
                ", maxLng=" + maxLng +
                '}';
    }
}
