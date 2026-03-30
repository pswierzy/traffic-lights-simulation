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

    public static Intersection createSimpleIntersection() {
        List<Road> inRoads = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            List<Lane> lanes = new ArrayList<>();

            Direction left = Direction.getLeftDirection(direction);
            Direction right = Direction.getRightDirection(direction);
            Direction straight = Direction.getOppositeDirection(direction);

            // LEFT / STRAIGHT / RIGHT - ONE LANE
            TrafficLight mainLight = new TrafficLight();
            Lane singleLane = new Lane(Set.of(left, straight, right), mainLight);
            lanes.add(singleLane);

            inRoads.add(new Road(direction, lanes));
        }

        return new Intersection(inRoads);
    }

    public static Intersection createTwoLaneIntersection() {
        List<Road> inRoads = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            List<Lane> lanes = new ArrayList<>();

            Direction left = Direction.getLeftDirection(direction);
            Direction right = Direction.getRightDirection(direction);
            Direction straight = Direction.getOppositeDirection(direction);

            // LEFT / STRAIGHT
            TrafficLight leftStraightLight = new TrafficLight();
            Lane leftLane = new Lane(Set.of(left, straight), leftStraightLight);
            lanes.add(leftLane);

            // STRAIGHT / RIGHT
            TrafficLight rightStraightLight = new TrafficLight();
            Lane rightLane = new Lane(Set.of(right, straight), rightStraightLight);
            lanes.add(rightLane);

            inRoads.add(new Road(direction, lanes));
        }

        return new Intersection(inRoads);
    }

    public static Intersection createUnevenIntersection() {
        List<Road> inRoads = new ArrayList<>();

        // NORTH
        Direction direction = Direction.NORTH;
        Direction left = Direction.getLeftDirection(direction);
        Direction right = Direction.getRightDirection(direction);
        Direction straight = Direction.getOppositeDirection(direction);
        List<Lane> lanes = new ArrayList<>();

        // LEFT / STRAIGHT
        TrafficLight leftStraightLight = new TrafficLight();
        Lane leftLane = new Lane(Set.of(left, straight), leftStraightLight);
        lanes.add(leftLane);

        // STRAIGHT / RIGHT
        TrafficLight rightStraightLight = new TrafficLight();
        Lane rightLane = new Lane(Set.of(right, straight), rightStraightLight);
        lanes.add(rightLane);

        inRoads.add(new Road(direction, lanes));

        // SOUTH
        direction = Direction.SOUTH;
        left = Direction.getLeftDirection(direction);
        right = Direction.getRightDirection(direction);
        straight = Direction.getOppositeDirection(direction);
        lanes = new ArrayList<>();

        // LEFT / STRAIGHT / RIGHT - ONE LANE
        TrafficLight mainLight = new TrafficLight();
        Lane singleLane = new Lane(Set.of(left, straight, right), mainLight);
        lanes.add(singleLane);

        inRoads.add(new Road(direction, lanes));

        // WEST
        direction = Direction.WEST;
        left = Direction.getLeftDirection(direction);
        right = Direction.getRightDirection(direction);
        straight = Direction.getOppositeDirection(direction);
        lanes = new ArrayList<>();

        // LEFT TURN
        TrafficLight leftLight = new TrafficLight(true);
        leftLane = new Lane(Set.of(left), leftLight);
        lanes.add(leftLane);

        // STRAIGHT
        TrafficLight straightLight = new TrafficLight();
        Lane middleLane = new Lane(Set.of(straight), straightLight);
        lanes.add(middleLane);

        // STRAIGHT / RIGHT
        TrafficLight rightLight = new TrafficLight();
        rightLane = new Lane(Set.of(right, straight), rightLight);
        lanes.add(rightLane);

        inRoads.add(new Road(direction, lanes));

        // EAST
        direction = Direction.EAST;
        left = Direction.getLeftDirection(direction);
        right = Direction.getRightDirection(direction);
        straight = Direction.getOppositeDirection(direction);
        lanes = new ArrayList<>();

        // LEFT TURN
        leftLight = new TrafficLight(true);
        leftLane = new Lane(Set.of(left), leftLight);
        lanes.add(leftLane);

        // STRAIGHT / RIGHT
        rightLight = new TrafficLight();
        rightLane = new Lane(Set.of(right, straight), rightLight);
        lanes.add(rightLane);

        inRoads.add(new Road(direction, lanes));

        return new Intersection(inRoads);
    }
}