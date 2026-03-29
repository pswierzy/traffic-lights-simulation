package pl.trafficapp.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.MAX_VALUE;

public class Road {
    private final Direction incomingDirection;
    private final List<Lane> lanes;

    public Road(Direction incomingDirection, List<Lane> lanes) {
        this.incomingDirection = incomingDirection;
        this.lanes = lanes;
    }

    public Direction getIncomingDirection() {
        return incomingDirection;
    }
    public List<Lane> getLanes() {
        return lanes;
    }

    // Structure validation - can you go everywhere from this road
    public boolean isValid() {
        Set<Direction> possibleOutDirections = new HashSet<>();
        possibleOutDirections.add(incomingDirection);
        for (Lane lane : lanes) {
            possibleOutDirections.addAll(lane.getAllowedDirections());
            // directional left turns should be ONLY for left turns
            if (lane.getAllowedDirections().contains(Direction.getLeftDirection(incomingDirection)) &&
                    lane.getTrafficLight().isDirectional() &&
                    lane.getAllowedDirections().size() != 1) {
                return false;
            }
        }
        return possibleOutDirections.size() == Direction.values().length;
    }

    public boolean addVehicle(Vehicle vehicle) {
        Lane shortestLane = null;
        int shortestLine = MAX_VALUE;

        for (Lane lane : lanes) {
            if (lane.isDirectionAllowed(vehicle.end())) {
                if (shortestLine > lane.getQueueSize()) {
                    shortestLine = lane.getQueueSize();
                    shortestLane = lane;
                }
            }
        }

        return shortestLane != null && shortestLane.addVehicle(vehicle);
    }
}
