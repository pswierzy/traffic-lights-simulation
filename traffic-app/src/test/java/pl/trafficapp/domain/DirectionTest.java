package pl.trafficapp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void fromString_ValidInput() {
        assertEquals(Direction.NORTH, Direction.fromString("north"));
        assertEquals(Direction.SOUTH, Direction.fromString("SOUTH"));
    }

    @Test
    void fromString_InvalidInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Direction.fromString("invalid"));
    }

    @Test
    void getRightDirection_CalculatesCorrectly() {
        assertEquals(Direction.WEST, Direction.getRightDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, Direction.getRightDirection(Direction.WEST));
        assertEquals(Direction.EAST, Direction.getRightDirection(Direction.SOUTH));
        assertEquals(Direction.NORTH, Direction.getRightDirection(Direction.EAST));
    }

    @Test
    void getOppositeDirection_CalculatesCorrectly() {
        assertEquals(Direction.SOUTH, Direction.getOppositeDirection(Direction.NORTH));
        assertEquals(Direction.WEST, Direction.getOppositeDirection(Direction.EAST));
    }

    @Test
    void getLeftDirection_CalculatesCorrectly() {
        assertEquals(Direction.EAST, Direction.getLeftDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, Direction.getLeftDirection(Direction.EAST));
    }
}