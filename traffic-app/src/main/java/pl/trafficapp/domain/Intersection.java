package pl.trafficapp.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection {
    private final Map<Direction, Road> inRoads;

    public Intersection(List<Road> roads) {
        this.inRoads = new HashMap<>();
        for (Road road : roads) {
            this.inRoads.put(road.getIncomingDirection(), road);
        }
    }

    public Road getRoad(Direction direction) {
        return this.inRoads.get(direction);
    }
    public Collection<Road> getAllRoads() {
        return inRoads.values();
    }

    // as for now we accept only 4 way intersections
    // T-interceptions are on a TODO list
    public boolean isValid() {
        for (Road road : inRoads.values()) {
            if (!road.isValid()) return false;
        }
        return inRoads.get(Direction.NORTH) != null &&
                inRoads.get(Direction.SOUTH) != null &&
                inRoads.get(Direction.EAST) != null &&
                inRoads.get(Direction.WEST) != null &&
                inRoads.size() == 4;
    }

    public boolean addVehicle(Vehicle vehicle) {
        return inRoads.get(vehicle.start()).addVehicle(vehicle);
    }

    public Road getRightRoad(Road road) {
        Direction dir = road.getIncomingDirection();

        return inRoads.get(Direction.getRightDirection(dir));
    }
    public Road getOppositeRoad(Road road) {
        Direction dir = road.getIncomingDirection();

        return inRoads.get(Direction.getOppositeDirection(dir));
    }
    public Road getLeftRoad(Road road) {
        Direction dir = road.getIncomingDirection();

        return inRoads.get(Direction.getLeftDirection(dir));
    }
}
