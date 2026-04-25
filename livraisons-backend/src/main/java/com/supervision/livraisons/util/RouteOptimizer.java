package com.supervision.livraisons.util;

import java.util.ArrayList;
import java.util.List;

import com.supervision.livraisons.model.Delivery;

public final class RouteOptimizer {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private RouteOptimizer() {
    }

    /**
     * Nearest-neighbour TSP heuristic.
     * Starts from the driver's current position and greedily picks
     * the closest unvisited delivery at each step.
     * Deliveries with no coordinates (0,0) are appended at the end.
     */
    public static List<Delivery> optimize(List<Delivery> deliveries, double startLat, double startLng) {
        if (deliveries == null || deliveries.size() <= 1) {
            return deliveries;
        }

        List<Delivery> withCoords = new ArrayList<>();
        List<Delivery> noCoords = new ArrayList<>();

        for (Delivery d : deliveries) {
            if (d.getLat() != 0 || d.getLng() != 0) {
                withCoords.add(d);
            } else {
                noCoords.add(d);
            }
        }

        List<Delivery> result = new ArrayList<>();
        double currentLat = startLat;
        double currentLng = startLng;

        while (!withCoords.isEmpty()) {
            int nearestIndex = 0;
            double minDist = Double.MAX_VALUE;

            for (int i = 0; i < withCoords.size(); i++) {
                double dist = haversine(currentLat, currentLng,
                        withCoords.get(i).getLat(), withCoords.get(i).getLng());
                if (dist < minDist) {
                    minDist = dist;
                    nearestIndex = i;
                }
            }

            Delivery nearest = withCoords.remove(nearestIndex);
            result.add(nearest);
            currentLat = nearest.getLat();
            currentLng = nearest.getLng();
        }

        result.addAll(noCoords);
        return result;
    }

    private static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
