package pl.trafficapp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection {
    private final Map<Direction, Road> inRoads;
    private final Map<Direction, TrafficLight> lights;

    public Intersection(List<Road> roads, List<TrafficLight> lights) {
        this.inRoads = new HashMap<>();
        this.lights = new HashMap<>();
    }

    public void addVehicle(Vehicle vehicle) {
        //TODO
    }
}
