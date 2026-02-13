package com.example.vroom.DTOs.map;

import java.util.List;

public class OSRMEnvelope {
    public List<MapOSRMRoute> routes;

    public static class MapOSRMRoute {
        public Geometry geometry;
        public double distance;
        public double duration;
    }

    public static class Geometry {
        public List<List<Double>> coordinates;
    }
}
