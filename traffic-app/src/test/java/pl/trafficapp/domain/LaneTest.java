package pl.trafficapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LaneTest {
    private Lane lane;
    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        lane = new Lane(Set.of(Direction.SOUTH, Direction.WEST), new TrafficLight());
        vehicle1 = new Vehicle("V1", Direction.NORTH, Direction.SOUTH);
        vehicle2 = new Vehicle("V2", Direction.NORTH, Direction.WEST);
    }

    @Test
    void isDirectionAllowed() {
        assertTrue(lane.isDirectionAllowed(Direction.SOUTH));
        assertFalse(lane.isDirectionAllowed(Direction.NORTH));
    }

    @Test
    void queueOperations() {
        assertTrue(lane.isEmpty());

        lane.addVehicle(vehicle1);
        lane.addVehicle(vehicle2);

        assertEquals(2, lane.getQueueSize());
        assertFalse(lane.isEmpty());

        assertEquals(vehicle1, lane.peekVehicle().orElse(null));
        assertEquals(vehicle1, lane.pollVehicle().orElse(null));

        assertEquals(1, lane.getQueueSize());
        assertEquals(vehicle2, lane.pollVehicle().orElse(null));
        assertTrue(lane.isEmpty());
    }
}