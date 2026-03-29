package pl.trafficapp;

import pl.trafficapp.domain.*;

import java.util.*;

public class IntersectionFactory {
    public static Intersection createStandardIntersection() {
        List<Road> inRoads = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            List<Lane> lanes = new ArrayList<>();

            Direction left = Direction.getLeftDirection(direction);
            Direction right = Direction.getRightDirection(direction);
            Direction straight = Direction.getOppositeDirection(direction);

            // LEFT TURN
            TrafficLight leftLight = new TrafficLight(true);
            Lane leftLane = new Lane(Set.of(left), leftLight);
            lanes.add(leftLane);

            // STRAIGHT
            TrafficLight straightLight = new TrafficLight();
            Lane middleLane = new Lane(Set.of(straight), straightLight);
            lanes.add(middleLane);

            // STRAIGHT / RIGHT
            TrafficLight rightLight = new TrafficLight();
            Lane rightLane = new Lane(Set.of(right, straight), rightLight);
            lanes.add(rightLane);

            inRoads.add(new Road(direction, lanes));
        }

        return new Intersection(inRoads);
    }
}
