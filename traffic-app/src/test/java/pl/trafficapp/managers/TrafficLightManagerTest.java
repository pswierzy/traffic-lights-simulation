package pl.trafficapp.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.trafficapp.domain.*;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightManagerTest {

    private Intersection intersection;
    private TrafficLightManager lightManager;
    private Lane northStraightRight;
    private Lane eastLeft;
    private Lane eastStraightRight;

    @BeforeEach
    void setUp() {
        intersection = createTestIntersection();
        lightManager = new TrafficLightManager(intersection);

        northStraightRight = intersection.getRoad(Direction.NORTH).getLanes().get(1);
        eastLeft = intersection.getRoad(Direction.EAST).getLanes().get(0);
        eastStraightRight = intersection.getRoad(Direction.EAST).getLanes().get(1);
    }

    @Test
    void tick_StateCycleTiming_TransitionsProperly() {
        lightManager.tick(); // RED_ALL -> RED_YELLOW
        System.out.println(northStraightRight.getTrafficLight().getColor());
        assertEquals(LightColor.YELLOW, eastLeft.getTrafficLight().getColor());

        lightManager.tick(); // RED_YELLOW -> GREEN
        assertEquals(LightColor.GREEN_ARROW_LEFT, eastLeft.getTrafficLight().getColor());

        lightManager.tick(); // GREEN

        lightManager.tick(); // GREEN -> YELLOW
        assertEquals(LightColor.YELLOW, eastLeft.getTrafficLight().getColor());

        lightManager.tick(); // YELLOW -> RED_ALL
        assertEquals(LightColor.RED, eastLeft.getTrafficLight().getColor());
    }

    @Test
    void getNextPhase_PrioritizesLanesWithLongestQueue() {
        for (int i = 0; i < 10; i++) {
            intersection.addVehicle(new Vehicle("V" + i, Direction.EAST, Direction.WEST));
        }

        lightManager.tick(); // RED_ALL -> RED_YELLOW
        lightManager.tick(); // RED_YELLOW -> GREEN
        lightManager.tick(); // GREEN
        lightManager.tick(); // GREEN
        lightManager.tick(); // GREEN -> YELLOW
        lightManager.tick(); // YELLOW -> RED_ALL

        lightManager.tick(); // RED_ALL -> RED_YELLOW

        assertEquals(LightColor.YELLOW, eastStraightRight.getTrafficLight().getColor());
    }

    @Test
    void calculateGreenDuration_ScalesWithQueueLength() {
        for (int i = 0; i < 16; i++) {
            intersection.addVehicle(new Vehicle("V" + i, Direction.NORTH, Direction.SOUTH));
        }

        lightManager.tick(); // RED_ALL -> RED_YELLOW
        lightManager.tick(); // RED_YELLOW -> GREEN

        assertEquals(LightColor.GREEN, northStraightRight.getTrafficLight().getColor());

        for (int i = 0; i < 7; i++) {
            lightManager.tick(); // GREEN
        }
        assertEquals(LightColor.GREEN, northStraightRight.getTrafficLight().getColor());

        lightManager.tick(); // GREEN -> YELLOW
        assertEquals(LightColor.YELLOW, northStraightRight.getTrafficLight().getColor());
    }

    @Test
    void calculateGreenDuration_RespectsMinAndMaxBoundaries() {
        for (int i = 0; i < 30; i++) {
            intersection.addVehicle(new Vehicle("V" + i, Direction.NORTH, Direction.SOUTH));
        }

        lightManager.tick(); // RED_ALL -> RED_YELLOW
        lightManager.tick(); // RED_YELLOW -> GREEN

        for (int i = 0; i < TrafficLightManager.MAX_GREEN_LENGTH - 1; i++) {
            lightManager.tick(); // GREEN
        }
        assertEquals(LightColor.GREEN, northStraightRight.getTrafficLight().getColor());

        lightManager.tick(); // GREEN -> YELLOW
        assertEquals(LightColor.YELLOW, northStraightRight.getTrafficLight().getColor());
    }

    @Test
    void applyColors_ManagesRightGreenArrowsProperly() {
        lightManager.tick(); // RED_ALL -> RED_YELLOW
        lightManager.tick(); // RED_YELLOW -> GREEN

        assertEquals(LightColor.GREEN_ARROW_RIGHT, northStraightRight.getTrafficLight().getColor());

        lightManager.tick(); // GREEN
        lightManager.tick(); // GREEN
        lightManager.tick(); // GREEN -> YELLOW

        assertEquals(LightColor.RED, northStraightRight.getTrafficLight().getColor());
    }

    @Test
    void getNextPhase_PreventsStarvationByAccumulatingWaitTicks() {
        intersection.addVehicle(new Vehicle("V_ALONE", Direction.EAST, Direction.SOUTH));

        for (int i = 0; i < 3; i++) {
            intersection.addVehicle(new Vehicle("V_EW_" + i, Direction.EAST, Direction.WEST));
        }

        lightManager.tick(); // RED_ALL -> RED_YELLOW
        lightManager.tick(); // RED_YELLOW -> GREEN

        assertEquals(LightColor.GREEN, eastStraightRight.getTrafficLight().getColor());

        lightManager.tick(); // GREEN
        lightManager.tick(); // GREEN -> YELLOW
        lightManager.tick(); // YELLOW -> RED_ALL

        for (int i = 0; i < 4; i++) {
            intersection.addVehicle(new Vehicle("V_NS_" + i, Direction.NORTH, Direction.SOUTH));
        }
        lightManager.tick(); // RED_ALL -> RED_YELLOW

        assertEquals(LightColor.YELLOW, eastLeft.getTrafficLight().getColor());
        assertEquals(LightColor.RED, northStraightRight.getTrafficLight().getColor());
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
        Lane leftLane = new Lane(new HashSet<>(Arrays.asList(Direction.getLeftDirection(dir))), new TrafficLight(true));
        Lane straightRightLane = new Lane(new HashSet<>(Arrays.asList(
                Direction.getOppositeDirection(dir), Direction.getRightDirection(dir)
        )), new TrafficLight(false));
        return new Road(dir, Arrays.asList(leftLane, straightRightLane));
    }
}