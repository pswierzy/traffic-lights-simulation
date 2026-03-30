package pl.trafficapp.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.trafficapp.domain.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrafficManagerTest {
    private Intersection intersection;
    private TrafficManager trafficManager;
    private Lane northStraightLane;
    private Lane southStraightLane;

    @BeforeEach
    void setUp() {
        intersection = createTestIntersection();
        trafficManager = new TrafficManager(intersection);

        northStraightLane = intersection.getRoad(Direction.NORTH).getLanes().get(1);
        southStraightLane = intersection.getRoad(Direction.SOUTH).getLanes().get(1);
    }

    @Test
    void processTraffic_VehicleGoesStraightOnGreen() {
        Vehicle car = new Vehicle("V1", Direction.NORTH, Direction.SOUTH);
        intersection.addVehicle(car);
        northStraightLane.getTrafficLight().setColor(LightColor.GREEN);

        List<String> leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V1"));
        assertTrue(northStraightLane.isEmpty());
    }

    @Test
    void processTraffic_VehicleStopsOnRed() {
        Vehicle car = new Vehicle("V1", Direction.NORTH, Direction.SOUTH);
        intersection.addVehicle(car);
        northStraightLane.getTrafficLight().setColor(LightColor.RED);

        List<String> leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.isEmpty());
        assertFalse(northStraightLane.isEmpty());
    }

    @Test
    void processTraffic_RightTurnWithGreenArrow_StopsFirstThenGoes() {
        Vehicle car = new Vehicle("V1", Direction.NORTH, Direction.WEST);
        intersection.addVehicle(car);
        northStraightLane.getTrafficLight().setColor(LightColor.GREEN_ARROW_RIGHT);

        List<String> leftStep1 = trafficManager.processTraffic();
        assertTrue(leftStep1.isEmpty());
        assertTrue(car.hasStopped());

        List<String> leftStep2 = trafficManager.processTraffic();
        assertTrue(leftStep2.contains("V1"));
        assertTrue(northStraightLane.isEmpty());
    }

    @Test
    void processTraffic_RightTurnWaitsForStraightFromLeft() {
        Vehicle turningRightCar = new Vehicle("V1", Direction.NORTH, Direction.WEST);
        intersection.addVehicle(turningRightCar);
        northStraightLane.getTrafficLight().setColor(LightColor.GREEN_ARROW_RIGHT);

        Lane eastStraightLane = intersection.getRoad(Direction.EAST).getLanes().get(1);
        Vehicle straightCar1 = new Vehicle("V2", Direction.EAST, Direction.WEST);
        Vehicle straightCar2 = new Vehicle("V3", Direction.EAST, Direction.WEST);

        intersection.addVehicle(straightCar1);
        intersection.addVehicle(straightCar2);

        eastStraightLane.getTrafficLight().setColor(LightColor.GREEN);

        List<String> leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V2"));
        assertFalse(leftVehicles.contains("V1"));
        assertFalse(northStraightLane.isEmpty());

        leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V3"));
        assertFalse(leftVehicles.contains("V1"));
        assertFalse(northStraightLane.isEmpty());

        leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V1"));
        assertTrue(northStraightLane.isEmpty());
    }

    @Test
    void processTraffic_LeftTurnWaitsForOppositeStraightVehicle() {
        Lane northLeftLane = intersection.getRoad(Direction.NORTH).getLanes().get(0);

        Vehicle turningCar = new Vehicle("V1", Direction.NORTH, Direction.EAST);
        intersection.addVehicle(turningCar);
        northLeftLane.getTrafficLight().setColor(LightColor.GREEN);

        Vehicle straightCar = new Vehicle("V2", Direction.SOUTH, Direction.NORTH);
        intersection.addVehicle(straightCar);
        southStraightLane.getTrafficLight().setColor(LightColor.GREEN);

        List<String> leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V2"));
        assertFalse(leftVehicles.contains("V1"));
        assertFalse(northLeftLane.isEmpty());

        leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V1"));
        assertTrue(northLeftLane.isEmpty());
    }

    @Test
    void processTraffic_LeftTurnWaitsForOppositeRightTurningVehicle() {
        Lane northLeftLane = intersection.getRoad(Direction.NORTH).getLanes().get(0);

        Vehicle turningCar = new Vehicle("V1", Direction.NORTH, Direction.EAST);
        intersection.addVehicle(turningCar);
        northLeftLane.getTrafficLight().setColor(LightColor.GREEN);

        Vehicle oppositeCar = new Vehicle("V2", Direction.SOUTH, Direction.EAST);
        intersection.addVehicle(oppositeCar);
        southStraightLane.getTrafficLight().setColor(LightColor.GREEN);

        List<String> leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V2"));
        assertFalse(leftVehicles.contains("V1"));
        assertFalse(northLeftLane.isEmpty());

        leftVehicles = trafficManager.processTraffic();

        assertTrue(leftVehicles.contains("V1"));
        assertTrue(northLeftLane.isEmpty());
    }

    private Intersection createTestIntersection() {
        return new Intersection(Arrays.asList(
                createRoad(Direction.NORTH),
                createRoad(Direction.SOUTH),
                createRoad(Direction.EAST),
                createRoad(Direction.WEST)
        ));
    }

    private Road createRoad(Direction dir) {
        Lane leftLane = new Lane(new HashSet<>(Arrays.asList(Direction.getLeftDirection(dir))), new TrafficLight());
        Lane straightRightLane = new Lane(new HashSet<>(Arrays.asList(
                Direction.getOppositeDirection(dir), Direction.getRightDirection(dir)
        )), new TrafficLight());
        return new Road(dir, Arrays.asList(leftLane, straightRightLane));
    }
}