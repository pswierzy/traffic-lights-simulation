package pl.trafficapp;

import pl.trafficapp.domain.*;
import java.util.*;

public class IntersectionFactory {

    public static Intersection createStandardIntersection() {
        return createSymmetric(dir -> List.of(
                lane(dir, true,  "L"),
                lane(dir, false, "S"),
                lane(dir, false, "SR")
        ));
    }

    public static Intersection createSimpleIntersection() {
        return createSymmetric(dir -> List.of(
                lane(dir, false, "LSR")
        ));
    }

    public static Intersection createTwoLaneIntersection() {
        return createSymmetric(dir -> List.of(
                lane(dir, false, "LS"),
                lane(dir, false, "SR")
        ));
    }

    public static Intersection createUnevenIntersection() {
        List<Road> roads = new ArrayList<>();

        roads.add(new Road(Direction.NORTH, List.of(lane(Direction.NORTH, false, "LS"), lane(Direction.NORTH, false, "SR"))));

        roads.add(new Road(Direction.SOUTH, List.of(lane(Direction.SOUTH, false, "LSR"))));

        roads.add(new Road(Direction.WEST, List.of(
                lane(Direction.WEST, true, "L"),
                lane(Direction.WEST, false, "S"),
                lane(Direction.WEST, false, "SR")
        )));

        roads.add(new Road(Direction.EAST, List.of(lane(Direction.EAST, true, "L"), lane(Direction.EAST, false, "SR"))));

        return new Intersection(roads);
    }

    private static Intersection createSymmetric(java.util.function.Function<Direction, List<Lane>> roadTemplate) {
        List<Road> roads = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            roads.add(new Road(dir, roadTemplate.apply(dir)));
        }
        return new Intersection(roads);
    }

    private static Lane lane(Direction incoming, boolean isDirectional, String spec) {
        Set<Direction> allowed = new HashSet<>();
        if (spec.contains("L")) allowed.add(Direction.getLeftDirection(incoming));
        if (spec.contains("S")) allowed.add(Direction.getOppositeDirection(incoming));
        if (spec.contains("R")) allowed.add(Direction.getRightDirection(incoming));

        return new Lane(allowed, new TrafficLight(isDirectional));
    }
}