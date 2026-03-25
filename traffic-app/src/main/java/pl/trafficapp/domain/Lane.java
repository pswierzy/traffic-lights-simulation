package pl.trafficapp.domain;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class Lane {
    private final Set<Direction> allowedDirections;
    private final Queue<Vehicle> vehicles = new LinkedList<>();

    public Lane(Set<Direction> allowedDirections) {
        this.allowedDirections = allowedDirections;
    }

    public Set<Direction> getAllowedDirections() {
        return allowedDirections;
    }
    public boolean isDirectionAllowed(Direction direction) {
        return allowedDirections.contains(direction);
    }

    public Boolean addVehicle(Vehicle vehicle) {
        return vehicles.add(vehicle);
    }
    public Optional<Vehicle> pollVehicle () {
        return Optional.ofNullable(vehicles.poll());
    }
    public Boolean isEmpty() {
        return vehicles.isEmpty();
    }
}
