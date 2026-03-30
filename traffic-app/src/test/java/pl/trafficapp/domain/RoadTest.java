package pl.trafficapp.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {

    @Test
    void isValid_AllDirectionsCovered_ReturnsTrue() {
        Lane straightRight = new Lane(Set.of(Direction.SOUTH, Direction.WEST), new TrafficLight());
        Lane left = new Lane(Set.of(Direction.EAST), new TrafficLight(true));

        Road road = new Road(Direction.NORTH, List.of(straightRight, left));
        assertTrue(road.isValid());
    }

    @Test
    void isValid_MissingDirection_ReturnsFalse() {
        Lane straightOnly = new Lane(Set.of(Direction.SOUTH), new TrafficLight());

        Road road = new Road(Direction.NORTH, List.of(straightOnly));
        assertFalse(road.isValid());
    }

    @Test
    void isValid_DirectionalLeftTurnAllowsOtherDirections_ReturnsFalse() {
        Lane leftAndStraight = new Lane(Set.of(Direction.EAST, Direction.SOUTH), new TrafficLight(true));
        Lane right = new Lane(Set.of(Direction.WEST), new TrafficLight());

        Road road = new Road(Direction.NORTH, List.of(leftAndStraight, right));
        assertFalse(road.isValid());
    }

    @Test
    void addVehicle_RoutesToShortestValidLane() {
        Lane lane1 = new Lane(Set.of(Direction.SOUTH), new TrafficLight());
        Lane lane2 = new Lane(Set.of(Direction.SOUTH), new TrafficLight());

        lane1.addVehicle(new Vehicle("V1", Direction.NORTH, Direction.SOUTH));

        Road road = new Road(Direction.NORTH, List.of(lane1, lane2));
        Vehicle newVehicle = new Vehicle("V2", Direction.NORTH, Direction.SOUTH);

        assertTrue(road.addVehicle(newVehicle));
        assertEquals(1, lane1.getQueueSize());
        assertEquals(1, lane2.getQueueSize());
    }
}