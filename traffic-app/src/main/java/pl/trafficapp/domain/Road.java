package pl.trafficapp.domain;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Road {
    private final Direction incomingDirection;
    private final List<Lane> lanes;

    public Road(Direction incomingDirection, List<Lane> lanes) {
        this.incomingDirection = incomingDirection;
        this.lanes = lanes;
    }

    public boolean isCorrect() {
        Set<Direction> possibleOutDirections = new HashSet<>();
        possibleOutDirections.add(incomingDirection);
        for (Lane lane : lanes) {
            possibleOutDirections.addAll(lane.getAllowedDirections());
        }
        return possibleOutDirections.size() == Direction.values().length;
    }
}
