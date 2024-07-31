// DirectionsResponse.java
package com.wandr.backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DirectionsResponse {
    @JsonProperty("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public static class Route {
        @JsonProperty("waypoint_order")
        private List<Integer> waypointOrder;

        public List<Integer> getWaypointOrder() {
            return waypointOrder;
        }

        public void setWaypointOrder(List<Integer> waypointOrder) {
            this.waypointOrder = waypointOrder;
        }
    }
}
