package pl.trafficapp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    @Test
    void vehicleInitialization() {
        Vehicle vehicle = new Vehicle("V1", Direction.NORTH, Direction.SOUTH);

        assertEquals("V1", vehicle.id());
        assertEquals(Direction.NORTH, vehicle.start());
        assertEquals(Direction.SOUTH, vehicle.end());
        assertFalse(vehicle.hasStopped());
    }
}