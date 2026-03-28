package pl.trafficapp.domain;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class Lane {
    private final Set<Direction> allowedDirections;
    private final Queue<Vehicle> vehicles = new LinkedList<>();
    private final TrafficLight trafficLight;

    public Lane(Set<Direction> allowedDirections, TrafficLight trafficLight) {
        this.allowedDirections = allowedDirections;
        this.trafficLight = trafficLight;
    }

    public Set<Direction> getAllowedDirections() {
        return allowedDirections;
    }
    public Queue<Vehicle> getVehicles() {
        return vehicles;
    }
    public TrafficLight getTrafficLight() {
        return trafficLight;
    }
    public int getQueueSize() {
        return vehicles.size();
    }

    public boolean isDirectionAllowed(Direction direction) {
        return allowedDirections.contains(direction);
    }

    public boolean addVehicle(Vehicle vehicle) {
        return vehicles.add(vehicle);
    }
    public Optional<Vehicle> pollVehicle () {
        return Optional.ofNullable(vehicles.poll());
    }
    public Optional<Vehicle> peekVehicle() {
        return Optional.ofNullable(vehicles.peek());
    }
    public boolean isEmpty() {
        return vehicles.isEmpty();
    }
}
