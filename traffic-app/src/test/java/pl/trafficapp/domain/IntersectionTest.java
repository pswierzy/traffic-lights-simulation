package pl.trafficapp.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    private Road createValidRoad(Direction incoming) {
        Lane lane = new Lane(Set.of(
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
        ), new TrafficLight());
        return new Road(incoming, List.of(lane));
    }

    @Test
    void isValid_FourValidRoads_ReturnsTrue() {
        List<Road> roads = List.of(
                createValidRoad(Direction.NORTH),
                createValidRoad(Direction.SOUTH),
                createValidRoad(Direction.EAST),
                createValidRoad(Direction.WEST)
        );
        Intersection intersection = new Intersection(roads);
        assertTrue(intersection.isValid());
    }

    @Test
    void isValid_MissingRoad_ReturnsFalse() {
        List<Road> roads = List.of(
                createValidRoad(Direction.NORTH),
                createValidRoad(Direction.SOUTH),
                createValidRoad(Direction.EAST)
        );
        Intersection intersection = new Intersection(roads);
        assertFalse(intersection.isValid());
    }

    @Test
    void addVehicle_RoutesToCorrectRoad() {
        Road northRoad = createValidRoad(Direction.NORTH);
        Road southRoad = createValidRoad(Direction.SOUTH);

        Intersection intersection = new Intersection(List.of(
                northRoad, southRoad, createValidRoad(Direction.EAST), createValidRoad(Direction.WEST)
        ));

        Vehicle vehicle = new Vehicle("V1", Direction.NORTH, Direction.SOUTH);
        assertTrue(intersection.addVehicle(vehicle));

        assertEquals(1, northRoad.getLanes().get(0).getQueueSize());
        assertEquals(0, southRoad.getLanes().get(0).getQueueSize());
    }

}